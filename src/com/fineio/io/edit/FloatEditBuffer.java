package com.fineio.io.edit;

import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.io.FloatBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class FloatEditBuffer extends EditBuffer implements FloatBuffer{


    public static final EditModel MODEL = new EditModel<FloatBuffer>() {

        protected final FloatEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new FloatEditBuffer(connector, block, max_offset);
        }

        @Override
        public final FloatEditBuffer createBuffer(Connector connector, URI uri) {
            return new FloatEditBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private FloatEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private FloatEditBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final float get(int p) {
        checkIndex(p);
        return MemoryUtils.getFloat(address, p);
    }

    public final void put(float b) {
        put(++max_position, b);
    }
    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final  void put(int position, float b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private final void judeChange(int position, float b) {
        if(!changed) {
            if(Float.compare(b, get(position)) != 0){
                changed = true;
            }
        }
    }
}
