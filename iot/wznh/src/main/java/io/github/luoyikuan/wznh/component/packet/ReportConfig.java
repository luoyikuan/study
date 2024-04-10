package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.github.luoyikuan.wznh.component.enums.CommonType;
import lombok.Data;

/**
 * @author lyk
 */
@Data
public class ReportConfig implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private CommonType operation;

    @JacksonXmlProperty(localName = "report_ack")
    private String reportAck;
}
