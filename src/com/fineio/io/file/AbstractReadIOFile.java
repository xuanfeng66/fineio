package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by daniel on 2017/2/20.
 */
abstract class AbstractReadIOFile<T extends Buffer> extends IOFile<T> {


    AbstractReadIOFile(Connector connector, URI uri, AbstractFileModel<T> model) {
        super(connector, uri, model);
        readHeader(model.offset());
    }


    private void readHeader(byte offset) {
        try {
            InputStream is  = this.connector.read(createHeadBlock());
            if(is == null){
                //throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
            }
            byte[] header = new byte[9];
            is.read(header);
            int p = 0;
            createBufferArray(Bits.getInt(header, p));
            //先空个long的位置
            p += MemoryConstants.STEP_LONG;
            block_size_offset = (byte) (header[p] - offset);
        } catch (Throwable e) {
           // throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
            this.block_size_offset = (byte) (connector.getBlockOffset() - offset);
        }
        single_block_len = (1L << block_size_offset) - 1;
    }
}
