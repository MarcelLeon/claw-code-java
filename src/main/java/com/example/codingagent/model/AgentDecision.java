package com.example.codingagent.model;

/**
 * 模型决策结果。
 */
public record AgentDecision(
        String summary,
        String finalAnswer,
        ToolCall toolCall
) {

    /**
     * 是否已经生成最终答案。
     *
     * @return true 表示结束
     */
    public boolean isFinalAnswer() {
        return finalAnswer != null && !finalAnswer.isBlank();
    }
}
