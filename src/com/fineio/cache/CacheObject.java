package com.fineio.cache;

/**
 * Created by daniel on 2017/3/1.
 */
public class CacheObject<T> {

    private long t = System.currentTimeMillis();

    private T value;

    private CacheObject<T> last;

    private CacheObject<T> next;

    CacheObject<T> getLast(){
        return last;
    }

    CacheObject<T> getNext(){
        return next;
    }


    void setLast(CacheObject<T> last){
        this.last = last;
    }

    void setNext(CacheObject<T> next ) {
        this.next = next;
        if(next != null){
            next.setLast(this);
        }
    }


    public void updateTime(){
        t = System.currentTimeMillis();
    }

    public CacheObject(T value){
        this.value = value;
    }

    public T get() {
        return value;
    }

    public long getIdle() {
        return System.currentTimeMillis() - t;
    }
}
