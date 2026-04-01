package com.example.codingagent.cli;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.CodingAgentApplication;
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
}
