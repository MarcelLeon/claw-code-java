package com.example.codingagent.runtime;

import java.time.Instant;

/**
 * 会话摘要信息。
 */
public record SessionSummary(
        String sessionId,
        Instant modifiedAt,
        String title,
        String preview
) {
}
