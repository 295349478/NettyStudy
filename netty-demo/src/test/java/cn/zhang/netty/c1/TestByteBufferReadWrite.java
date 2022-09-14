package cn.zhang.netty.c1;

import java.nio.ByteBuffer;

import static cn.zhang.netty.ByteBufferUtil.debugAll;

/**
 * @ClassName TestByteBufferReadWrite
 * @Description 对工具类的测试
 * @Author ZPH
 * @Date 2022/9/3 15:57
 * @Version 1.0
 */
public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        //创建一个10个字节的缓存空间
        ByteBuffer buffer = ByteBuffer.allocate(10);
        //放入一个16进制的a
        buffer.put(new byte[]{0x61, 0x62, 0x63});
        debugAll(buffer);
        buffer.flip();
        System.out.println(buffer.get());
    }
}
