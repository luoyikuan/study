package io.github.luoyikuan.wznh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.github.luoyikuan.wznh.entity.Wz;

/**
 * 温州市民用建筑能耗信息管理系统(信息)配置
 *
 * @author lyk
 */
@Component
@ConfigurationProperties(prefix = "wz")
public class WzConfig extends Wz {

}
