package com.gj.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

/**
 * Socket 初始化器
 * @author Gjing
 **/
@Component
public class SocketInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 心跳检测
        pipeline.addLast(new IdleStateHandler(30, 0, 0));
        pipeline.addLast(new HeartbeatHandler());
        // 添加对byte数组的编解码，netty提供了很多编解码器，你们可以根据需要选择
        pipeline.addLast(new ByteArrayDecoder());
        pipeline.addLast(new ByteArrayEncoder());
        // 最后添加上自己的处理器
        pipeline.addLast(new SocketHandler());
    }
}
