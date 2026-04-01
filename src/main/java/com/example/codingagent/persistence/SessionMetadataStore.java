package com.example.codingagent.persistence;

import com.example.codingagent.runtime.AgentSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

/**
 * 负责维护会话侧边元数据。
 */
@Component
public class SessionMetadataStore {

    private static final String METADATA_SUFFIX = ".meta.json";

    private final ObjectMapper objectMapper;

    public SessionMetadataStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 读取会话元数据。
     *
     * @param session 会话
     * @return 元数据，不存在时返回空
     */
    public SessionMetadata load(AgentSession session) {
        Path metadataPath = metadataPath(session);
        if (!Files.exists(metadataPath)) {
            return new SessionMetadata(null);
        }
        try {
            return objectMapper.readValue(metadataPath.toFile(), SessionMetadata.class);
        } catch (IOException ex) {
            throw new IllegalStateException("读取会话元数据失败: " + metadataPath, ex);
        }
    }

    /**
     * 写入自定义标题。
     *
     * @param session 会话
     * @param customTitle 自定义标题
     */
    public void saveCustomTitle(AgentSession session, String customTitle) {
        Path metadataPath = metadataPath(session);
        try {
            Files.createDirectories(metadataPath.getParent());
            Files.writeString(
                    metadataPath,
                    objectMapper.writeValueAsString(new SessionMetadata(customTitle)),
                    StandardCharsets.UTF_8
            );
        } catch (IOException ex) {
            throw new IllegalStateException("写入会话元数据失败: " + metadataPath, ex);
        }
    }

    /**
     * 返回元数据文件路径。
     *
     * @param session 会话
     * @return 元数据路径
     */
    public Path metadataPath(AgentSession session) {
        String fileName = session.transcriptPath().getFileName().toString();
        if (fileName.endsWith(".jsonl")) {
            fileName = fileName.substring(0, fileName.length() - ".jsonl".length());
        }
        return session.transcriptPath().resolveSibling(fileName + METADATA_SUFFIX);
    }
}
