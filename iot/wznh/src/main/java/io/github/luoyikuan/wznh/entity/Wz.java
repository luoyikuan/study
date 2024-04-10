package io.github.luoyikuan.wznh.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 温州市民用建筑能耗信息管理系统(信息)
 *
 * @author lyk
 */
@Data
public class Wz implements Serializable {
    /**
     * 主机
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * 建筑物
     */
    private String buildingId;
    /**
     * 网关
     */
    private String gatewayId;
    /**
     * 网关名称(建筑物名称)
     */
    private String gatewayName;
}
