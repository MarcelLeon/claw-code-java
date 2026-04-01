package com.example.codingagent.tool;

import com.example.codingagent.model.ToolCall;
import com.example.codingagent.runtime.AgentRuntimeContext;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 分发并执行工具。
 */
@Component
public class ToolExecutor {

    private final List<WorkspaceTool> tools;

    public ToolExecutor(List<WorkspaceTool> tools) {
        this.tools = tools;
    }

    /**
     * 执行指定工具。
     *
     * @param context 运行上下文
     * @param toolCall 工具调用
     * @return 工具结果
     */
    public ToolExecutionResult execute(AgentRuntimeContext context, ToolCall toolCall) {
        WorkspaceTool matchedTool = tools.stream()
                .filter(tool -> tool.supports(toolCall.toolName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的工具: " + toolCall.toolName()));
        return matchedTool.execute(context, toolCall.argument());
    }
}
