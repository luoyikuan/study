package io.github.luoyikuan.wznh.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 传感器日志
 *
 * @author lyk
 */
@Data
public class SensorLog implements Serializable {
    private Long id;
    private Long sensorId;
    private Boolean success;
    private String valueName;
    private String value;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logTime;
    private String direction;
    private String remark;
}
