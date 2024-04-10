package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

/**
 * @author lyk
 */
@Data
public class Item implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private String mflag;
    @JacksonXmlProperty(isAttribute = true)
    private String fnId;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String cmd;
    @JacksonXmlProperty(isAttribute = true)
    private String di;
    @JacksonXmlProperty(isAttribute = true)
    private String offset;
    @JacksonXmlProperty(isAttribute = true)
    private String len;
    @JacksonXmlProperty(isAttribute = true)
    private String dt;
    @JacksonXmlProperty(isAttribute = true)
    private String calc;
}
