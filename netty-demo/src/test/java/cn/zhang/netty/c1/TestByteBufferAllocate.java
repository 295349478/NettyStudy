package cn.zhang.netty.c1;

import java.nio.ByteBuffer;

/**
 * @ClassName TestByteBufferAllocate
 * @Description 测试ByteBuffer系列中获取内存的方法
 * @Author ZPH
 * @Date 2022/9/3 16:17
 * @Version 1.0
 */
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());

        /**
         * class java.nio.HeapByteBuffer   --使用Java 的堆内存，1、读写效率较低，2、收GC影响，3、分配速度效率高
         * class java.nio.DirectByteBuffer --使用直接内存，2、读写效率搞（少一次拷贝），2、不受GC影响，3、分配速度效率低，可能会存在内存泄露
         */
    }
}
