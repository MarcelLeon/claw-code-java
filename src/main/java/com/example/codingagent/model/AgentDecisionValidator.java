package com.example.codingagent.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 校验模型决策结构，避免半结构化输出进入运行时。
 */
@Component
public class AgentDecisionValidator {

    /**
     * 校验并返回决策对象。
     *
     * @param decision 模型决策
     * @return 原决策对象
     */
    public AgentDecision validate(AgentDecision decision) {
        if (decision == null) {
            throw new IllegalStateException("模型未返回任何决策内容");
        }
        if (StringUtils.isBlank(decision.summary())) {
            throw new IllegalStateException("模型返回的 summary 为空，无法记录当前动作");
        }

        boolean hasFinalAnswer = StringUtils.isNotBlank(decision.finalAnswer());
        boolean hasToolCall = decision.toolCall() != null;

        if (hasFinalAnswer == hasToolCall) {
            throw new IllegalStateException("模型决策必须在 finalAnswer 和 toolCall 之间二选一");
        }
        if (hasToolCall) {
            validateToolCall(decision.toolCall());
        }
        return decision;
    }

    private void validateToolCall(ToolCall toolCall) {
        if (StringUtils.isBlank(toolCall.toolName())) {
            throw new IllegalStateException("模型返回的 toolCall.toolName 为空");
        }
        if (!toolCall.hasArgumentPayload()) {
            throw new IllegalStateException("模型返回的 toolCall.argument/toolCall.arguments 为空");
        }
    }
}
