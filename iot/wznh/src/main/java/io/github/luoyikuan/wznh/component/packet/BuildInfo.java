package io.github.luoyikuan.wznh.component.packet;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lyk
 */
@Data
public class BuildInfo implements Serializable {

    @JacksonXmlProperty(localName = "build_name")
    private String buildName;
}
