package io.github.luoyikuan.wznh;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import cn.hutool.extra.spring.SpringUtil;
import io.github.luoyikuan.wznh.component.WzClient;
import io.github.luoyikuan.wznh.component.packet.WzPacketFactory;
import io.github.luoyikuan.wznh.entity.SensorLog;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 程序入口
 * 
 * @author lyk
 */
@Slf4j
@SpringBootApplication
@Import(cn.hutool.extra.spring.SpringUtil.class)
public class WznhApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(WznhApplication.class, args);

		// 10秒后模拟上报电量操作
		Thread.sleep(1000 * 10);
		Thread.ofVirtual().start(() -> {
			WzClient client = SpringUtil.getBean(WzClient.class);
			WzPacketFactory packetFactory = SpringUtil.getBean(WzPacketFactory.class);

			Channel channel = client.getChannel();

			if (channel == null || !channel.isActive()) {
				return;
			}

			var sensorLog = new SensorLog();
			sensorLog.setSensorId(1L); // 与配置文件的ID字段对应
			sensorLog.setValueName("电量");
			sensorLog.setValue("1024"); // 电表读数
			sensorLog.setLogTime(new Date());

			log.info("上报数据 {}", sensorLog);
			var data = packetFactory.requestReport(sensorLog);
			channel.writeAndFlush(data);
		});

	}

}
