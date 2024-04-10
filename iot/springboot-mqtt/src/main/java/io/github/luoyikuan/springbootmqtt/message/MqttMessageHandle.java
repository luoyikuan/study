package io.github.luoyikuan.springbootmqtt.message;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * mqtt 消息处理器
 *
 * @author lyk
 */
@Slf4j
@Component
public class MqttMessageHandle implements MessageHandler {

    @Override
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        log.info("MQTT head = {}", message.getHeaders());
        log.info("MQTT payload= {}", message.getPayload());
    }

}
