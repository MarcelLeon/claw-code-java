package com.example.codingagent.tool;

/**
 * 工具执行结果。
 */
public record ToolExecutionResult(
        String toolName,
        String summary,
        String output
) {
}
