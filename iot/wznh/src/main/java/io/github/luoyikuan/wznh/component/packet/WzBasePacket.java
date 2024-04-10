package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import cn.hutool.core.io.checksum.crc16.CRC16XModem;
import lombok.Data;

/**
 * @author lyk
 */
@Data
public class WzBasePacket implements Serializable {

    private static long sequence = 0L;

    public static long generateSequence() {
        if (sequence > 0xFFFFFFFFL) {
            sequence = 0L;
        }
        return sequence++;
    }

    public static final byte H1 = (byte) 0x55;
    public static final byte H2 = (byte) 0xAA;
    public static final byte H3 = (byte) 0x55;
    public static final byte H4 = (byte) 0xAA;

    public static final byte F1 = (byte) 0x68;
    public static final byte F2 = (byte) 0x68;
    public static final byte F3 = (byte) 0x16;
    public static final byte F4 = (byte) 0x16;

    /**
     * 包头
     */
    private final byte[] head = { H1, H2, H3, H4 };

    /**
     * 有效数据总长度
     */
    private int len;

    /**
     * 有效数据
     */
    private byte[] data;

    /**
     * CRC校验
     */
    private int crc;

    /**
     * 包尾
     */
    private final byte[] foot = { F1, F2, F3, F4 };

    /**
     * 检查包头
     *
     * @return
     */
    public boolean checkHead() {
        return head[0] == H1 && head[1] == H2 && head[2] == H3 && head[3] == H4;
    }

    /**
     * 检查包尾
     *
     * @return
     */
    public boolean checkFoot() {
        return foot[0] == F1 && foot[1] == F2 && foot[2] == F3 && foot[3] == F4;
    }

    /**
     * 检查CRC
     *
     * @return
     */
    public boolean checkCrc() {
        return calcCrc() == crc;
    }

    public int calcCrc() {
        CRC16XModem crc16XModem = new CRC16XModem();
        crc16XModem.update(this.data);
        return (int) (crc16XModem.getValue() & 0xFFFF);
    }

    public static WzBasePacket generatePacket(byte[] data) {
        WzBasePacket wzp = new WzBasePacket();

        wzp.setLen(data.length + 4);
        wzp.data = new byte[wzp.len];

        long seq = generateSequence();
        wzp.data[0] = (byte) (seq & 0xFF);
        wzp.data[1] = (byte) (seq >>> 8 & 0xFF);
        wzp.data[2] = (byte) (seq >>> 16 & 0xFF);
        wzp.data[3] = (byte) (seq >>> 24 & 0xFF);
        System.arraycopy(data, 0, wzp.data, 4, data.length);

        CRC16XModem crc16XModem = new CRC16XModem();
        crc16XModem.update(wzp.data);
        wzp.crc = wzp.calcCrc();

        return wzp;
    }

}
