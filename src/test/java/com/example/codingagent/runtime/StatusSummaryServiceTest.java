package com.example.codingagent.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.CodingAgentApplication;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 状态摘要服务测试。
 */
@SpringBootTest(classes = CodingAgentApplication.class)
class StatusSummaryServiceTest {

    @Autowired
    private AgentRunnerFacade agentRunnerFacade;

    @Autowired
    private StatusSummaryService statusSummaryService;

    @Test
    void shouldSummarizeCurrentSessionStatus() {
        String sessionId = "status-session-test-" + UUID.randomUUID();

        agentRunnerFacade.run(new RunRequest("请读取 README", null, null, null, sessionId));

        SessionStatusSummary summary = statusSummaryService.summarize(sessionId, "mock", null, null);

        assertThat(summary.applicationName()).isEqualTo("coding-agent-cli");
        assertThat(summary.applicationVersion()).isNotBlank();
        assertThat(summary.sessionId()).isEqualTo(sessionId);
        assertThat(summary.provider()).isEqualTo("mock");
        assertThat(summary.model()).isNull();
        assertThat(summary.baseUrl()).isNull();
        assertThat(summary.toolCount()).isGreaterThan(0);
        assertThat(summary.contextFileCount()).isEqualTo(1);
        assertThat(summary.maxTurns()).isGreaterThan(0);
        assertThat(summary.apiKeyConfigured()).isFalse();
    }
}
