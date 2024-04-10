package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

/**
 * @author lyk
 */
@Data
public class Meter implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private String meterId;
    @JacksonXmlProperty(isAttribute = true)
    private String addr;
    @JacksonXmlProperty(isAttribute = true, localName = "mType")
    private String mType;
    @JacksonXmlProperty(isAttribute = true)
    private String com;
    @JacksonXmlProperty(isAttribute = true)
    private String comType;
    @JacksonXmlProperty(isAttribute = true, localName = "tUnit")
    private String tUnit;
    @JacksonXmlProperty(isAttribute = true)
    private String code;
    @JacksonXmlProperty(isAttribute = true)
    private String ct;
    @JacksonXmlProperty(isAttribute = true)
    private String pt;
    @JacksonXmlProperty(isAttribute = true)
    private String tp;
    @JacksonXmlProperty(isAttribute = true)
    private String memo;
    @JacksonXmlProperty(isAttribute = true)
    private String sampleId;
    @JacksonXmlProperty(isAttribute = true)
    private String name;

    private Function function;
}
