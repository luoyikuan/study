package io.github.luoyikuan.wznh.component;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import io.github.luoyikuan.wznh.component.handler.WzClientInitializer;
import io.github.luoyikuan.wznh.config.WzConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 温州市民用建筑能耗 客户端
 *
 * @author lyk
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WzClient {
    /**
     * 温州市民用建筑能耗信息管理系统(信息)配置消息
     */
    private final WzConfig config;

    /**
     * 客户端启动器
     */
    private final Bootstrap bootstrap = new Bootstrap();

    /**
     * 客户端消息循坏组
     */
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * 客户端管道
     */
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    /**
     * 初始化客户端(连接)
     */
    @PostConstruct
    public void init() {
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new WzClientInitializer());

        log.info("正在连接 [温州市民用建筑能耗信息管理系统]");
        connect();
    }

    /**
     * 销毁客户端
     */
    @PreDestroy
    public void shutdown() {
        log.info("正在关闭 [温州市民用建筑能耗信息管理系统]");
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
    }

    /**
     * 连接到[温州市民用建筑能耗信息管理系统]
     * <p>
     * 失败会自动重连
     * </p>
     */
    @SneakyThrows
    public void connect() {
        var cf = bootstrap.connect(config.getHost(), config.getPort());

        cf.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("成功连接 [温州市民用建筑能耗信息管理系统]");
                channel = future.channel();
            } else {
                log.info("重新连接 [温州市民用建筑能耗信息管理系统]");
                future
                        .channel()
                        .eventLoop()
                        .schedule(this::connect, 1, TimeUnit.SECONDS);
            }
        });
    }
}
