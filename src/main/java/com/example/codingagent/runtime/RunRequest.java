package com.example.codingagent.runtime;

/**
 * 单次执行请求。
 */
public record RunRequest(
        String prompt,
        String providerOverride,
        String modelOverride,
        String baseUrlOverride,
        String sessionId
) {
}
