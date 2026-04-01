package com.example.codingagent.model;

/**
 * 表示一次 `/model` 变更后的结果。
 */
public record ModelSelectionResult(
        String modelSetting,
        String displayLabel
) {
}
