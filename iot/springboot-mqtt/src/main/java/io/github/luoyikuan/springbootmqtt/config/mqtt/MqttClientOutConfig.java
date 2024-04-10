package io.github.luoyikuan.springbootmqtt.config.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.StringUtils;

/**
 * 配置出站消息通道及相关处理Bean
 *
 * @author lyk
 */
@Configuration
public class MqttClientOutConfig {

    @Autowired
    private MqttServerConfig mqttServerConfig;

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(MqttPahoClientFactory mqttClientFactory) {
        String clientId = "mqtt-" + System.currentTimeMillis();
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(2);
        if (StringUtils.hasText(mqttServerConfig.getPub())) {
            messageHandler.setDefaultTopic(mqttServerConfig.getPub());
        }
        return messageHandler;
    }

}
