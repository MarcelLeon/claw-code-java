package com.example.codingagent.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.CodingAgentApplication;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 最小闭环验证测试。
 */
@SpringBootTest(classes = CodingAgentApplication.class)
class CodingAgentEngineTest {

    @Autowired
    private AgentRunnerFacade agentRunnerFacade;

    @Test
    void shouldReturnDirectMockAnswer() {
        RunResult result = agentRunnerFacade.run(new RunRequest("请帮我总结当前阶段目标", null, null, null, "test-session"));
        assertThat(result.finalAnswer()).contains("闭环已打通");
        assertThat(result.turns()).isEqualTo(1);
    }

    @Test
    void shouldFinishAfterToolExecution() {
        RunResult result = agentRunnerFacade.run(new RunRequest("请读取 README", null, null, null, "tool-session"));
        assertThat(result.finalAnswer()).contains("已执行工具 `read_file`");
        assertThat(result.turns()).isEqualTo(2);
        assertThat(result.steps()).contains("turn 1: 准备读取 README");
    }

    @Test
    void shouldExecuteShellTool() {
        RunResult result = agentRunnerFacade.run(new RunRequest("请执行一个 pwd 命令", null, null, null, "shell-session"));
        assertThat(result.finalAnswer()).contains("已执行工具 `bash_exec`");
        assertThat(result.finalAnswer()).contains("/Users/wangzq/VsCodeProjects/claude-code");
        assertThat(result.turns()).isEqualTo(2);
    }

    @Test
    void shouldWriteFileByTool() throws IOException {
        RunResult result = agentRunnerFacade.run(new RunRequest("请帮我创建文件", null, null, null, "write-session"));
        Path target = Path.of(".agent/generated/mock-note.txt").toAbsolutePath().normalize();
        assertThat(result.finalAnswer()).contains("已执行工具 `write_file`");
        assertThat(Files.exists(target)).isTrue();
        assertThat(Files.readString(target)).contains("mock generated content");
    }

    @Test
    void shouldApplyPatchFileByTool() throws IOException {
        Path target = Path.of(".agent/generated/mock-patch.txt").toAbsolutePath().normalize();
        Files.createDirectories(target.getParent());
        Files.writeString(target, "before\n");

        RunResult result = agentRunnerFacade.run(new RunRequest("请帮我打一个补丁", null, null, null, "patch-session"));

        assertThat(result.finalAnswer()).contains("已执行工具 `patch_file`");
        assertThat(Files.readString(target)).contains("after");
    }

    @Test
    void shouldLoadTranscriptWhenContinuingSameSession() throws IOException {
        String sessionId = "resume-session";
        agentRunnerFacade.run(new RunRequest("请帮我总结当前阶段目标", null, null, null, sessionId));

        RunResult result = agentRunnerFacade.run(new RunRequest("请根据历史继续", null, null, null, sessionId));
        Path transcript = Path.of(".agent/sessions/" + sessionId + ".jsonl").toAbsolutePath().normalize();

        assertThat(result.finalAnswer()).contains("已加载历史会话");
        assertThat(result.finalAnswer()).contains("请帮我总结当前阶段目标");
        assertThat(result.finalAnswer()).contains("闭环已打通");
        assertThat(Files.readString(transcript)).contains("请根据历史继续");
    }
}
