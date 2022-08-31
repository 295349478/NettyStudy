package cn.zhang.netty.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author ZPH
 * @version V1.0
 * @Package cn.zhang.netty.c1
 * @date 2022/8/29 19:22
 */
@Slf4j
public class TestByBuffer {

    public static void main(String[] args) {
        //FileChannel
        //获取文件信道
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            //创建缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);

            while (true) {
                //将文件读到缓冲区，有返回值，返回实际读到的字节数，返回-1的时候没有值
                int readLen = channel.read(buffer);
                log.debug("信道读取到的字节数{}", readLen);
                if (readLen == -1){
                    break;
                }
                //从缓冲区中获取数据出来(读取之前需要将buffer改成读模式)
                buffer.flip();
                while (buffer.hasRemaining()){//查看是否缓冲区是否还有内容
                    byte b = buffer.get();
                    log.debug("实际字节是：{}", (char) b);
                }
                //将缓冲区切换写模式
                buffer.clear();
            }
        } catch (IOException e) {
        }


    }
}
