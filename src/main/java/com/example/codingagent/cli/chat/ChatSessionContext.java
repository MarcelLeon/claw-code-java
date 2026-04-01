package com.example.codingagent.cli.chat;

/**
 * 交互式会话上下文。
 */
public record ChatSessionContext(
        String sessionId,
        String provider,
        String model,
        String baseUrl
) {
}
