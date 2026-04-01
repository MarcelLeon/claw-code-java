package com.example.codingagent.runtime;

import com.example.codingagent.persistence.TranscriptEntry;
import com.example.codingagent.tool.ToolExecutionResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 表示一次 agent 执行过程中的会话状态。
 */
public class ConversationState {

    private final List<TranscriptEntry> transcriptEntries;
    private final List<ToolExecutionResult> toolResults;

    public ConversationState(List<TranscriptEntry> transcriptEntries) {
        this.transcriptEntries = new ArrayList<>(transcriptEntries);
        this.toolResults = new ArrayList<>();
    }

    /**
     * 返回历史 transcript 快照。
     *
     * @return transcript 列表
     */
    public List<TranscriptEntry> transcriptEntries() {
        return List.copyOf(transcriptEntries);
    }

    /**
     * 返回当前运行中产生的工具结果快照。
     *
     * @return 工具结果列表
     */
    public List<ToolExecutionResult> toolResults() {
        return List.copyOf(toolResults);
    }

    /**
     * 记录一次工具结果。
     *
     * @param result 工具结果
     */
    public void addToolResult(ToolExecutionResult result) {
        toolResults.add(result);
    }

    /**
     * 返回最近一次工具结果。
     *
     * @return 最近工具结果
     */
    public Optional<ToolExecutionResult> lastToolResult() {
        if (toolResults.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toolResults.get(toolResults.size() - 1));
    }

    /**
     * 返回最近一条指定角色的历史记录。
     *
     * @param role 角色名
     * @return 历史记录
     */
    public Optional<TranscriptEntry> lastTranscriptEntry(String role) {
        for (int index = transcriptEntries.size() - 1; index >= 0; index--) {
            TranscriptEntry entry = transcriptEntries.get(index);
            if (role.equals(entry.role())) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }
}
