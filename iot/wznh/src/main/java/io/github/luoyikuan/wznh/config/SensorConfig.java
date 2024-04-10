package io.github.luoyikuan.wznh.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.github.luoyikuan.wznh.entity.Sensor;
import lombok.Data;

/**
 * 传感器配置
 *
 * @author lyk
 */
@Data
@Component
@ConfigurationProperties
public class SensorConfig {

    private List<Sensor> sensor;

}
