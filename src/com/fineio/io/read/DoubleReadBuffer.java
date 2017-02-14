package com.fineio.io.read;

import com.fineio.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public class DoubleReadBuffer extends ByteReadBuffer {
    public DoubleReadBuffer(Connector connector, FileBlock block) {
        super(connector, block);
    }

    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_DOUBLE;
    }

    public final double getDouble(int p) {
        checkIndex(p);
        return MemoryUtils.getDouble(address, p);
    }
}
