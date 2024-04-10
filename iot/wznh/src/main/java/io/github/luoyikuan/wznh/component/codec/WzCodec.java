package io.github.luoyikuan.wznh.component.codec;

import java.util.List;

import cn.hutool.core.util.HexUtil;
import io.github.luoyikuan.wznh.component.packet.WzBasePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.extern.slf4j.Slf4j;

/**
 * [温州市民用建筑能耗信息管理系统数据采集器上行传输通信协议]编解码器
 *
 * @author lyk
 */
@Slf4j
public class WzCodec extends ByteToMessageCodec<WzBasePacket> {

    /**
     * 编码
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, WzBasePacket msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getHead());
        out.writeIntLE(msg.getLen());
        out.writeBytes(msg.getData());
        out.writeShortLE(msg.getCrc());
        out.writeBytes(msg.getFoot());
    }

    /**
     * 解码
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!canDecode(in)) {
            return;
        }

        var wzp = new WzBasePacket();

        // ---------- 包头 ----------
        in.readBytes(wzp.getHead());
        if (!wzp.checkHead()) {
            log.warn("包头错误 0x{}", HexUtil.encodeHexStr(wzp.getHead()));
            throw new DecoderException("包头错误");
        }

        // ---------- 长度 ----------
        int len = in.readIntLE();
        wzp.setLen(len);

        // ---------- 数据 ----------
        // 可读字节数 >= 长度 + CRC + 包尾 = len + 2 + 4 = len + 6
        if (in.readableBytes() < len + 6) {
            in.resetReaderIndex();
            return;
        }
        wzp.setData(new byte[len]);
        in.readBytes(wzp.getData());

        // ---------- CRC ----------
        wzp.setCrc(in.readUnsignedShortLE());
        if (!wzp.checkCrc()) {
            log.warn("CRC错误 0x{} 0x{}", HexUtil.toHex(wzp.getCrc()), HexUtil.encodeHexStr(wzp.getData()));
            throw new DecoderException("CRC错误");
        }

        // ---------- 包尾 ----------
        in.readBytes(wzp.getFoot());
        if (!wzp.checkFoot()) {
            log.warn("包尾错误 0x{}", HexUtil.encodeHexStr(wzp.getFoot()));
            throw new DecoderException("包尾错误");
        }

        out.add(wzp);
    }

    private boolean canDecode(ByteBuf in) {
        // 可读字节数 >= 包头 + 长度 + CRC + 包尾 = 4 + 4 + 2 + 4 = 14
        if (in.readableBytes() < 14) {
            return false;
        }

        // 可读字节数 >= 包头 + 长度 + CRC + 包尾 = 4 + len + 2 + 4 = len + 10
        if (in.readableBytes() < in.getIntLE(4) + 10) {
            return false;
        }

        return true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.warn("编解码错误", cause);
    }

}
