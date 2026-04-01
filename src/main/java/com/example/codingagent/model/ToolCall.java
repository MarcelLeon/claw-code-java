package com.example.codingagent.model;

/**
 * 工具调用描述。
 */
public record ToolCall(
        String toolName,
        String argument
) {
}
