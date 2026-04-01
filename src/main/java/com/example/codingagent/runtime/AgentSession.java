package com.example.codingagent.runtime;

import java.nio.file.Path;

/**
 * Agent 会话对象。
 */
public record AgentSession(
        String sessionId,
        Path transcriptPath
) {
}
