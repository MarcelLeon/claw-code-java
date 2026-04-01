package com.example.codingagent.runtime;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.persistence.TranscriptEntry;
import com.example.codingagent.persistence.TranscriptStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * 管理本地会话标识与落盘路径。
 */
@Service
public class SessionService {

    private final AgentProperties agentProperties;
    private final TranscriptStore transcriptStore;

    public SessionService(AgentProperties agentProperties, TranscriptStore transcriptStore) {
        this.agentProperties = agentProperties;
        this.transcriptStore = transcriptStore;
    }

    /**
     * 打开或创建会话。
     *
     * @param sessionId 指定的会话 ID
     * @return 会话对象
     */
    public AgentSession openSession(String sessionId) {
        String resolvedSessionId = sessionId == null || sessionId.isBlank()
                ? createSessionId()
                : sessionId;
        Path transcriptPath = sessionPath(resolvedSessionId);
        return new AgentSession(resolvedSessionId, transcriptPath);
    }

    /**
     * 创建一个新的随机会话 ID。
     *
     * @return 会话 ID
     */
    public String createSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 判断会话是否已存在。
     *
     * @param sessionId 会话 ID
     * @return true 表示已存在
     */
    public boolean sessionExists(String sessionId) {
        return Files.exists(sessionPath(sessionId));
    }

    /**
     * 返回最近更新的会话列表。
     *
     * @param limit 返回条数
     * @return 会话摘要
     */
    public List<SessionSummary> listRecentSessions(int limit) {
        Path sessionsDir = Paths.get(agentProperties.getRuntime().getSessionsDir());
        if (!Files.exists(sessionsDir)) {
            return List.of();
        }
        try (var stream = Files.list(sessionsDir)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(".jsonl"))
                    .sorted(Comparator.comparing(this::lastModifiedTime).reversed())
                    .limit(limit)
                    .map(this::toSummary)
                    .toList();
        } catch (IOException ex) {
            throw new IllegalStateException("读取会话列表失败: " + sessionsDir, ex);
        }
    }

    private Path sessionPath(String sessionId) {
        return Path.of(agentProperties.getRuntime().getSessionsDir())
                .resolve(sessionId + ".jsonl");
    }

    private Instant lastModifiedTime(Path path) {
        try {
            return Files.getLastModifiedTime(path).toInstant();
        } catch (IOException ex) {
            return Instant.EPOCH;
        }
    }

    private SessionSummary toSummary(Path path) {
        String fileName = path.getFileName().toString();
        String sessionId = fileName.substring(0, fileName.length() - ".jsonl".length());
        AgentSession session = new AgentSession(sessionId, path);
        List<TranscriptEntry> entries = transcriptStore.loadTranscript(session);
        String preview = entries.stream()
                .filter(entry -> "user".equals(entry.role()))
                .reduce((first, second) -> second)
                .or(() -> entries.stream().reduce((first, second) -> second))
                .map(entry -> truncate(entry.content()))
                .orElse("(空会话)");
        return new SessionSummary(sessionId, lastModifiedTime(path), preview);
    }

    private String truncate(String content) {
        if (content == null || content.isBlank()) {
            return "(空内容)";
        }
        String normalized = content.replace(System.lineSeparator(), " ").trim();
        if (normalized.length() <= 80) {
            return normalized;
        }
        return normalized.substring(0, 80) + "...";
    }
}
