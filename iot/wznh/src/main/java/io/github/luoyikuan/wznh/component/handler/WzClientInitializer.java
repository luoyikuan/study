package io.github.luoyikuan.wznh.component.handler;

import java.util.concurrent.TimeUnit;

import io.github.luoyikuan.wznh.component.codec.WzCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 管道初始化程序
 *
 * @author lyk
 */
public class WzClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        var pipeline = ch.pipeline();

        // 配置空闲状态
        pipeline.addFirst(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
        // 配置解码器
        pipeline.addLast(new WzCodec());
        // 配置消息入站处理器
        pipeline.addLast(new WzClientChannelInboundHandler());
    }

}
