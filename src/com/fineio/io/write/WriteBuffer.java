package com.fineio.io.write;

import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.StreamCloseException;
import com.fineio.io.file.FileBlock;
import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/15.
 * 注意 写是连续的并且不支持并发操作哦，写操作也是在byte全部被赋值的情况下才支持，目前writeBuffer仅支持到这样的程度
 * writeBuffer虽然提供了随机写的接口，但是实际上只支持连续写入
 * EditBuffer可以支持随机写入,并且写之后不再更改
 * 写入方法均不支持多线程
 */
public abstract class WriteBuffer extends AbstractBuffer implements Write {

    //1024
    public static final int DEFAULT_CAPACITY_OFFSET = 10;

    protected int current_max_size;

    protected int current_max_offset = DEFAULT_CAPACITY_OFFSET;

    protected int max_offset;

    protected int max_position = -1;

    protected volatile boolean flushed = false;

    protected volatile boolean changed = false;


    public boolean hasChanged() {
        return changed;
    }

    /**
     * 如果没写过或者写过了又更改了都需要重新写
     * @return
     */
    public boolean needFlush() {
        return !flushed || changed;
    }

    protected void checkIndex(int p) {
        if (ir(p)){
            return;
        }
        throw new BufferIndexOutOfBoundsException(p);
    }

    public boolean full() {
        return max_position  == max_size - 1;
    }

    /**
     *对于child edit来说 如果没改变是不用写文件的，就不会创建outputstream
     * @return
     */
    public final int getByteSize() {
        return (max_position + 1) << getLengthOffset();
    }

    protected final boolean ir(int p){
        return p > -1 && p < current_max_size;
    }

    protected WriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block);
        this.max_offset = max_offset;
        this.max_size = 1 << max_offset;
    }

    protected final void setCurrentCapacity(int offset) {
        this.current_max_offset = offset;
        this.current_max_size = 1 << offset;
    }


    protected void ensureCapacity(int position){
        if(position < max_size) {
            addCapacity(position);
            changed = true;
        } else {
            throw new BufferIndexOutOfBoundsException(position);
        }
    }

    private final void setMaxPosition(int position) {
        access();
        if(position > max_position ){
            max_position = position;
        }
    }

    protected final void addCapacity(int position) {
        while ( position >= current_max_size){
            addCapacity();
        }
        setMaxPosition(position);
    }

    private final void addCapacity() {
        int len = this.current_max_size << getLengthOffset();
        setCurrentCapacity(this.current_max_offset + 1);
        int newLen = this.current_max_size << getLengthOffset();
        beforeStatusChange();
        //todo 预防内存设置超大 fill的时候发生溢出
        this.address = CacheManager.getInstance().allocateWrite(address, len, newLen);
        MemoryUtils.fill0(this.address + len, newLen - len);
        afterStatusChange();
    }

    public void force() {
        while (needFlush()) {
            SyncManager.getInstance().force(createWriteJob());
        }
    }

    protected JobAssist createWriteJob() {
        return new JobAssist(bufferKey, new Job() {
            public void doJob() {
                try {
                    write0();
                } catch (StreamCloseException e){
                    flushed = false;
                    write();
                }
            }
        });
    }

    public void write() {

        SyncManager.getInstance().triggerWork(createWriteJob());

    }

    /**
     * 在clear的时候关闭
     */
    protected void closeDuringClear(){
        close = true;
        this.max_size = 0;
    }

    public void clear(){
        synchronized (this) {
            closeDuringClear();
            this.current_max_size = 0;
            super.clear();
        }
    }

    protected void write0(){
        synchronized (this) {
            changed = false;
            bufferKey.getConnector().write(bufferKey.getBlock(), getInputStream());
            flushed = true;
            clear();
        }
    }


    public LEVEL getLevel() {
        return LEVEL.WRITE;
    }

}
