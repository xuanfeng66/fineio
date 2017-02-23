package com.fineio.io.read;

import com.fineio.file.FileBlock;
import com.fineio.file.ReadModel;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class CharReadBuffer extends ReadBuffer implements CharBuffer {

    public static final ReadModel MODEL = new ReadModel<CharBuffer>() {

        @Override
        protected final CharReadBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new CharReadBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private CharReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final char get(int p) {
        checkIndex(p);
        return MemoryUtils.getChar(address, p);
    }
}
