package io.github.luoyikuan.springbootmqtt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.luoyikuan.springbootmqtt.message.MqttMessagingGateway;

@SpringBootTest
class SpringbootMqttApplicationTests {

	@Autowired
	private MqttMessagingGateway mqttMessagingGateway;

	@Test
	void contextLoads() throws Exception {
		mqttMessagingGateway.sendToMqtt("123");

		// 等待一段时间:查看控制台接收到的消息
		Thread.sleep(1000 * 10);
	}

}
