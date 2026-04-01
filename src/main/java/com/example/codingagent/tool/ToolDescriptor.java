package com.example.codingagent.tool;

/**
 * 工具元数据描述。
 */
public record ToolDescriptor(
        String name,
        String description,
        ToolArgumentDescriptor argumentDescriptor
) {
}
