package com.example.codingagent.runtime;

import java.time.Instant;

/**
 * 当前会话的本地成本摘要。
 */
public record SessionCostSummary(
        int totalMessages,
        int userMessages,
        int assistantMessages,
        int toolMessages,
        int totalCharacters,
        int toolOutputCharacters,
        int contextFileCount,
        Instant startedAt,
        Instant lastUpdatedAt,
        long durationSeconds
) {
}
