package com.example.codingagent.tool;

import com.example.codingagent.model.ToolCall;
import com.example.codingagent.runtime.AgentRuntimeContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 分发并执行工具。
 */
@Component
public class ToolExecutor {

    private final List<WorkspaceTool> tools;
    private final ToolContextFileTracker toolContextFileTracker;
    private final ObjectMapper objectMapper;

    public ToolExecutor(
            List<WorkspaceTool> tools,
            ToolContextFileTracker toolContextFileTracker,
            ObjectMapper objectMapper
    ) {
        this.tools = tools;
        this.toolContextFileTracker = toolContextFileTracker;
        this.objectMapper = objectMapper;
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
        ToolExecutionResult result = matchedTool.execute(context, toolCall.resolveArgument(objectMapper));
        toolContextFileTracker.track(context, toolCall, result);
        return result;
    }
}
