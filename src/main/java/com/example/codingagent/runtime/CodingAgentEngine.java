package com.example.codingagent.runtime;

import com.example.codingagent.model.AgentDecision;
import com.example.codingagent.model.AgentModelGateway;
import com.example.codingagent.model.AgentRequestContext;
import com.example.codingagent.model.AgentDecisionValidator;
import com.example.codingagent.persistence.TranscriptEntry;
import com.example.codingagent.persistence.TranscriptStore;
import com.example.codingagent.tool.ToolExecutionResult;
import com.example.codingagent.tool.ToolExecutor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 最小可用 Agent Loop。
 */
@Service
public class CodingAgentEngine {

    private final AgentRuntimeFactory agentRuntimeFactory;
    private final AgentModelGateway agentModelGateway;
    private final AgentDecisionValidator agentDecisionValidator;
    private final ToolExecutor toolExecutor;
    private final TranscriptStore transcriptStore;

    public CodingAgentEngine(
            AgentRuntimeFactory agentRuntimeFactory,
            AgentModelGateway agentModelGateway,
            AgentDecisionValidator agentDecisionValidator,
            ToolExecutor toolExecutor,
            TranscriptStore transcriptStore
    ) {
        this.agentRuntimeFactory = agentRuntimeFactory;
        this.agentModelGateway = agentModelGateway;
        this.agentDecisionValidator = agentDecisionValidator;
        this.toolExecutor = toolExecutor;
        this.transcriptStore = transcriptStore;
    }

    /**
     * 执行单轮或多轮工具闭环。
     *
     * @param session 会话
     * @param request 运行请求
     * @return 结果
     */
    public RunResult run(AgentSession session, RunRequest request) {
        AgentRuntimeContext context = agentRuntimeFactory.create(session, request);
        List<String> steps = new ArrayList<>();
        List<TranscriptEntry> transcriptEntries = transcriptStore.loadTranscript(session);
        ConversationState conversationState = new ConversationState(transcriptEntries);
        transcriptStore.appendUserPrompt(session, request.prompt());

        for (int turn = 1; turn <= context.maxTurns(); turn++) {
            AgentDecision decision = agentDecisionValidator.validate(agentModelGateway.decide(
                    new AgentRequestContext(context, request.prompt(), conversationState)
            ));
            steps.add("turn " + turn + ": " + decision.summary());

            if (decision.isFinalAnswer()) {
                transcriptStore.appendAssistantAnswer(session, decision.finalAnswer());
                return new RunResult(session.sessionId(), decision.finalAnswer(), turn, steps);
            }

            ToolExecutionResult toolResult = toolExecutor.execute(context, decision.toolCall());
            conversationState.addToolResult(toolResult);
            transcriptStore.appendToolResult(session, toolResult);
            steps.add("tool " + decision.toolCall().toolName() + ": " + toolResult.summary());
        }

        String fallbackAnswer = "任务已达到最大轮次，当前版本请缩小问题范围后重试。";
        transcriptStore.appendAssistantAnswer(session, fallbackAnswer);
        return new RunResult(session.sessionId(), fallbackAnswer, context.maxTurns(), steps);
    }
}
