package com.example.codingagent.cli.chat;

/**
 * 交互式会话启动参数。
 */
public record ChatSessionRequest(
        String provider,
        String model,
        String baseUrl,
        String sessionId
) {
}
