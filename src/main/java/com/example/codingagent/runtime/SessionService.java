package com.example.codingagent.runtime;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.persistence.SessionMetadata;
import com.example.codingagent.persistence.SessionMetadataStore;
import com.example.codingagent.persistence.TranscriptEntry;
import com.example.codingagent.persistence.TranscriptStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
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
    private final SessionMetadataStore sessionMetadataStore;
    private final SessionTitleGenerator sessionTitleGenerator;

    public SessionService(
            AgentProperties agentProperties,
            TranscriptStore transcriptStore,
            SessionMetadataStore sessionMetadataStore,
            SessionTitleGenerator sessionTitleGenerator
    ) {
        this.agentProperties = agentProperties;
        this.transcriptStore = transcriptStore;
        this.sessionMetadataStore = sessionMetadataStore;
        this.sessionTitleGenerator = sessionTitleGenerator;
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
     * 保存会话自定义标题。
     *
     * @param sessionId 会话 ID
     * @param title 标题
     */
    public void renameSession(String sessionId, String title) {
        sessionMetadataStore.saveCustomTitle(openSession(sessionId), title);
    }

    /**
     * 读取会话自定义标题。
     *
     * @param sessionId 会话 ID
     * @return 自定义标题，不存在时返回 null
     */
    public String getCustomTitle(String sessionId) {
        SessionMetadata metadata = sessionMetadataStore.load(openSession(sessionId));
        return metadata.customTitle();
    }

    /**
     * 根据当前历史生成标题。
     *
     * @param sessionId 会话 ID
     * @return 生成结果，没有足够内容时返回 null
     */
    public String generateSessionTitle(String sessionId) {
        List<TranscriptEntry> entries = transcriptStore.loadTranscript(openSession(sessionId));
        return sessionTitleGenerator.generate(entries);
    }

    /**
     * 向会话上下文文件集合中登记文件。
     *
     * @param sessionId 会话 ID
     * @param relativePath 工作区相对路径
     */
    public void addContextFile(String sessionId, String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        AgentSession session = openSession(sessionId);
        SessionMetadata metadata = sessionMetadataStore.load(session);
        List<String> contextFiles = new ArrayList<>(metadata.contextFiles());
        if (!contextFiles.contains(relativePath)) {
            contextFiles.add(relativePath);
            contextFiles.sort(String::compareTo);
            sessionMetadataStore.saveContextFiles(session, contextFiles);
        }
    }

    /**
     * 列出当前会话进入上下文的文件。
     *
     * @param sessionId 会话 ID
     * @return 相对路径列表
     */
    public List<String> listContextFiles(String sessionId) {
        return sessionMetadataStore.load(openSession(sessionId)).contextFiles();
    }

    /**
     * 返回当前工作区根目录。
     *
     * @return 工作区根目录
     */
    public Path workspaceRoot() {
        return Path.of(agentProperties.getRuntime().getWorkspaceRoot()).toAbsolutePath().normalize();
    }

    /**
     * 统计当前会话的本地成本摘要。
     *
     * @param sessionId 会话 ID
     * @return 成本摘要
     */
    public SessionCostSummary summarizeCost(String sessionId) {
        AgentSession session = openSession(sessionId);
        List<TranscriptEntry> entries = transcriptStore.loadTranscript(session);
        int userMessages = 0;
        int assistantMessages = 0;
        int toolMessages = 0;
        int totalCharacters = 0;
        int toolOutputCharacters = 0;
        for (TranscriptEntry entry : entries) {
            String content = entry.content() == null ? "" : entry.content();
            totalCharacters += content.length();
            switch (entry.role()) {
                case "user" -> userMessages++;
                case "assistant" -> assistantMessages++;
                case "tool" -> {
                    toolMessages++;
                    toolOutputCharacters += content.length();
                }
                default -> {
                    // 预留给未来新增 role，不在这里抛错阻塞本地统计。
                }
            }
        }
        int contextFileCount = sessionMetadataStore.load(session).contextFiles().size();
        return new SessionCostSummary(
                entries.size(),
                userMessages,
                assistantMessages,
                toolMessages,
                totalCharacters,
                toolOutputCharacters,
                contextFileCount
        );
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
        String title = sessionMetadataStore.load(session).customTitle();
        String preview = entries.stream()
                .filter(entry -> "user".equals(entry.role()))
                .reduce((first, second) -> second)
                .or(() -> entries.stream().reduce((first, second) -> second))
                .map(entry -> truncate(entry.content()))
                .orElse("(空会话)");
        return new SessionSummary(sessionId, lastModifiedTime(path), title, preview);
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
