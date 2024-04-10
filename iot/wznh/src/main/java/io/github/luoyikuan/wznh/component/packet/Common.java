package io.github.luoyikuan.wznh.component.packet;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.github.luoyikuan.wznh.component.enums.CommonType;
import lombok.Data;

/**
 * @author 准点下班
 * @date 2023/6/16 12:52
 */
@Data
public class Common implements Serializable {

    @JacksonXmlProperty(localName = "building_id")
    private String buildingId;

    @JacksonXmlProperty(localName = "gateway_id")
    private String gatewayId;

    private CommonType type;
}

