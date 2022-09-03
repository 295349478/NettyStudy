package cn.zhang.netty.c1;

import java.nio.ByteBuffer;

import static cn.zhang.netty.c1.ByteBufferUtil.debugAll;

/**
 * @ClassName TestByteBufferExam
 * @Description 网络上网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
 * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
 * Hello,world\n
 * I'm zhangsan\n
 * How are you?\n
 * 变成了下面的两个 byteBuffer (黏包，半包)
 * Hello,world\nI'm zhangsan\nHo
 * w are you?\n
 * 现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
 * @Author ZPH
 * @Date 2022/9/3 17:17
 * @Version 1.0
 */
public class TestByteBufferExam {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        //粘包解决
        split(buffer);
        //半包解决 
        buffer.put("w are you?\n".getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer buffer) {
        //切换值读模式
        buffer.flip();
        //使用get一个一个去看
        for (int i = 0; i < buffer.limit(); i++) {
            //找到换行符
            if (buffer.get(i) == '\n') {
                //拿到字符串的在原来buffer的存储位置
                int length = i + 1 - buffer.position();
                //创建新的Buffer做其他处理
                ByteBuffer byteBuffer = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    byteBuffer.put(buffer.get());
                }
                debugAll(byteBuffer);
            }
        }
        buffer.compact();


    }
}
