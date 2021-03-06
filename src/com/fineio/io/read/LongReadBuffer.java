package com.fineio.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.io.LongBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class LongReadBuffer extends ReadBuffer implements LongBuffer{


    public static final ReadModel MODEL = new ReadModel<LongBuffer>() {

        @Override
        protected final LongReadBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new LongReadBuffer(connector, block, max_offset);
        }

        @Override
        public final LongReadBuffer  createBuffer(Connector connector, URI uri) {
            return new LongReadBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };


    private LongReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private LongReadBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final long get(int p) {
        checkIndex(p);
        return MemoryUtils.getLong(address, p);
    }
}
