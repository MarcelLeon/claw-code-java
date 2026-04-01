package com.example.codingagent.runtime;

import java.nio.file.Path;

/**
 * 当前会话的状态摘要。
 */
public record SessionStatusSummary(
        String applicationName,
        String applicationVersion,
        Path workspaceRoot,
        String sessionId,
        String title,
        String provider,
        String model,
        String baseUrl,
        int maxTurns,
        int toolCount,
        int contextFileCount,
        boolean apiKeyConfigured
) {
}
