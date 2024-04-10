package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.github.luoyikuan.wznh.component.enums.CommonType;
import lombok.Data;

/**
 * @author lyk
 */
@Data
public class Device implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private CommonType operation;

    @JacksonXmlProperty(localName = "build_name")
    private String buildName;

    @JacksonXmlProperty(localName = "build_no")
    private String buildNo;

    @JacksonXmlProperty(localName = "dev_no")
    private String devNo;

    private String factory;
    private String hardware;
    private String software;
    private String mac;
    private String ip;
    private String mask;
    private String gate;
    private String server;
    private String port;
    private String host;
    private String com;

    @JacksonXmlProperty(localName = "dev_num")
    private String devNum;

    private String period;

    @JacksonXmlProperty(localName = "begin_time")
    private String beginTime;

    private String address;

    @JacksonXmlProperty(localName = "device_ack")
    private String deviceAck;
}
