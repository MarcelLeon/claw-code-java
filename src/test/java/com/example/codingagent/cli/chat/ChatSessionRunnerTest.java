package com.example.codingagent.cli.chat;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.CodingAgentApplication;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
        ByteArrayInputStream input = new ByteArrayInputStream((
                "/status\n"
                        + "/tools\n"
                        + "/help\n"
                        + "/unknown\n"
                        + "请读取 README\n"
                        + "/resume\n"
                        + "/clear\n"
                        + "/model gpt-4.1-mini\n"
                        + "/model\n"
                        + "/status\n"
                        + "/resume chat-session-test\n"
                        + "请根据历史继续\n"
                        + "/exit\n"
        ).getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        chatSessionRunner.run(
                new ChatSessionRequest("mock", null, null, "chat-session-test"),
                input,
                new PrintStream(output, true, StandardCharsets.UTF_8)
        );

        String text = output.toString(StandardCharsets.UTF_8);
        assertThat(text).contains("session: chat-session-test");
        assertThat(text).contains("provider: mock");
        assertThat(text).contains("- read_file:");
        assertThat(text).contains("/help  查看可用 slash commands");
        assertThat(text).contains("未知命令: /unknown");
        assertThat(text).contains("已执行工具 `read_file`");
        assertThat(text).contains("Recent conversations:");
        assertThat(text).contains("Started a new conversation.");
        assertThat(text).contains("Set model to gpt-4.1-mini");
        assertThat(text).contains("Current model: gpt-4.1-mini");
        assertThat(text).contains("model: gpt-4.1-mini");
        assertThat(text).contains("Resumed conversation chat-session-test.");
        assertThat(text).contains("已加载历史会话");
        assertThat(text).contains("chat ended.");
    }
}
