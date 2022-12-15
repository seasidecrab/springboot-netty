package com.gj.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Gjing
 **/
@Slf4j
@Component
public class SocketServer {
    @Resource
    private SocketInitializer socketInitializer;

    @Getter
    private ServerBootstrap serverBootstrap;

    /**
     * netty服务监听端口
     */
    @Value("${netty.port:8088}")
    private int port;
    /**
     * 主线程组数量
     */
    @Value("${netty.bossThread:1}")
    private int bossThread;

    /**
     * 启动netty服务器
     */
    public void start() throws Exception {
        this.init();
    }

    /**
     * 初始化netty配置
     */
    private void init() throws Exception {
        // 创建两个线程组，bossGroup为接收请求的线程组，一般1-2个就行
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(this.bossThread);
        // 实际工作的线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 绑定线程池：handler是针对bossGroup，childHandler是针对workerHandler
            this.serverBootstrap = new ServerBootstrap();
            this.serverBootstrap.group(bossGroup, workerGroup) // 两个线程组加入进来
                    .channel(NioServerSocketChannel.class)  // 配置为nio类型
                    .childHandler(this.socketInitializer); // 加入自己的初始化器
            // 服务器异步创建绑定
            ChannelFuture f = this.serverBootstrap.bind(this.port).sync(); // 也可以将端口通过 bind 绑定
            log.info("Netty started on port: {} (TCP) with boss thread {}", this.port, this.bossThread);
            // 该方法进行阻塞,等待服务端链路关闭之后继续执行
            // 这种模式一般都是使用Netty模块主动向服务端发送请求，然后最后结束才使用
            f.channel().closeFuture().sync();
        } finally {
            // 释放线程池资源
            workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }
    }
}
