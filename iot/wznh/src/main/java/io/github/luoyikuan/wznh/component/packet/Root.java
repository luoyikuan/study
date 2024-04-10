package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author lyk
 */
@lombok.Data
@JacksonXmlRootElement(localName = "root")
public class Root implements Serializable {

    private Common common;

    @JacksonXmlProperty(localName = "id_validate")
    private IdValidate idValidate;

    private Device device;

    private Instruction instruction;

    @JacksonXmlProperty(localName = "heart_beat")
    private HeartBeat heartBeat;

    private Data data;

    @JacksonXmlProperty(localName = "report_config")
    private ReportConfig reportConfig;
}
