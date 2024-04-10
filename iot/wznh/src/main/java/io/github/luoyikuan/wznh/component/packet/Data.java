package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.github.luoyikuan.wznh.component.enums.CommonType;

/**
 * @author lyk
 */
@lombok.Data
public class Data implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private CommonType operation;
    private String sequence;
    private String parse;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private Date time;
    private Meter meter;
}
