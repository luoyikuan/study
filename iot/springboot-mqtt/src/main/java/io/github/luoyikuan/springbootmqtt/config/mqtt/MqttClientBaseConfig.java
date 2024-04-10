package io.github.luoyikuan.springbootmqtt.config.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.util.StringUtils;

/**
 * 配置 mqtt 客户端 公用部分
 *
 * @author lyk
 */
@Configuration
public class MqttClientBaseConfig {

    @Autowired
    private MqttServerConfig mqttServerConfig;

    /**
     * mqtt 客户端工厂
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { mqttServerConfig.getHost() });

        if (StringUtils.hasText(mqttServerConfig.getUsername())) {
            options.setUserName(mqttServerConfig.getUsername());
        }

        if (StringUtils.hasText(mqttServerConfig.getPassword())) {
            options.setPassword(mqttServerConfig.getPassword().toCharArray());
        }

        options.setAutomaticReconnect(true);

        factory.setConnectionOptions(options);
        return factory;
    }

}
