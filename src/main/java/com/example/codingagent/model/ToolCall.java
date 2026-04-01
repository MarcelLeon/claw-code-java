package com.example.codingagent.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 工具调用描述。
 */
public record ToolCall(
        String toolName,
        String argument,
        Map<String, Object> arguments
) {

    public ToolCall {
        arguments = arguments == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(arguments));
    }

    /**
     * 兼容旧协议中的纯字符串参数工具调用。
     *
     * @param toolName 工具名
     * @param argument 字符串参数
     */
    public ToolCall(String toolName, String argument) {
        this(toolName, argument, Map.of());
    }

    /**
     * 兼容新协议中的结构化参数工具调用。
     *
     * @param toolName 工具名
     * @param arguments 结构化参数
     */
    public ToolCall(String toolName, Map<String, Object> arguments) {
        this(toolName, null, arguments);
    }

    /**
     * 是否包含可执行的参数载荷。
     *
     * @return true 表示包含字符串参数或结构化参数
     */
    public boolean hasArgumentPayload() {
        return StringUtils.isNotBlank(argument) || !arguments.isEmpty();
    }

    /**
     * 读取结构化参数中的字符串字段。
     *
     * @param key 字段名
     * @return 字段值
     */
    public String structuredString(String key) {
        Object value = arguments.get(key);
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isBlank() ? null : text;
    }

    /**
     * 将工具参数统一序列化为工具层可消费的字符串。
     *
     * @param objectMapper JSON 序列化器
     * @return 字符串参数
     */
    public String resolveArgument(ObjectMapper objectMapper) {
        if (StringUtils.isNotBlank(argument)) {
            return argument.trim();
        }
        if (arguments.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(arguments);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("结构化工具参数序列化失败: " + toolName, ex);
        }
    }
}
