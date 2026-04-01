package com.example.codingagent.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.CodingAgentApplication;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 会话服务相关测试。
 */
@SpringBootTest(classes = CodingAgentApplication.class)
class SessionServiceTest {

    @Autowired
    private AgentRunnerFacade agentRunnerFacade;

    @Autowired
    private SessionService sessionService;

    @Test
    void shouldSummarizeLocalSessionCost() {
        String sessionId = "cost-session-test-" + UUID.randomUUID();

        agentRunnerFacade.run(new RunRequest("请读取 README", null, null, null, sessionId));

        SessionCostSummary summary = sessionService.summarizeCost(sessionId);

        assertThat(summary.totalMessages()).isEqualTo(3);
        assertThat(summary.userMessages()).isEqualTo(1);
        assertThat(summary.assistantMessages()).isEqualTo(1);
        assertThat(summary.toolMessages()).isEqualTo(1);
        assertThat(summary.totalCharacters()).isGreaterThan(0);
        assertThat(summary.toolOutputCharacters()).isGreaterThan(0);
        assertThat(summary.contextFileCount()).isEqualTo(1);
    }

    @Test
    void shouldReturnZeroCostSummaryForEmptySession() {
        String sessionId = "empty-cost-session-test-" + UUID.randomUUID();

        SessionCostSummary summary = sessionService.summarizeCost(sessionId);

        assertThat(summary.totalMessages()).isZero();
        assertThat(summary.userMessages()).isZero();
        assertThat(summary.assistantMessages()).isZero();
        assertThat(summary.toolMessages()).isZero();
        assertThat(summary.totalCharacters()).isZero();
        assertThat(summary.toolOutputCharacters()).isZero();
        assertThat(summary.contextFileCount()).isZero();
    }
}
