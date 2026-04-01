package com.example.codingagent.tool;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.runtime.AgentRuntimeContext;
import com.example.codingagent.runtime.AgentSession;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class BashExecToolTest {

    @Test
    void shouldBlockDangerousCommandByPattern() {
        AgentProperties properties = new AgentProperties();
        BashExecTool tool = new BashExecTool(properties);

        ToolExecutionResult result = tool.execute(runtimeContext(), "rm -rf .");

        assertThat(result.summary()).isEqualTo("命令被安全策略拦截");
        assertThat(result.output()).contains("rm\\s+-rf");
    }

    @Test
    void shouldRespectConfiguredTimeout() {
        AgentProperties properties = new AgentProperties();
        properties.getTool().getBashExec().setTimeoutSeconds(1L);
        BashExecTool tool = new BashExecTool(properties);

        ToolExecutionResult result = tool.execute(runtimeContext(), "sleep 2");

        assertThat(result.summary()).isEqualTo("命令执行超时");
        assertThat(result.output()).contains("1 秒");
    }

    @Test
    void shouldExecuteSafeCommand() {
        AgentProperties properties = new AgentProperties();
        BashExecTool tool = new BashExecTool(properties);

        ToolExecutionResult result = tool.execute(runtimeContext(), "printf 'hello'");

        assertThat(result.summary()).contains("退出码 0");
        assertThat(result.output()).isEqualTo("hello");
    }

    private AgentRuntimeContext runtimeContext() {
        return new AgentRuntimeContext(
                Path.of(".").toAbsolutePath().normalize(),
                "mock",
                "mock-coder",
                "https://api.openai.com",
                8,
                new AgentSession("test-session", Path.of(".agent/sessions/test-session.jsonl").toAbsolutePath().normalize())
        );
    }
}
