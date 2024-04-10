package io.github.luoyikuan.springbootmqtt.config;

import java.util.stream.Stream;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.ResourceHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/*
 * 原生编译配置
 * 
 * @author lyk
 */
@Configuration
@ImportRuntimeHints(NativeConfig.MqttHints.class)
public class NativeConfig {

    static class MqttHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            ReflectionHints reflectionHints = hints.reflection();
            Stream.of(
                    "org.eclipse.paho.client.mqttv3.logging.JSR47Logger",
                    "org.eclipse.paho.mqttv5.client.logging.JSR47Logger")
                    .forEach(type -> reflectionHints.registerTypeIfPresent(classLoader, type,
                            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS));

            ResourceHints resources = hints.resources();
            Stream.of(
                    "org/eclipse/paho/client/mqttv3/internal/nls/messages",
                    "org/eclipse/paho/client/mqttv3/internal/nls/logcat",
                    "org/eclipse/paho/mqttv5/common/nls/messages",
                    "org/eclipse/paho/mqttv5/client/internal/nls/logcat")
                    .forEach(resources::registerResourceBundle);
        }
    }
}
