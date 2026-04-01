package com.example.codingagent.cli.chat;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.CodingAgentApplication;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 交互式 chat 闭环测试。
 */
@SpringBootTest(classes = CodingAgentApplication.class)
class ChatSessionRunnerTest {

    @Autowired
    private ChatSessionRunner chatSessionRunner;

    @Test
    void shouldRunInteractiveSessionAndReuseSessionId() {
        String sessionId = "chat-session-test-" + UUID.randomUUID();
        ByteArrayInputStream input = new ByteArrayInputStream((
                "/status\n"
                        + "/version\n"
                        + "/doctor\n"
                        + "/cost\n"
                        + "/files\n"
                        + "/rename\n"
                        + "/provider\n"
                        + "/provider openai\n"
                        + "/base-url\n"
                        + "/base-url https://example.invalid/v1\n"
                        + "/provider mock\n"
                        + "/base-url default\n"
                        + "/tools\n"
                        + "/help\n"
                        + "/unknown\n"
                        + "请读取 README\n"
                        + "/files\n"
                        + "/resume\n"
                        + "/clear\n"
                        + "/model help\n"
                        + "/model current\n"
                        + "/model gpt-4.1-mini\n"
                        + "/model\n"
                        + "/provider openai\n"
                        + "/model current\n"
                        + "/model\n"
                        + "/rename focused-java-agent\n"
                        + "/provider\n"
                        + "/base-url\n"
                        + "/status\n"
                        + "/cost\n"
                        + "/provider default\n"
                        + "/base-url default\n"
                        + "/resume " + sessionId + "\n"
                        + "/cost\n"
                        + "请根据历史继续\n"
                        + "/quit\n"
        ).getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        chatSessionRunner.run(
                new ChatSessionRequest("mock", null, null, sessionId),
                input,
                new PrintStream(output, true, StandardCharsets.UTF_8)
        );

        String text = output.toString(StandardCharsets.UTF_8);
        assertThat(text).contains("app: coding-agent-cli");
        assertThat(text).contains("workspace: ");
        assertThat(text).contains("session: " + sessionId);
        assertThat(text).contains("0.1.0-SNAPSHOT");
        assertThat(text).contains("Java version: ");
        assertThat(text).contains("resolved-base-url: https://api.openai.com");
        assertThat(text).contains("title: (untitled)");
        assertThat(text).contains("api-key: not configured");
        assertThat(text).contains("connectivity: not checked (run doctor to verify)");
        assertThat(text).contains("tools: ");
        assertThat(text).contains("messages: total=0, user=0, assistant=0, tool=0");
        assertThat(text).contains("duration: unavailable (legacy transcript has no timestamps)");
        assertThat(text).contains("billing: unavailable (provider token usage not tracked yet)");
        assertThat(text).contains("No files in context");
        assertThat(text).contains("Could not generate a name: no conversation context yet. Usage: /rename <name>");
        assertThat(text).contains("provider: mock");
        assertThat(text).contains("Current provider: mock");
        assertThat(text).contains("Set provider to openai");
        assertThat(text).contains("Current base-url: (default)");
        assertThat(text).contains("Set base-url to https://example.invalid/v1");
        assertThat(text).contains("Set provider to mock");
        assertThat(text).contains("Set base-url to (default)");
        assertThat(text).contains("- read_file:");
        assertThat(text).contains("argument-kind: plain_text");
        assertThat(text).contains("argument-kind: json_object");
        assertThat(text).contains("field: path <string> required");
        assertThat(text).contains("/provider  查看或切换当前会话 provider");
        assertThat(text).contains("/base-url  查看或切换当前会话 base URL");
        assertThat(text).contains("/cost  查看当前会话的本地成本统计");
        assertThat(text).contains("/doctor  诊断当前运行环境");
        assertThat(text).contains("/help  查看可用 slash commands");
        assertThat(text).contains("/version  查看当前会话运行版本");
        assertThat(text).contains("/exit  结束当前 chat 会话 (aliases: quit)");
        assertThat(text).contains("未知命令: /unknown");
        assertThat(text).contains("已执行工具 `read_file`");
        assertThat(text).contains("Files in context:");
        assertThat(text).contains("README.md");
        assertThat(text).contains("Recent conversations:");
        assertThat(text).contains("Started a new conversation.");
        assertThat(text).contains("Run /model to open the model selection menu, or /model [modelName] to set the model.");
        assertThat(text).contains("Current model: mock-coder (default)");
        assertThat(text).contains("Set model to gpt-4.1-mini");
        assertThat(text).contains("Model selection menu (text mode):");
        assertThat(text).contains("Current model: gpt-4.1-mini");
        assertThat(text).contains("Default model for provider openai: gpt-4.1-mini");
        assertThat(text).contains("Preset models:");
        assertThat(text).contains("- mock-coder");
        assertThat(text).contains("- gpt-4.1-mini");
        assertThat(text).contains("- gpt-4.1");
        assertThat(text).contains("Session renamed to: focused-java-agent");
        assertThat(text).contains("Current provider: openai");
        assertThat(text).contains("Current base-url: (default)");
        assertThat(text).contains("title: focused-java-agent");
        assertThat(text).contains("model: gpt-4.1-mini");
        assertThat(text).contains("provider: openai");
        assertThat(text).contains("base-url: (default)");
        assertThat(text).contains("context-files: 1");
        assertThat(text).contains("duration: ");
        assertThat(text).contains("max-turns: 8");
        assertThat(text).contains("Set base-url to (default)");
        assertThat(text).contains("Set provider to (default)");
        assertThat(text).contains("Resumed conversation " + sessionId + ".");
        assertThat(text).contains("messages: total=3, user=1, assistant=1, tool=1");
        assertThat(text).contains("focused-java-agent");
        assertThat(text).contains("已加载历史会话");
        assertThat(text).contains("chat ended.");
    }
}
