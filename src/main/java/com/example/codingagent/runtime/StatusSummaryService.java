package com.example.codingagent.runtime;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.tool.ToolCatalog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 汇总 CLI 当前会话的状态信息。
 */
@Service
public class StatusSummaryService {

    private static final String FALLBACK_VERSION = "0.1.0-SNAPSHOT";

    private final AgentProperties agentProperties;
    private final SessionService sessionService;
    private final ToolCatalog toolCatalog;
    private final String applicationName;

    public StatusSummaryService(
            AgentProperties agentProperties,
            SessionService sessionService,
            ToolCatalog toolCatalog,
            @Value("${spring.application.name:coding-agent-cli}") String applicationName
    ) {
        this.agentProperties = agentProperties;
        this.sessionService = sessionService;
        this.toolCatalog = toolCatalog;
        this.applicationName = applicationName;
    }

    /**
     * 生成当前会话状态摘要。
     *
     * @param sessionId 会话 ID
     * @param provider 当前 provider
     * @param model 当前模型
     * @param baseUrl 当前 base URL
     * @return 状态摘要
     */
    public SessionStatusSummary summarize(
            String sessionId,
            String provider,
            String model,
            String baseUrl
    ) {
        return new SessionStatusSummary(
                applicationName,
                resolveVersion(),
                sessionService.workspaceRoot(),
                sessionId,
                sessionService.getCustomTitle(sessionId),
                provider,
                model,
                baseUrl,
                agentProperties.getRuntime().getMaxTurns(),
                toolCatalog.listTools().size(),
                sessionService.listContextFiles(sessionId).size(),
                agentProperties.getModel().getApiKey() != null && !agentProperties.getModel().getApiKey().isBlank()
        );
    }

    private String resolveVersion() {
        Package sourcePackage = StatusSummaryService.class.getPackage();
        if (sourcePackage != null && sourcePackage.getImplementationVersion() != null) {
            return sourcePackage.getImplementationVersion();
        }
        return FALLBACK_VERSION;
    }
}
