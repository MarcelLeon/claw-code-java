package com.example.codingagent.runtime;

import com.example.codingagent.bootstrap.DirectoryBootstrapService;
import org.springframework.stereotype.Service;

/**
 * 对外暴露的一次性执行门面。
 */
@Service
public class AgentRunnerFacade {

    private final DirectoryBootstrapService directoryBootstrapService;
    private final SessionService sessionService;
    private final CodingAgentEngine codingAgentEngine;

    public AgentRunnerFacade(
            DirectoryBootstrapService directoryBootstrapService,
            SessionService sessionService,
            CodingAgentEngine codingAgentEngine
    ) {
        this.directoryBootstrapService = directoryBootstrapService;
        this.sessionService = sessionService;
        this.codingAgentEngine = codingAgentEngine;
    }

    /**
     * 执行一次任务。
     *
     * @param request 请求参数
     * @return 执行结果
     */
    public RunResult run(RunRequest request) {
        directoryBootstrapService.initialize();
        AgentSession session = sessionService.openSession(request.sessionId());
        return codingAgentEngine.run(session, request);
    }
}
