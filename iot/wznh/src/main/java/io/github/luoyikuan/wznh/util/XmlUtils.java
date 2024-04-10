package io.github.luoyikuan.wznh.util;

import java.util.TimeZone;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * XML工具类
 *
 * @author lyk
 */
@Slf4j
public final class XmlUtils {

    // 全局单例,直接注入spring容器会与json解析冲突
    private static final XmlMapper XML_MAPPER;

    static {
        XML_MAPPER = XmlMapper.xmlBuilder()
                .configure(SerializationFeature.INDENT_OUTPUT, log.isDebugEnabled())
                .defaultTimeZone(TimeZone.getTimeZone("GMT+8"))
                .build();
    }

    /**
     * 私有化构造器
     */
    private XmlUtils() {
        throw new AssertionError("No XmlUtils instances for you!");
    }

    /**
     * 序列化
     *
     * @param value 对象
     * @return
     */
    @SneakyThrows
    public static String writeValueAsString(Object value) {
        return XML_MAPPER.writeValueAsString(value);
    }

    /**
     * 反序列化
     *
     * @param content   xml文本
     * @param valueType 类型
     * @param <T>
     * @return 对象
     */
    @SneakyThrows
    public static <T> T readValue(String content, Class<T> valueType) {
        return XML_MAPPER.readValue(content, valueType);
    }
}
