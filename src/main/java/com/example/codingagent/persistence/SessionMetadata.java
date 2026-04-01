package com.example.codingagent.persistence;

import java.util.List;

/**
 * 会话元数据。
 */
public record SessionMetadata(
        String customTitle,
        List<String> contextFiles
) {
}
