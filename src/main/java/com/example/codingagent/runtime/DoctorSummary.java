package com.example.codingagent.runtime;

/**
 * 运行时诊断摘要。
 */
public record DoctorSummary(
        String javaVersion,
        String workspaceRoot,
        String sessionsDir,
        int transcriptRecentEntries,
        int transcriptSummaryEntries,
        int transcriptMaxEntryChars,
        long shellTimeoutSeconds,
        int shellBlockedPatternCount,
        String defaultProvider,
        String defaultModel,
        String resolvedBaseUrl
) {
}
