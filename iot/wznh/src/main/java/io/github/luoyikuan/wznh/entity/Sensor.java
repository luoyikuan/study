package io.github.luoyikuan.wznh.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 传感器
 *
 * @author lyk
 */
@Data
public class Sensor implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 对接ID
     */
    private String djId;

    /**
     * 名称
     */
    private String name;

    /**
     * 对接编号
     */
    private String djNum14;

    /**
     * 对接编号
     */
    private String djNum12;

    /**
     * 分项用电
     */
    private String electricalType;
}
