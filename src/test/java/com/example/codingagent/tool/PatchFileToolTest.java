package com.example.codingagent.tool;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.runtime.AgentRuntimeContext;
import com.example.codingagent.runtime.AgentSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PatchFileToolTest {

    private final PatchFileTool tool = new PatchFileTool(new ObjectMapper());

    @TempDir
    Path tempDir;

    @Test
    void shouldReplaceExactSnippetInsideWorkspace() throws IOException {
        Path file = tempDir.resolve("src/demo.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "before\n");

        ToolExecutionResult result = tool.execute(
                runtimeContext(tempDir),
                "{\"path\":\"src/demo.txt\",\"findText\":\"before\",\"replaceText\":\"after\",\"expectedMatches\":1}"
        );

        assertThat(result.summary()).isEqualTo("已应用补丁 src/demo.txt");
        assertThat(Files.readString(file)).isEqualTo("after\n");
    }

    @Test
    void shouldRejectPatchOutsideWorkspace() {
        ToolExecutionResult result = tool.execute(
                runtimeContext(tempDir),
                "{\"path\":\"../escape.txt\",\"findText\":\"before\",\"replaceText\":\"after\",\"expectedMatches\":1}"
        );

        assertThat(result.summary()).isEqualTo("补丁应用失败");
        assertThat(result.output()).contains("路径越界");
    }

    @Test
    void shouldFailWhenMatchCountDiffersFromExpectation() throws IOException {
        Path file = tempDir.resolve("src/demo.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "before before");

        ToolExecutionResult result = tool.execute(
                runtimeContext(tempDir),
                "{\"path\":\"src/demo.txt\",\"findText\":\"before\",\"replaceText\":\"after\",\"expectedMatches\":1}"
        );

        assertThat(result.summary()).isEqualTo("补丁未应用");
        assertThat(result.output()).contains("expected=1, actual=2");
        assertThat(Files.readString(file)).isEqualTo("before before");
    }

    private AgentRuntimeContext runtimeContext(Path workspaceRoot) {
        return new AgentRuntimeContext(
                workspaceRoot.toAbsolutePath().normalize(),
                "mock",
                "mock-coder",
                "https://api.openai.com",
                8,
                new AgentSession("test-session", workspaceRoot.resolve(".agent/sessions/test-session.jsonl"))
        );
    }
}
