package com.example.codingagent.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * 工具调用参数兼容性测试。
 */
class ToolCallTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldResolvePlainStringArgument() {
        ToolCall toolCall = new ToolCall("read_file", "README.md");

        assertThat(toolCall.resolveArgument(objectMapper)).isEqualTo("README.md");
    }

    @Test
    void shouldResolveStructuredArgumentsAsJson() {
        ToolCall toolCall = new ToolCall("write_file", Map.of(
                "path", "README.md",
                "content", "hello"
        ));

        assertThat(toolCall.resolveArgument(objectMapper)).contains("\"path\":\"README.md\"");
        assertThat(toolCall.structuredString("path")).isEqualTo("README.md");
    }
}
