package com.fineio.io.write;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface FloatWriteBuffer extends WriteOnlyBuffer {
    void put(int pos, float value);

    void put(float value);
}