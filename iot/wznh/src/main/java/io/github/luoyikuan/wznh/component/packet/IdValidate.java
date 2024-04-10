package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.github.luoyikuan.wznh.component.enums.CommonType;
import lombok.Data;

/**
 * @author 准点下班
 */
@Data
public class IdValidate implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private CommonType operation;

    private String sequence;

    private String md5;

    private String result;
}
