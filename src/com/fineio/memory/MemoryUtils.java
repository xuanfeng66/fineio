package com.fineio.memory;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by daniel on 2017/2/8.
 */
public final class MemoryUtils {

    private static Unsafe unsafe;


    static {
        try {
            Field f =Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
        }
    }

    private static final long arrayBaseOffset = (long)unsafe.arrayBaseOffset(byte[].class);

    /**
     * 复制数组对象的值到堆外内存葱address开始的地址
     * @param src
     * @param address
     * @param size
     */
    public static void copyMemory(byte[] src, long address, long size) {
        unsafe.copyMemory(src, arrayBaseOffset, null, address, size);
    }


    /**
     * 复制数组对象的值到堆外内存葱address开始的地址
     * @param src
     * @param address
     */
    public static void copyMemory(byte[] src, long address) {
        copyMemory(src, address, src.length);
    }

    private static Unsafe getUnsafe() {
        return  unsafe;
    }

    /**
     * 分配指定大小的内存
     * @param size
     * @return
     */
    public static long allocate(long size){
        return unsafe.allocateMemory(size);
    }

    /**
     * reallocate用法：参数1 老的地址
     * 参数2 新的总内存大小
     * 返回新的地址替换原来的地址
     * @param address
     * @param size
     * @return
     */
    public static long reallocate(long address, long size) {
        return unsafe.reallocateMemory(address, size);
    }

    /**
     * 释放内存的方法
     * @param s
     */
    public static void free(long s){
        unsafe.freeMemory(s);
    }

    /**
     * 给指定位置赋值
     * @param s
     * @param offset
     * @param b
     */
    public static void put(long s, long offset, byte b){
        unsafe.putByte(s + offset, b);
    }


    public static byte getByte(long s, int offset){
        return unsafe.getByte(s + offset);
    }

    public static int getInt(long s, long offset){
        return unsafe.getInt(s + (offset << MemoryConstants.OFFSET_INT));
    }

    public static void put(long s, long offset, int v){
        unsafe.putInt(s + (offset << MemoryConstants.OFFSET_INT), v);
    }

    public static long getLong(long s, long offset){
        return unsafe.getLong(s + (offset << MemoryConstants.OFFSET_LONG));
    }

    public static void put(long s, long offset, long v){
        unsafe.putLong(s + (offset << MemoryConstants.OFFSET_LONG), v);
    }

    public static char getChar(long s, long offset){
        return unsafe.getChar(s + (offset << MemoryConstants.OFFSET_CHAR));
    }

    public static void put(long s, long offset, char v){
        unsafe.putChar(s + (offset << MemoryConstants.OFFSET_CHAR), v);
    }

    public static short getShort(long s, long offset){
        return unsafe.getShort(s + (offset << MemoryConstants.OFFSET_SHORT));
    }

    public static void put(long s, long offset, short v){
        unsafe.putShort(s + (offset << MemoryConstants.OFFSET_SHORT), v);
    }

    public static float getFloat(long s, long offset){
        return unsafe.getFloat(s + (offset << MemoryConstants.OFFSET_FLOAT));
    }

    public static void put(long s, long offset, float v){
        unsafe.putFloat(s + (offset << MemoryConstants.OFFSET_FLOAT), v);
    }

    public final static double getDouble(long s, long offset){
        return unsafe.getDouble(s + (offset << MemoryConstants.OFFSET_DOUBLE));
    }

    public static void put(long s, long offset, double v){
        unsafe.putDouble(s + (offset << MemoryConstants.OFFSET_DOUBLE), v);
    }
}
