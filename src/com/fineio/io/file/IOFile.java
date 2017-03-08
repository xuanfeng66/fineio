package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.cache.CacheManager;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.IOSetException;
import com.fineio.io.*;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public abstract class IOFile<E extends Buffer> {


    /**
     * 内部路径 key
     */
    protected URI uri;
    /**
     * 连接器
     */
    protected Connector connector;
    /**
     * 分多少块
     */
    protected int blocks;
    /**
     * 每块尺寸的大小的偏移量 2的N次方
     */
    protected byte block_size_offset;
    /**
     * 读的类型
     */
    private AbstractFileModel<E> model;
    /**
     * 单个block的大小
     */
    protected long single_block_len;
    protected volatile E[] buffers;


    IOFile(Connector connector, URI uri, AbstractFileModel<E> model) {
        if(uri == null || connector == null|| model == null){
            throw new IOSetException("uri  or connector or model can't be null");
        }
        this.connector = connector;
        this.uri = uri;
        this.model = model;
    }

    protected FileBlock createHeadBlock(){
        return new FileBlock(uri, FileConstants.HEAD);
    }

    /**
     * 注意所有写方法并不支持多线程操作，仅读的方法支持
     * @param size
     */
    protected final void createBufferArray(int size) {
        this.blocks = size;
        this.buffers = (E[]) new Buffer[size];
    }

    private boolean inRange(int index) {
        return buffers != null && buffers.length > index;
    }

    protected final int checkBuffer(int index) {
        if(index < 0){
            throw new BufferIndexOutOfBoundsException(index);
        }
        return inRange(index) ? index : createBufferArrayInRange(index);
    }

    private int createBufferArrayInRange(int index) {
        Buffer[] buffers = this.buffers;
        createBufferArray(index + 1);
        if(buffers != null){
            System.arraycopy(buffers, 0, this.buffers, 0, buffers.length);
        }
        return index;
    }

    private final int gi(long p) {
        return (int)(p >> block_size_offset);
    }

    private final int gi() {
        if(buffers == null || buffers.length == 0) {
            return 0;
        }
        int len = buffers.length - 1;
        return buffers[len].full() ? triggerWrite(len) : len;
    }

    private final int triggerWrite(int len) {
        buffers[len].write();
        return len + 1;
    }



    private final int gp(long p){
        return (int)(p & single_block_len);
    }


    private final E getBuffer(int index){
        return buffers[checkIndex(index)] != null ?  buffers[index] : initBuffer(index);
    }

    private int checkIndex(int index){
        if(index > -1 && index < blocks){
            return index;
        }
        throw new BufferIndexOutOfBoundsException(index);
    }

    private E initBuffer(int index) {
        synchronized (this){
            if(buffers[index] == null) {
                buffers[index] = createBuffer(index);
                CacheManager.getInstance().registerBuffer(buffers[index]);
            }
            return buffers[index];
        }
    }


    private E createBuffer(int index) {
        return model.createBuffer(connector, new FileBlock(uri, String.valueOf(index)), block_size_offset);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<DoubleBuffer> file, double d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<ByteBuffer> file,   byte d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<CharBuffer> file,  char d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<FloatBuffer> file,  float d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<LongBuffer> file,   long d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<IntBuffer> file, int d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<ShortBuffer> file,  short d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(  d);
    }


    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<DoubleBuffer> file, long p, double d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<ByteBuffer> file, long p, byte d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<CharBuffer> file, long p, char d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<FloatBuffer> file, long p, float d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<LongBuffer> file, long p, long d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<IntBuffer> file, long p, int d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<ShortBuffer> file, long p, short d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(IOFile<LongBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(IOFile<IntBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static int getChar(IOFile<CharBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(IOFile<DoubleBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(IOFile<FloatBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(IOFile<ByteBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }
    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(IOFile<ShortBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    private final static int HEAD_LEN = MemoryConstants.STEP_LONG + 1;

    protected void writeHeader() {
        FileBlock block = createHeadBlock();
        byte[] bytes = new byte[HEAD_LEN];
        Bits.putInt(bytes, 0, buffers.length);
        bytes[MemoryConstants.STEP_LONG] = (byte) (block_size_offset + model.offset());
        connector.write(block, bytes);
    }

    public void close() {
        writeHeader();
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i] != null) {
                buffers[i].force();
            }
        }
    }
}
