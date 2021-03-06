package com.fineio.storage;

import com.fineio.io.file.FileBlock;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daniel on 2017/2/9.
 * 存储对接接口
 */
public interface Connector {

    /**
     * 读整块的方法
     * @param file
     * @return
     */
    InputStream read(FileBlock file) throws IOException;

    /**
     * 写整快的方法，可以保证通一个块不被同时写
     * @param file
     * @param inputStream
     */
    void write(FileBlock file, InputStream inputStream)  throws IOException;

    /*
    输出byte[]
     */
    void write(FileBlock file, byte[] bytes)  throws IOException;

    /**
     * 删除块
     * @param block
     * @return
     */
    boolean delete(FileBlock block);

    /**
     * 写文件时单个块的最大size偏移量
     * 用1L << value 表示单个块的最大尺寸，不建议超过28 （256M） 不建议小于22 (4M)
     * 可以根据磁盘的读写能力控制这个值的大小介于12-31之间
     * 不支持小于12 4K
     * 不支持大于31 2G
     * @return
     */
    byte getBlockOffset();

}
