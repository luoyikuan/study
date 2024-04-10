package io.github.luoyikuan.springbootmqtt.config.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * MQTT 服务器相关配置
 *
 * @author lyk
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttServerConfig {

    /**
     * mqtt 服务器地址
     */
    private String host;

    /**
     * mqtt 用户名
     */
    private String username;

    /**
     * mqtt 密码
     */
    private String password;

    /**
     * 订阅的主题
     */
    private String[] sub;

    /**
     * 默认发布主题
     */
    private String pub;
}
