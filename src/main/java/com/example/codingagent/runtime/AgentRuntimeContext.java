package com.example.codingagent.runtime;

import java.nio.file.Path;

/**
 * 运行期上下文。
 */
public record AgentRuntimeContext(
        Path workspaceRoot,
        String provider,
        String model,
        String baseUrl,
        int maxTurns,
        AgentSession session
) {
}
