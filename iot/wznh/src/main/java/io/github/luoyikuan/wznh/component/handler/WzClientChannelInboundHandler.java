package io.github.luoyikuan.wznh.component.handler;

import java.util.Arrays;

import org.springframework.context.ApplicationContext;

import cn.hutool.extra.spring.SpringUtil;
import io.github.luoyikuan.wznh.component.WzClient;
import io.github.luoyikuan.wznh.component.packet.Root;
import io.github.luoyikuan.wznh.component.packet.WzBasePacket;
import io.github.luoyikuan.wznh.component.packet.WzPacketFactory;
import io.github.luoyikuan.wznh.util.XmlUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 管道消息入站处理器
 *
 * @author lyk
 */
@Slf4j
public class WzClientChannelInboundHandler extends SimpleChannelInboundHandler<WzBasePacket> {
    private final WzPacketFactory wzPacketFactory;
    private final ApplicationContext applicationContext;

    public WzClientChannelInboundHandler() {
        wzPacketFactory = SpringUtil.getBean(WzPacketFactory.class);
        applicationContext = SpringUtil.getApplicationContext();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush(wzPacketFactory.requestValidate());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent ise && ise.state() == IdleState.WRITER_IDLE) {
            ctx.channel().writeAndFlush(wzPacketFactory.requestNotify());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        var wzClient = applicationContext.getBean(WzClient.class);
        log.info("掉线重连 [温州市民用建筑能耗信息管理系统]");
        wzClient.connect();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WzBasePacket msg) throws Exception {
        // 解密xml内容
        var xmlBytes = wzPacketFactory.AES.decrypt(Arrays.copyOfRange(msg.getData(), 4, msg.getData().length));
        var xml = new String(xmlBytes, wzPacketFactory.GB_2312);

        if (log.isDebugEnabled()) {
            log.debug("收到内容 [温州市民用建筑能耗信息管理系统]\n{}", xml);
        }

        // 把xml反序列化成Root对象
        var root = XmlUtils.readValue(xml, Root.class);

        // 处理不同的命令
        var res = switch (root.getCommon().getType()) {
            case SEQUENCE -> handleSequence(root);
            case RESULT -> handleResult(root);
            case DEVICE_ACK -> handleDeviceAck(root);
            case ARCHIVES -> handleArchives(root);
            case TIME -> handleTime(root);
            case REPORT_ACK -> handleReportAck(root);
            default -> null;
        };

        if (res != null) {
            // 复制序列
            res.getData()[0] = msg.getData()[0];
            res.getData()[1] = msg.getData()[1];
            res.getData()[2] = msg.getData()[2];
            res.getData()[3] = msg.getData()[3];

            // 计算CRC
            res.setCrc(res.calcCrc());

            // 发送到服务器
            ctx.writeAndFlush(res);
        }
    }

    private WzBasePacket handleSequence(Root root) {
        var sequence = root.getIdValidate().getSequence();
        return wzPacketFactory.requestMd5(sequence);
    }

    private WzBasePacket handleResult(Root root) {
        WzBasePacket wzp = null;
        var result = root.getIdValidate().getResult();
        if ("pass".equals(result)) {
            wzp = wzPacketFactory.requestDevice();
            log.info("验证成功 [温州市民用建筑能耗信息管理系统]");
        } else {
            log.warn("验证失败 [温州市民用建筑能耗信息管理系统]");
        }

        return wzp;
    }

    private WzBasePacket handleDeviceAck(Root root) {
        var deviceAck = root.getDevice().getDeviceAck();
        if ("pass".equals(deviceAck)) {
            log.info("设备成功 [温州市民用建筑能耗信息管理系统]");
        } else {
            log.warn("设备失败 [温州市民用建筑能耗信息管理系统]");
        }
        return null;
    }

    private WzBasePacket handleTime(Root root) {
        log.debug("心跳回应 [温州市民用建筑能耗信息管理系统]");
        return null;
    }

    private WzBasePacket handleArchives(Root root) {
        return wzPacketFactory.requestArchives();
    }

    private WzBasePacket handleReportAck(Root root) {
        var reportAck = root.getReportConfig().getReportAck();
        if ("pass".equals(reportAck)) {
            log.info("上报成功 [温州市民用建筑能耗信息管理系统]");
        } else {
            log.warn("上报失败 [温州市民用建筑能耗信息管理系统]");
        }

        return null;
    }

}
