package com.zhang.protocol;

import com.zhang.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @ClassName MessageCodecSharable
 * @Description 专门用于Byte 转化成 Message（共享版）
 *              重要：必须放在粘包半包处理器 LengthFieldBasedFrameDecoder(1024,12,4,0,0) 之后，保证接收数据完整
 * @Author ZPH
 * @Date 2022/10/31 17:38
 * @Version 1.0
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();
        //1、魔数（4 字节）
        byteBuf.writeBytes(new byte[]{1,2,3,4});
        //2、版本（1 字节）
        byteBuf.writeByte(1);
        //3、序列化算法类型 0 JDK, 1 JSON（1 字节）
        byteBuf.writeByte(0);
        //4、指令类型（1 字节）
        byteBuf.writeByte(message.getMessageType());
        //5、请求序号（4 字节）
        byteBuf.writeInt(message.getSequenceId());

        //无意义，对齐字节
        byteBuf.writeByte(0xff);

        //6、正文长度（使用序列化将Java对象转成数组）
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(message);
        byte[] bytes = outputStream.toByteArray();
        byteBuf.writeInt(bytes.length);
        //7、写入正文内容
        byteBuf.writeBytes(bytes);

        //交给下一个Handler处理器
        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //1、读取魔数（4 字节）
        int magicNum = byteBuf.readInt();
        //2、读取版本（1 字节）
        byte version = byteBuf.readByte();
        //3、读取序列化算法类型 0 JDK, 1 JSON（1 字节）
        byte serializerType = byteBuf.readByte();
        //4、读取指令类型（1 字节）
        byte messageType = byteBuf.readByte();
        //5、读取请求序号（4 字节）
        int sequenceId = byteBuf.readInt();

        //无意义，读取对齐字节
        byteBuf.readByte();

        //6、读取正文长度（使用序列化将Java对象转成数组）
        int length = byteBuf.readInt();
        //7、读取写入正文内容
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes, 0, length);

        //根据序列化算法类型反序列化
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message)objectInputStream.readObject();

        log.debug("魔数：{}，版本：{}，序列化算法类型：{}，指令类型：{}，请求序号：{}，正文长度：{}",
                magicNum, version, serializerType, messageType, sequenceId, length);

        log.debug("正文：{}", message);

        //给下一个Handler使用
        list.add(message);
    }
}
