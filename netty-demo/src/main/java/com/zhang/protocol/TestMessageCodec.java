package com.zhang.protocol;

import com.zhang.message.LoginRequestMessage;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @ClassName TestMessageCodec
 * @Description 测试
 * @Author ZPH
 * @Date 2022/10/31 16:17
 * @Version 1.0
 */
public class TestMessageCodec {
    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new LoggingHandler(),
                new MessageCodec());

        LoginRequestMessage requestMessage = new LoginRequestMessage("zhangSan", "123", "张三");
        embeddedChannel.writeOutbound(requestMessage);
    }
}
