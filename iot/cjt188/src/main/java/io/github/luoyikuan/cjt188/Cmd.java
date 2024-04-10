package io.github.luoyikuan.cjt188;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 命令
 * 
 * @author lyk
 */
@Getter
@AllArgsConstructor
public enum Cmd {
    CTR_0((byte) 0x01, "表计数据:服务器->表计", new byte[] { (byte) 0x90, (byte) 0x1F }),
    CTR_1((byte) 0x81, "表计数据:表计->服务器", new byte[] { (byte) 0x90, (byte) 0x1F }),

    CTR_5((byte) 0x2A, "写阀门控制:服务器->表计", new byte[] { (byte) 0xA0, (byte) 0x17 }),
    CTR_6((byte) 0xA5, "写阀门控制:表计->服务器", new byte[] { (byte) 0xA0, (byte) 0x17 });

    public final byte c;
    public final String name;
    public final byte[] dataFlag;
}
