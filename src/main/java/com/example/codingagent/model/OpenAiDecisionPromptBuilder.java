package com.example.codingagent.model;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.model.protocol.AgentDecisionProtocol;
import com.example.codingagent.persistence.TranscriptEntry;
import com.example.codingagent.tool.ToolExecutionResult;
import com.example.codingagent.tool.WorkspaceTool;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

/**
 * 负责构造 OpenAI 决策提示词。
 */
@Component
public class OpenAiDecisionPromptBuilder {

    private final AgentProperties agentProperties;
    private final List<WorkspaceTool> tools;
    private final AgentDecisionProtocol agentDecisionProtocol;

    public OpenAiDecisionPromptBuilder(
            AgentProperties agentProperties,
            List<WorkspaceTool> tools,
            AgentDecisionProtocol agentDecisionProtocol
    ) {
        this.agentProperties = agentProperties;
        this.tools = tools;
        this.agentDecisionProtocol = agentDecisionProtocol;
    }

    /**
     * 构建单轮决策 Prompt。
     *
     * @param context 模型上下文
     * @return Prompt
     */
    public Prompt build(AgentRequestContext context) {
        List<String> lines = new ArrayList<>(agentProperties.getModel().getPrompt().getSystemLines());
        lines.addAll(agentDecisionProtocol.instructionLines());
        lines.add("可用工具：");
        lines.addAll(tools.stream()
                .map(tool -> "- " + tool.name() + ": " + tool.description())
                .collect(Collectors.toList()));

        List<String> userLines = new ArrayList<>();
        userLines.add("用户任务：");
        userLines.add(context.prompt());
        userLines.add("");
        userLines.add("工作区目录：");
        userLines.add(context.runtimeContext().workspaceRoot().toString());

        appendTranscriptSection(userLines, context.conversationState().transcriptEntries());

        if (!context.conversationState().toolResults().isEmpty()) {
            userLines.add("");
            userLines.add("最近工具结果：");
            for (ToolExecutionResult result : context.conversationState().toolResults()) {
                userLines.add("* 工具名: " + result.toolName());
                userLines.add("* 摘要: " + result.summary());
                userLines.add("* 输出: " + result.output());
            }
        }

        return new Prompt(
                new SystemMessage(String.join(System.lineSeparator(), lines)),
                new UserMessage(String.join(System.lineSeparator(), userLines))
        );
    }

    private void appendTranscriptSection(List<String> userLines, List<TranscriptEntry> transcriptEntries) {
        if (transcriptEntries.isEmpty()) {
            return;
        }

        AgentProperties.Transcript transcriptProperties = agentProperties.getRuntime().getTranscript();
        int recentEntries = transcriptProperties.getRecentEntries();
        int summaryEntries = transcriptProperties.getSummaryEntries();
        int maxEntryChars = transcriptProperties.getMaxEntryChars();
        int recentStart = Math.max(0, transcriptEntries.size() - recentEntries);

        userLines.add("");
        userLines.add("历史会话（同一 session 的较早内容会被压缩，最近记录优先保留）：");

        if (recentStart > 0) {
            List<TranscriptEntry> olderEntries = transcriptEntries.subList(0, recentStart);
            userLines.add("* 较早历史摘要: 共 " + olderEntries.size() + " 条，"
                    + formatRoleCounts(olderEntries));

            if (summaryEntries > 0) {
                int summaryStart = Math.max(0, olderEntries.size() - summaryEntries);
                for (int index = summaryStart; index < olderEntries.size(); index++) {
                    TranscriptEntry entry = olderEntries.get(index);
                    userLines.add("* 较早片段: " + entry.role() + ": "
                            + truncateContent(entry.content(), maxEntryChars));
                }
            }
        }

        for (int index = recentStart; index < transcriptEntries.size(); index++) {
            TranscriptEntry entry = transcriptEntries.get(index);
            userLines.add("* 最近记录: " + entry.role() + ": "
                    + truncateContent(entry.content(), maxEntryChars));
        }
    }

    private String formatRoleCounts(List<TranscriptEntry> entries) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (TranscriptEntry entry : entries) {
            counts.merge(entry.role(), 1L, Long::sum);
        }
        return counts.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue() + " 条")
                .collect(Collectors.joining("，"));
    }

    private String truncateContent(String content, int maxEntryChars) {
        String normalized = Objects.requireNonNullElse(content, "")
                .replace(System.lineSeparator(), " ")
                .replace('\n', ' ')
                .trim();
        if (normalized.length() <= maxEntryChars) {
            return normalized;
        }
        return normalized.substring(0, maxEntryChars) + "...";
    }
}
