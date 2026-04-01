package com.example.codingagent.runtime;

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
        int contextFileCount
) {
}
