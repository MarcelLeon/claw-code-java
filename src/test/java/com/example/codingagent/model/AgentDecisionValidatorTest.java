package com.example.codingagent.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AgentDecisionValidatorTest {

    private final AgentDecisionValidator validator = new AgentDecisionValidator();

    @Test
    void shouldAcceptFinalAnswerDecision() {
        AgentDecision decision = new AgentDecision("直接回答", "final", null);

        assertThat(validator.validate(decision)).isSameAs(decision);
    }

    @Test
    void shouldAcceptToolCallDecision() {
        AgentDecision decision = new AgentDecision("读取文件", null, new ToolCall("read_file", "README.md"));

        assertThat(validator.validate(decision)).isSameAs(decision);
    }

    @Test
    void shouldRejectDecisionWithoutFinalAnswerOrToolCall() {
        assertThatThrownBy(() -> validator.validate(new AgentDecision("缺失动作", null, null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("二选一");
    }

    @Test
    void shouldRejectDecisionWithBothFinalAnswerAndToolCall() {
        assertThatThrownBy(() -> validator.validate(
                new AgentDecision("冲突动作", "final", new ToolCall("read_file", "README.md"))
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("二选一");
    }

    @Test
    void shouldRejectToolCallWithoutToolName() {
        assertThatThrownBy(() -> validator.validate(
                new AgentDecision("坏工具调用", null, new ToolCall(" ", "README.md"))
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("toolCall.toolName");
    }

    @Test
    void shouldRejectBlankSummary() {
        assertThatThrownBy(() -> validator.validate(new AgentDecision(" ", "final", null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("summary");
    }
}
