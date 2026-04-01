package com.example.codingagent.tool;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.runtime.AgentRuntimeContext;
import com.example.codingagent.runtime.AgentSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WriteFileToolTest {

    private final WriteFileTool tool = new WriteFileTool(new ObjectMapper());

    @TempDir
    Path tempDir;

    @Test
    void shouldRejectWriteOutsideWorkspace() {
        ToolExecutionResult result = tool.execute(
                runtimeContext(tempDir),
                "{\"path\":\"../escape.txt\",\"content\":\"hello\"}"
        );

        assertThat(result.summary()).isEqualTo("写入文件失败");
        assertThat(result.output()).contains("路径越界");
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
