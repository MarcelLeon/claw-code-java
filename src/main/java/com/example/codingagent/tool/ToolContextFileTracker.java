package com.example.codingagent.tool;

import com.example.codingagent.model.ToolCall;
import com.example.codingagent.runtime.AgentRuntimeContext;
import com.example.codingagent.runtime.SessionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 根据工具调用维护会话级上下文文件索引。
 */
@Component
public class ToolContextFileTracker {

    private final ObjectMapper objectMapper;
    private final SessionService sessionService;

    public ToolContextFileTracker(ObjectMapper objectMapper, SessionService sessionService) {
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
    }

    /**
     * 在工具成功后记录上下文文件。
     *
     * @param context 运行上下文
     * @param toolCall 工具调用
     * @param result 工具结果
     */
    public void track(AgentRuntimeContext context, ToolCall toolCall, ToolExecutionResult result) {
        if (!isTrackable(toolCall.toolName(), result.summary())) {
            return;
        }
        String relativePath = extractRelativePath(toolCall);
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        sessionService.addContextFile(context.session().sessionId(), relativePath);
    }

    private boolean isTrackable(String toolName, String summary) {
        if (!"read_file".equals(toolName) && !"write_file".equals(toolName) && !"patch_file".equals(toolName)) {
            return false;
        }
        return summary != null && summary.startsWith("已");
    }

    private String extractRelativePath(ToolCall toolCall) {
        return switch (toolCall.toolName()) {
            case "read_file" -> firstNonBlank(toolCall.argument(), toolCall.structuredString("path"));
            case "write_file", "patch_file" -> firstNonBlank(
                    toolCall.structuredString("path"),
                    extractJsonPath(toolCall.resolveArgument(objectMapper))
            );
            default -> null;
        };
    }

    private String extractJsonPath(String argument) {
        try {
            JsonNode jsonNode = objectMapper.readTree(argument);
            JsonNode pathNode = jsonNode.get("path");
            if (pathNode == null || pathNode.asText().isBlank()) {
                return null;
            }
            return pathNode.asText();
        } catch (IOException ex) {
            return null;
        }
    }

    private String firstNonBlank(String first, String second) {
        if (StringUtils.isNotBlank(first)) {
            return first;
        }
        if (StringUtils.isNotBlank(second)) {
            return second;
        }
        return null;
    }
}
