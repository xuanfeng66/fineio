package com.fineio.test.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.read.IntReadBuffer;
import com.fineio.io.read.LongReadBuffer;
import com.fineio.storage.Connector;
import com.fineio.test.io.MemoryLeakTest;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public class FineReadIOFileTest extends TestCase {


    public void testConstruct() throws Exception{
        for(int i = 0 ;i < 100; i++){
            constructTest();
        }
        MemoryLeakTest.assertZeroMemory();
    }

    public void testException() throws Exception{
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Field head = FileConstants.class.getDeclaredField("HEAD");
        head.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, head.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andReturn(null).anyTimes();
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        control.replay();
        boolean exp = false;
        try {
            ReadIOFile.createFineIO(connector, u, ReadIOFile.LONG);
        } catch (BlockNotFoundException e){
            exp = true;
        }
        assertFalse(exp);
        MemoryLeakTest.assertZeroMemory();
    }

    public void constructTest() throws Exception {
        int len =  (int)(Math.random() * 100d);
        final byte[] res = new byte[16];
        Bits.putInt(res, 0, len * 2);
        res[8] =  (byte) len;
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("/");
        Field head = FileConstants.class.getDeclaredField("HEAD");
        head.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, head.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(res);
            }
        }).anyTimes();
        control.replay();
        u = new URI("");
        ReadIOFile<LongBuffer> file = ReadIOFile.createFineIO(connector, u, ReadIOFile.LONG);
        Field lenField = ReadIOFile.class.getSuperclass().getSuperclass().getDeclaredField("blocks");
        Field blockSizeField =  ReadIOFile.class.getSuperclass().getSuperclass().getDeclaredField("block_size_offset");
        lenField.setAccessible(true);
        blockSizeField.setAccessible(true);
        assertEquals(file.getPath(), "/");
        assertEquals(len * 2, ((Integer)lenField.get(file)).intValue());
        assertEquals(len - LongReadBuffer.OFFSET, ((Byte)blockSizeField.get(file)).byteValue());
        ReadIOFile<IntBuffer> ifile = ReadIOFile.createFineIO(connector, u, ReadIOFile.INT);
        lenField = ReadIOFile.class.getSuperclass().getSuperclass().getDeclaredField("blocks");
        blockSizeField =  ReadIOFile.class.getSuperclass().getSuperclass().getDeclaredField("block_size_offset");
        lenField.setAccessible(true);
        blockSizeField.setAccessible(true);
        assertEquals(ifile.getPath(), "/");
        assertEquals(len * 2, ((Integer)lenField.get(ifile)).intValue());
        assertEquals(len - IntReadBuffer.OFFSET, ((Byte)blockSizeField.get(ifile)).byteValue());
    }
}
