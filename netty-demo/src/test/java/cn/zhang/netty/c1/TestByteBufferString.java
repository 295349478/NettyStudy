package cn.zhang.netty.c1;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static cn.zhang.netty.c1.ByteBufferUtil.debugAll;

/**
 * @ClassName TestByteBufferString
 * @Description ByteBuffer与String的转换
 * @Author ZPH
 * @Date 2022/9/3 16:50
 * @Version 1.0
 */
public class TestByteBufferString {
    public static void main(String[] args) {
        // 1、字符串转为 ByteBuffer
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(16);
        byteBuffer1.put("hello".getBytes());
        debugAll(byteBuffer1);

        // 2、使用Charset，规定字符集以及放置字符串，直接创建一个内存（可以互转，但是Buffer必须是读模式）
        // 使用操作系统的默认字符集编码
//        Charset.defaultCharset()
        // 使用这个会自动切换至读模式
        ByteBuffer byteBuffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(byteBuffer2);

        // 3、wrap：直接创建一个内存将字节数组变成Buffer
        ByteBuffer byteBuffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(byteBuffer3);

    }
}
