package com.example.codingagent.tool;

/**
 * 工具参数字段描述。
 */
public record ToolParameterDescriptor(
        String name,
        String type,
        boolean required,
        String description
) {
}
