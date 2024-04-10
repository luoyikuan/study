package io.github.luoyikuan.cjt188;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.FastByteArrayOutputStream;

import lombok.Data;

/**
 * CJ/T188-2004 数据帧
 * 
 * <p>
 * 具体细节见 CJ/T188-2004 文档
 * http://www.down.bzko.com/download1/20091103CJ/CJT1882004.rar
 * </p>
 *
 * @author lyk
 */
@Data
public class Frame2004 {
    private Frame2004() {
    }

    /**
     * 前导字节
     */
    public static final byte PREFIX = (byte) 0xFE;

    /**
     * 帧起始符
     */
    public static final byte FLAG_START = (byte) 0x68;

    /**
     * 结束符
     */
    public static final byte FLAG_END = (byte) 0x16;

    public static Frame2004 generateReadCountFrame(String address) {
        Frame2004 frame = new Frame2004();
        frame.setAddress(address);
        frame.setCmd(Cmd.CTR_0);
        frame.data = new byte[frame.cmd.dataFlag.length + 1];
        frame.l = (byte) frame.data.length;
        for (int i = 0; i < frame.cmd.dataFlag.length; i++) {
            frame.data[i] = frame.cmd.dataFlag[i];
        }
        frame.data[frame.data.length - 1] = (byte) 0x00;
        return frame;
    }

    public static Frame2004 generateSetTap(String address, boolean open) {
        Frame2004 frame = new Frame2004();
        frame.setAddress(address);
        frame.setCmd(Cmd.CTR_5);
        frame.data = new byte[frame.cmd.dataFlag.length + 2];
        frame.l = (byte) frame.data.length;
        for (int i = 0; i < frame.cmd.dataFlag.length; i++) {
            frame.data[i] = frame.cmd.dataFlag[i];
        }
        frame.data[frame.data.length - 2] = (byte) 0x00;
        frame.data[frame.data.length - 1] = open ? (byte) 0x55 : (byte) 0x99;
        return frame;
    }

    /**
     * 编码
     *
     * @return
     */
    public byte[] encoded() throws IOException {
        this.start0 = FLAG_START;
        this.end = FLAG_END;

        // String addr = "00000000000000" + this.address;
        // addr = addr.substring(addr.length() - 14);
        if (this.address.length() != 14) {
            throw new RuntimeException("地址必须未14位");
        }

        for (int i = 0; i < this.a.length; i++) {
            int strEndIndex = this.a.length - i << 1;
            String str = this.address.substring(strEndIndex - 2, strEndIndex);
            this.a[i] = (byte) (Integer.valueOf(str, 16) & 0xFF);
        }

        this.c = cmd.getC();
        this.cs = check();

        FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
        buf.write(new byte[] { PREFIX, PREFIX, PREFIX });// 前导字节
        buf.write(this.start0);
        buf.write(this.t);
        buf.write(this.a);
        buf.write(this.c);
        buf.write(this.l);
        buf.write(this.data);
        buf.write(this.cs);
        buf.write(this.end);

        return buf.toByteArray();
    }

    public static List<Frame2004> decodeRaw(byte[] raw) {
        List<Frame2004> result = new ArrayList<>(1);
        ByteIterator bi = new ByteIterator(raw);
        while (bi.hasNext()) {
            Frame2004 item = new Frame2004();

            // ------- 帧起始符0 -------
            for (int i = 0; i < 5; i++) {
                if (i == 4) {
                    throw new RuntimeException("帧起始符错误");
                }
                item.start0 = bi.next();
                if (item.start0 == PREFIX) {
                    continue;
                } else if (item.start0 == FLAG_START) {
                    break;
                } else {
                    throw new RuntimeException("帧起始符0不为 0x68");
                }
            }

            // ------ 表计类型代码 ------
            item.t = bi.next();

            // ------- 地址域 -------
            for (int i = 0; i < item.a.length; i++) {
                item.a[i] = bi.next();
            }

            // ------- 控制码 -------
            item.c = bi.next();

            // ------- 数据域长度 -------
            item.l = bi.next();

            // ------- 数据域 -------
            int len = item.l & 0xFF;
            if (len > 0) {
                item.data = new byte[len];
                for (int i = 0; i < len; i++) {
                    item.data[i] = bi.next();
                }
            }

            // ------- 校验码 -------
            item.cs = bi.next();
            byte code = item.check();
            if (item.cs != code) {
                throw new RuntimeException("数据校验失败");
            }

            // ------- 结束符 -------
            item.cs = bi.next();
            if (item.cs != FLAG_END) {
                throw new RuntimeException("帧起始符不为 0x16");
            }

            item.decode();
            result.add(item);
        }

        return result;
    }

    public void decode() {
        // ------- 解码地址 -------
        StringBuilder addrSb = new StringBuilder();
        for (int i = this.a.length - 1; i >= 0; i--) {
            byte b = this.a[i];
            addrSb.append(b >>> 4 & 0x0F);
            addrSb.append(b & 0x0F);
        }
        this.address = addrSb.toString();

        for (Cmd cc : Cmd.values()) {
            if (cc.c == this.c) {
                this.cmd = cc;
                break;
            }
        }
        switch (this.cmd) {
            case CTR_1:
                StringBuilder vsb = new StringBuilder();
                for (int i = this.data.length - 16; i >= this.data.length - 19; i--) {
                    byte b = this.data[i];
                    vsb.append(b >>> 4 & 0x0F);
                    vsb.append(b & 0x0F);
                    if (i == this.data.length - 18) {
                        vsb.append('.');
                    }
                }
                this.waterVolume = vsb.toString();
                break;
        }

        byte t = (byte) (this.data[this.data.length - 2] & 0b00000011);
        if (t == (byte) 0b00) {
            this.tap = "开";
        } else if (t == (byte) 0b10) {
            this.tap = "关";
        } else if (t == (byte) 0b01) {
            this.tap = "控制中";
        } else if (t == (byte) 0b11) {
            this.tap = "异常";
        }
    }

    private String address;
    private Cmd cmd;

    private String waterVolume;

    private String tap;

    private byte start0;
    private byte t = (byte) 0x10;
    private byte[] a = new byte[7];
    private byte c;
    private byte l;
    private byte[] data = new byte[0];
    private byte cs;
    private byte end;

    /**
     * 字节迭代器
     */
    private static final class ByteIterator {
        private byte[] bytes;

        private int pos = 0;

        public ByteIterator(byte[] bytes) {
            this.bytes = bytes;
        }

        public boolean hasNext() {
            return this.pos < this.bytes.length;
        }

        public byte next() {
            if (hasNext()) {
                return this.bytes[pos++];
            } else {
                throw new RuntimeException("报文长度不对");
            }
        }
    }

    public byte check() {
        int code = 0;
        code += FLAG_START & 0xFF;
        code += t & 0xFF;
        for (int i = 0; i < this.a.length; i++) {
            code += this.a[i] & 0xFF;
        }
        code += this.c & 0xFF;
        code += this.l & 0xFF;
        if (this.l != 0) {
            for (int i = 0; i < this.data.length; i++) {
                code += this.data[i] & 0xFF;
            }
        }
        return (byte) (code & 0xFF);
    }
}
