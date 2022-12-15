package com.gj.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jason Li
 * @date 2022/12/15
 * @apiNote
 **/
@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            IdleState state = event.state();
            switch (state) {
                case READER_IDLE:
                case WRITER_IDLE:
                case ALL_IDLE:
                    String channelId = ctx.channel().id().asShortText();
                    log.info("Channel " + channelId + " timeout and closed");
                    //final ByteBuf time = ctx.alloc().buffer(4); // (2)
                    //time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
                    ctx.writeAndFlush("timeout").addListener(ChannelFutureListener.CLOSE_ON_FAILURE) ;
                    break;
                default:
                    break;
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
