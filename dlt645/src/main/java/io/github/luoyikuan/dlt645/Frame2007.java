package io.github.luoyikuan.dlt645;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.FastByteArrayOutputStream;

import lombok.Builder;
import lombok.Data;

/**
 * DL/T645-2007 数据帧
 * <p>
 * 具体细节见 DLT645-2007 文档
 * https://www.biao-zhun.cn/13933.html
 * </p>
 * 
 * @author lyk
 */
@Data
public class Frame2007 {
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

    /**
     * 功能码 保留
     */
    public static final byte FUN_CODE_RETAIN = 0b00000;
    /**
     * 功能码 读数据
     */
    public static final byte FUN_CODE_READ = 0b10001;

    /**
     * 控制开关
     */
    public static final byte FUN_CODE_CONTROL = 0b11100;

    private String address;
    private Boolean master;
    private Boolean ok;
    private byte funCode;

    private byte start0;
    private byte[] a = new byte[6];
    private byte start1;
    private byte c;
    private byte l;
    private byte[] data = new byte[0];
    private byte cs;
    private byte end;

    private Frame2007() {
    }

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
        for (int i = 0; i < this.a.length; i++) {
            code += this.a[i] & 0xFF;
        }
        code += FLAG_START & 0xFF;
        code += this.c & 0xFF;
        code += this.l & 0xFF;
        if (this.l != 0) {
            for (int i = 0; i < this.data.length; i++) {
                code += this.data[i] & 0xFF;
            }
        }
        return (byte) (code & 0xFF);
    }

    public static Frame2007 generateReadFrame(String address, Register register) {
        Frame2007 frame = new Frame2007();
        frame.setAddress(address);
        frame.setFunCode(FUN_CODE_READ);
        frame.setMaster(true);
        frame.setOk(true);
        frame.setL((byte) 0x04);
        frame.setData(register.getValue());
        return frame;
    }

    /**
     * 控制闭合
     *
     * @param address
     * @param close   时候闭合
     * @return
     */
    public static Frame2007 generateControlFrame(String address, boolean close) {
        Frame2007 frame = new Frame2007();
        frame.setAddress(address);
        frame.setFunCode(FUN_CODE_CONTROL);
        frame.setMaster(true);
        frame.setOk(true);

        frame.setL((byte) 0x10);

        if (close) {
            frame.setData(new byte[] {
                    (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x84, (byte) 0x3F, (byte) 0x05, (byte) 0x69,
                    (byte) 0x1B, (byte) 0x00, (byte) 0x01, (byte) 0x01,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x30
            });
        } else {
            frame.setData(new byte[] {
                    (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x84, (byte) 0x3F, (byte) 0x05, (byte) 0x69,
                    (byte) 0x1A, (byte) 0x00, (byte) 0x01, (byte) 0x01,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x30
            });
        }

        return frame;
    }

    /**
     * 编码
     *
     * @return
     */
    public byte[] encoded() throws IOException {
        this.start0 = FLAG_START;
        this.start1 = FLAG_START;
        this.end = FLAG_END;

        if (this.address.length() != 12) {
            throw new RuntimeException("地址长度必须是12个字符");
        }

        for (int i = 0; i < this.a.length; i++) {
            int strEndIndex = this.a.length - i << 1;
            String str = this.address.substring(strEndIndex - 2, strEndIndex);
            this.a[i] = (byte) (Integer.valueOf(str, 16) & 0xFF);
        }

        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (byte) (((this.data[i] & 0xFF) + 0x33) & 0xFF);
        }

        if (!this.master) {
            this.c |= 0b10000000;
        }

        if (!this.ok) {
            this.c |= 0b01000000;
        }

        this.c |= funCode;

        this.cs = check();

        FastByteArrayOutputStream buf = new FastByteArrayOutputStream();

        buf.write(new byte[] { PREFIX, PREFIX, PREFIX, PREFIX });// 前导字节
        buf.write(this.start0);
        buf.write(this.a);
        buf.write(this.start1);
        buf.write(this.c);
        buf.write(this.l);
        buf.write(this.data);
        buf.write(this.cs);
        buf.write(this.end);

        return buf.toByteArray();
    }

    @Data
    @Builder
    public static class ReadResult {
        /**
         * 寄存器
         */
        private Register register;
        /**
         * 值
         */
        private String value;
    }

    public ReadResult getReadResult() {
        Register register = Register.getRegister(this.data);

        StringBuilder vsb = new StringBuilder();
        for (int i = this.data.length - 1; i >= 0; i--) {
            byte b = this.data[i];
            vsb.append(b >>> 4 & 0x0F);
            vsb.append(b & 0x0F);
        }
        String value = vsb.substring(0, vsb.length() - 8);
        return ReadResult.builder()
                .register(register)
                .value(value)
                .build();
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

        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (byte) (((this.data[i] & 0xFF) - 0x33) & 0xFF);
        }

        // ------- 传送方向 -------
        this.master = this.c >>> 7 == 0;

        // ------- 从站应答标志 -------
        this.ok = (this.c >>> 6 & 0x1) == 0;

        // 功能码
        this.funCode = (byte) (this.c & 0b00011111);
    }

    /**
     * 解码
     *
     * @param raw
     * @return
     */
    public static List<Frame2007> decodeRaw(byte[] raw) {
        List<Frame2007> result = new ArrayList<>(1);

        ByteIterator bi = new ByteIterator(raw);
        while (bi.hasNext()) {
            Frame2007 item = new Frame2007();

            // ------- 帧起始符0 -------
            for (int i = 0; i < 6; i++) {
                if (i == 5) {
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

            // ------- 地址域 -------
            for (int i = 0; i < item.a.length; i++) {
                item.a[i] = bi.next();
            }

            // ------- 帧起始符1 -------
            item.start1 = bi.next();
            if (item.start1 != FLAG_START) {
                throw new RuntimeException("帧起始符1不为 0x68");
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
}
