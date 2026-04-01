package com.example.codingagent.runtime;

import com.example.codingagent.config.AgentProperties;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * 管理本地会话标识与落盘路径。
 */
@Service
public class SessionService {

    private final AgentProperties agentProperties;

    public SessionService(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    /**
     * 打开或创建会话。
     *
     * @param sessionId 指定的会话 ID
     * @return 会话对象
     */
    public AgentSession openSession(String sessionId) {
        String resolvedSessionId = sessionId == null || sessionId.isBlank()
                ? UUID.randomUUID().toString()
                : sessionId;
        Path transcriptPath = Path.of(agentProperties.getRuntime().getSessionsDir())
                .resolve(resolvedSessionId + ".jsonl");
        return new AgentSession(resolvedSessionId, transcriptPath);
    }
}
