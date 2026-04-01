package com.example.codingagent.persistence;

import com.example.codingagent.runtime.AgentSession;
import com.example.codingagent.tool.ToolExecutionResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 负责把会话记录写入 JSONL。
 */
@Component
public class TranscriptStore {

    private final ObjectMapper objectMapper;

    public TranscriptStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 记录用户输入。
     */
    public void appendUserPrompt(AgentSession session, String prompt) {
        write(session, "user", prompt);
    }

    /**
     * 记录工具结果。
     */
    public void appendToolResult(AgentSession session, ToolExecutionResult result) {
        write(session, "tool", result.toolName() + " -> " + result.output());
    }

    /**
     * 记录助手回答。
     */
    public void appendAssistantAnswer(AgentSession session, String answer) {
        write(session, "assistant", answer);
    }

    /**
     * 读取当前会话已有历史，供后续继续执行时回放。
     *
     * @param session 会话
     * @return 历史记录
     */
    public List<TranscriptEntry> loadTranscript(AgentSession session) {
        if (!Files.exists(session.transcriptPath())) {
            return List.of();
        }
        try {
            return Files.readAllLines(session.transcriptPath(), StandardCharsets.UTF_8).stream()
                    .filter(line -> !line.isBlank())
                    .map(this::readEntry)
                    .toList();
        } catch (IOException ex) {
            throw new IllegalStateException("读取会话记录失败: " + session.transcriptPath(), ex);
        }
    }

    private void write(AgentSession session, String role, String content) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("role", role);
        node.put("content", content);
        try {
            String line = objectMapper.writeValueAsString(node) + System.lineSeparator();
            Files.writeString(
                    session.transcriptPath(),
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException ex) {
            throw new IllegalStateException("写入会话记录失败: " + session.transcriptPath(), ex);
        }
    }

    private TranscriptEntry readEntry(String line) {
        try {
            return objectMapper.readValue(line, TranscriptEntry.class);
        } catch (IOException ex) {
            throw new IllegalStateException("解析会话记录失败: " + line, ex);
        }
    }
}
