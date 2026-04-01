package com.example.codingagent.cli;

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
 * 顶层上下文装载测试。
 */
@SpringBootTest(classes = CodingAgentApplication.class)
class DoctorCommandTest {

    @Autowired
    private RootCommand rootCommand;

    @Test
    void shouldLoadRootCommand() {
        assertThat(rootCommand).isNotNull();
    }

    @Test
    void shouldEnterDefaultChatWhenNoSubcommandProvided() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        rootCommand.run(
                new ByteArrayInputStream("/exit\n".getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8)
        );

        String text = output.toString(StandardCharsets.UTF_8);
        assertThat(text).contains("输入任务后回车执行");
        assertThat(text).contains("chat ended.");
    }
}
