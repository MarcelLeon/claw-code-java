package com.example.codingagent.tool;

import com.example.codingagent.runtime.AgentRuntimeContext;

/**
 * 工作区工具接口。
 */
public interface WorkspaceTool {

    /**
     * 返回工具名称。
     *
     * @return 工具名称
     */
    String name();

    /**
     * 返回工具描述。
     *
     * @return 工具描述
     */
    String description();

    /**
     * 是否支持当前工具名。
     *
     * @param toolName 工具名
     * @return 是否支持
     */
    default boolean supports(String toolName) {
        return name().equals(toolName);
    }

    /**
     * 执行工具。
     *
     * @param context 运行上下文
     * @param argument 输入参数
     * @return 工具结果
     */
    ToolExecutionResult execute(AgentRuntimeContext context, String argument);
}
