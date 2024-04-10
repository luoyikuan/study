package io.github.luoyikuan.wznh.component.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 命令枚举
 *
 * @author 准点下班
 */
@Getter
@AllArgsConstructor
public enum CommonType {
    REQUEST("request", "采集装置请求身份验证（数据采集装置发送）"),
    SEQUENCE("sequence", "数据中心发送一串随机序列（数据中心发送）"),
    MD5("md5", "采集装置发送计算的 MD5（数据采集装置发送）"),
    RESULT("result", "数据中心发送验证结果后发送授时信息（数据中心发送）"),

    DEVICE("device", "采集装置发送设备信息"),
    DEVICE_ACK("device_ack", "服务端受到后，发送接收设备成功的信息"),

    ARCHIVES("archives", "服务端主动发送 archive 信息，让设备进行初始化"),
    ARCHIVES_ACK("archives_ack", "采集设备收到指令后，发送 archive_ack 信息给服务端"),

    NOTIFY("notify", "等待上述步骤几秒钟后，采集装置定期给数据中心发送存活通知（数据采集装置发送）"),
    TIME("time", "数据中心在收到存活通知后发送应答信息（数据中心发送）"),

    QUERY("query", "设备验证及数据上报数据包:数据中心查询数据采集装置"),
    REPLY("reply", "设备验证及数据上报数据包:采集装置对数据中心查询的应答"),
    REPORT("report", "设备验证及数据上报数据包:采集装置定时上报的监测数据"),
    REPORT_ACK("report_ack", "定时监测数据应答包：由数据中心发送给采集装置");

    @JsonValue
    private String code;
    private String desc;
}
