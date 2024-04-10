package io.github.luoyikuan.dlt645;

import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DLT645Tests {

    /**
     * 编码测试
     */
    @Test
    public void encodedTest() throws Exception {
        // 查询电量报文
        Frame2007 energyFrame = Frame2007.generateReadFrame("223002000066", Register.ENERGY);
        byte[] energy = energyFrame.encoded();

        // 查询A相电压报文
        Frame2007 aVoltageFrame = Frame2007.generateReadFrame("223002000066", Register.VOLTAGE_A);
        byte[] aVoltage = aVoltageFrame.encoded();

        // 查询频率报文
        Frame2007 frequencyFrame = Frame2007.generateReadFrame("223002000066", Register.FREQUENCY);
        byte[] frequency = frequencyFrame.encoded();

        /* 其他查询内容定义在Register枚举类 */

        // 控制台打印
        HexFormat hf = HexFormat.of();
        System.out.println(hf.formatHex(energy));
        System.out.println(hf.formatHex(aVoltage));
        System.out.println(hf.formatHex(frequency));
    }

    /**
     * 解码测试
     */
    @Test
    public void Test() throws Exception {
        // 控制成功报文
        byte[] control = { (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0x68, (byte) 0x66, (byte) 0x00,
                (byte) 0x00, (byte) 0x02, (byte) 0x30, (byte) 0x22, (byte) 0x68, (byte) 0x9C, (byte) 0x00, (byte) 0x26,
                (byte) 0x16 };

        // java定义字节数组太麻烦了,这里使用16进制字符串表示
        HexFormat hf = HexFormat.of();

        // 用电量报文
        byte[] energy = hf.parseHex("FE FE FE FE 68 66 00 00 02 30 22 68 91 08 33 33 33 33 B4 54 33 33 5D 16".replace(" ", ""));

        // 电压报文
        byte[] voltage = hf.parseHex("FE FE FE FE 68 66 00 00 02 30 22 68 91 06 33 34 34 35 83 56 CA 16".replace(" ", ""));


        // 解码并解码后的内容放入list
        List<Frame2007> message = new LinkedList<>();
        message.addAll(Frame2007.decodeRaw(control));
        message.addAll(Frame2007.decodeRaw(energy));
        message.addAll(Frame2007.decodeRaw(voltage));

        for (Frame2007 frame : message) {
            System.out.println("Address: " + frame.getAddress());
            System.out.println("Master: " + frame.getMaster());
            System.out.print("Fun: ");
            switch (frame.getFunCode()) {
                case Frame2007.FUN_CODE_READ:
                    System.out.print("read");
                    System.out.print(" - ");
                    System.out.print(frame.getReadResult());

                    // 如果是电闸状态报文
                    if (frame.getReadResult().getRegister() == Register.ELECTRIC_BRAKE) {
                        int b = Integer.parseInt(frame.getReadResult().getValue().toString(), 16);
                        b &= 0xFF;
                        b >>>= 4;
                        b &= 0x01;
                        System.out.print(" - switch " + (b == 0));
                    }

                    break;
                case Frame2007.FUN_CODE_CONTROL:
                    System.out.print("control");
                    break;
            }
            System.out.println("\n-----------------");
        }
    }
}
