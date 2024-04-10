package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.github.luoyikuan.wznh.component.enums.CommonType;
import lombok.Data;

/**
 * @author lyk
 */
@Data
public class Instruction implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private String attr;

    @JacksonXmlProperty(isAttribute = true)
    private CommonType operation;

    @JacksonXmlProperty(localName = "build_info")
    private BuildInfo buildInfo;

    @JacksonXmlProperty(localName = "net_info")
    private NetInfo netInfo;

    @JacksonXmlElementWrapper(localName = "protocol_info")
    @JacksonXmlProperty(localName = "protocol")
    private List<Protocol> protocolInfo;

    @JacksonXmlElementWrapper(localName = "meter_info")
    @JacksonXmlProperty(localName = "meter")
    private List<Meter> meterInfo;
}
