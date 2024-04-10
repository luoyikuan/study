package io.github.luoyikuan.cjt188;

import java.util.HexFormat;

import org.junit.jupiter.api.Test;

public class CJT188Tests {

    /**
     * 编码测试
     */
    @Test
    public void encodedTest() throws Exception {
        // 控制阀门报文
        Frame2004 controlFrame = Frame2004.generateSetTap("00000091113480", false);
        byte[] control = controlFrame.encoded();

        // 读用水量报文
        Frame2004 readFrame = Frame2004.generateReadCountFrame("00000091113480");
        byte[] read = readFrame.encoded();

        // 控制台打印
        HexFormat hf = HexFormat.of();
        System.out.println(hf.formatHex(control));
        System.out.println(hf.formatHex(read));
    }

    /**
     * 解码测试
     */
    @Test
    public void Test() throws Exception {
        // 报文
        byte[] message = HexFormat.of().parseHex("FE FE FE 68 10 80 34 11 91 00 00 00 A5 05 A0 17 00 00 FF 2E 16".replace(" ", ""));

        // 解码并打印
        Frame2004.decodeRaw(message).forEach(System.out::println);
    }
}
