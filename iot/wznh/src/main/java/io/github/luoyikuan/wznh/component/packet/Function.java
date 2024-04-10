package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import lombok.Data;

/**
 * @author lyk
 */
@Data
public class Function implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private String coding;
    @JacksonXmlProperty(isAttribute = true)
    private String error;
    @JacksonXmlText
    private String value;
}
