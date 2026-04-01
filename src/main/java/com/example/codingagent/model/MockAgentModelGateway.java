package com.example.codingagent.model;

import com.example.codingagent.tool.ToolExecutionResult;
import java.util.Map;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * 用于本地闭环验证的 Mock 模型。
 */
@Component
public class MockAgentModelGateway implements ProviderAgentModelGateway {

    private static final int MAX_PREVIEW_LENGTH = 240;

    @Override
    public boolean supports(String provider) {
        return provider == null || provider.isBlank() || "mock".equalsIgnoreCase(provider);
    }

    @Override
    public AgentDecision decide(AgentRequestContext context) {
        String prompt = context.prompt().toLowerCase(Locale.ROOT);
        if (context.conversationState().lastToolResult().isPresent()) {
            ToolExecutionResult result = context.conversationState().lastToolResult().get();
            return new AgentDecision(
                    "基于工具结果生成最终答案",
                    "已执行工具 `" + result.toolName() + "`。" + System.lineSeparator()
                            + "工具摘要：" + result.summary() + System.lineSeparator()
                            + "输出预览：" + buildPreview(result.output()),
                    null
            );
        }
        if (prompt.contains("继续") || prompt.contains("上一轮") || prompt.contains("历史") || prompt.contains("上次")) {
            String lastUserPrompt = context.conversationState().lastTranscriptEntry("user")
                    .map(entry -> entry.content())
                    .orElse("无");
            String lastAssistantAnswer = context.conversationState().lastTranscriptEntry("assistant")
                    .map(entry -> entry.content())
                    .orElse("无");
            return new AgentDecision(
                    "基于历史会话生成续跑摘要",
                    "已加载历史会话。" + System.lineSeparator()
                            + "上一条用户消息：" + buildPreview(lastUserPrompt) + System.lineSeparator()
                            + "上一条助手消息：" + buildPreview(lastAssistantAnswer),
                    null
            );
        }
        if (prompt.contains("列出") || prompt.contains("list")) {
            return new AgentDecision("准备列出工作区文件", null, new ToolCall("list_files", "."));
        }
        if (prompt.contains("读取") || prompt.contains("read")) {
            return new AgentDecision("准备读取 README", null, new ToolCall("read_file", "README.md"));
        }
        if (prompt.contains("搜索") || prompt.contains("grep")) {
            return new AgentDecision("准备搜索关键字", null, new ToolCall("grep_text", "agent"));
        }
        if (prompt.contains("创建文件") || prompt.contains("写入")) {
            return new AgentDecision(
                    "准备写入工作区文件",
                    null,
                    new ToolCall("write_file", Map.of(
                            "path", ".agent/generated/mock-note.txt",
                            "content", "mock generated content\n"
                    ))
            );
        }
        if (prompt.contains("补丁") || prompt.contains("patch") || prompt.contains("替换")) {
            return new AgentDecision(
                    "准备按片段修改文件",
                    null,
                    new ToolCall("patch_file", Map.of(
                            "path", ".agent/generated/mock-patch.txt",
                            "findText", "before",
                            "replaceText", "after",
                            "expectedMatches", 1
                    ))
            );
        }
        if (prompt.contains("命令") || prompt.contains("shell") || prompt.contains("bash") || prompt.contains("pwd")) {
            return new AgentDecision("准备执行本地命令", null, new ToolCall("bash_exec", "pwd"));
        }
        return new AgentDecision(
                "直接给出结果",
                "Mock 模型已收到任务：" + context.prompt() + "。当前阶段闭环已打通，后续将替换为真实 Spring AI 模型。",
                null
        );
    }

    /**
     * 生成输出预览，避免 CLI 一次性打印过长内容。
     *
     * @param output 工具输出
     * @return 预览内容
     */
    private String buildPreview(String output) {
        if (output == null || output.isBlank()) {
            return "无输出";
        }
        String normalized = output.replace(System.lineSeparator(), " | ");
        if (normalized.length() <= MAX_PREVIEW_LENGTH) {
            return normalized;
        }
        return normalized.substring(0, MAX_PREVIEW_LENGTH) + "...";
    }
}
