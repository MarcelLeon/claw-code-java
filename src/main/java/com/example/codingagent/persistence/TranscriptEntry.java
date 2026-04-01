package com.example.codingagent.persistence;

import java.time.Instant;

/**
 * JSONL 会话中的单条记录。
 */
public record TranscriptEntry(
        String role,
        String content,
        Instant timestamp
) {
}
