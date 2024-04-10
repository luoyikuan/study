package io.github.luoyikuan.dlt645;

/**
 * 寄存器
 * 
 * @author lyk
 */
public enum Register {

    /**
     * 电量
     */
    ENERGY("00000000", "电量"),

    /**
     * A相电压
     */
    VOLTAGE_A("00010102", "A相电压"),
    /**
     * B相电压
     */
    VOLTAGE_B("00020102", "B相电压"),
    /**
     * C相电压
     */
    VOLTAGE_C("00030102", "C相电压"),

    /**
     * A相电流
     */
    CURRENT_A("00010202", "A相电流"),
    /**
     * B相电流
     */
    CURRENT_B("00020202", "B相电流"),
    /**
     * C相电流
     */
    CURRENT_C("00030202", "C相电流"),

    /**
     * 总功率
     */
    POWER("00000302", "总功率"),
    /**
     * A相功率
     */
    POWER_A("00010302", "A相功率"),
    /**
     * B相功率
     */
    POWER_B("00020302", "B相功率"),
    /**
     * C相功率
     */
    POWER_C("00030302", "C相功率"),
    /**
     * 零线电流
     */
    NEUTRAL_CURRENT("01008002", "零线电流"),
    /**
     * 频率
     */
    FREQUENCY("02008002", "频率"),
    /**
     * 表内温度
     */
    TEMPERATURE("07008002", "表内温度"),
    /**
     * 电闸
     */
    ELECTRIC_BRAKE("03050004", "电闸");


    Register(String addr, String name) {
        this.addr = addr;
        this.name = name;
    }

    public final String addr;
    public final String name;

    public static Register getRegister(byte[] data) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            byte b = data[i];
            sb.append(b >>> 4 & 0x0F);
            sb.append(b & 0x0F);
        }

        String a = sb.toString();
        Register result = null;
        for (int i = 0; i < values().length; i++) {
            if (values()[i].addr.equals(a)) {
                result = values()[i];
            }
        }
        return result;
    }

    public byte[] getValue() {
        byte[] val = new byte[4];
        for (int i = 0; i < val.length; i++) {
            int index = i << 1;
            String str = this.addr.substring(index, index + 2);
            val[i] = (byte) (Integer.valueOf(str, 16) & 0xFF);
        }
        return val;
    }
}
