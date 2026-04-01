package com.example.codingagent.model.protocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.codingagent.model.AgentDecision;
import com.example.codingagent.model.AgentDecisionValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * JSON 决策协议测试。
 */
class JsonAgentDecisionProtocolTest {

    private final JsonAgentDecisionProtocol protocol = new JsonAgentDecisionProtocol(
            new ObjectMapper(),
            new AgentDecisionValidator()
    );

    @Test
    void shouldDecodePlainJsonDecision() {
        AgentDecision decision = protocol.decode("""
                {"summary":"直接回答","finalAnswer":"ok","toolCall":null}
                """);

        assertThat(decision.summary()).isEqualTo("直接回答");
        assertThat(decision.finalAnswer()).isEqualTo("ok");
    }

    @Test
    void shouldDecodeWrappedJsonDecision() {
        AgentDecision decision = protocol.decode("""
                下面是结果：
                {"summary":"读取文件","finalAnswer":null,"toolCall":{"toolName":"read_file","argument":"README.md"}}
                """);

        assertThat(decision.toolCall()).isNotNull();
        assertThat(decision.toolCall().toolName()).isEqualTo("read_file");
        assertThat(decision.toolCall().argument()).isEqualTo("README.md");
    }

    @Test
    void shouldDecodeStructuredToolArguments() {
        AgentDecision decision = protocol.decode("""
                {"summary":"写文件","finalAnswer":null,"toolCall":{"toolName":"write_file","arguments":{"path":"README.md","content":"hello"}}}
                """);

        assertThat(decision.toolCall()).isNotNull();
        assertThat(decision.toolCall().toolName()).isEqualTo("write_file");
        assertThat(decision.toolCall().structuredString("path")).isEqualTo("README.md");
    }

    @Test
    void shouldRejectBlankContent() {
        assertThatThrownBy(() -> protocol.decode(" "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("模型返回为空");
    }

    @Test
    void shouldRejectNonJsonContent() {
        assertThatThrownBy(() -> protocol.decode("not json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("模型未返回 JSON");
    }
}
