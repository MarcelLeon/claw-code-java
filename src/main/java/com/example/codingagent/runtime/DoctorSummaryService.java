package com.example.codingagent.runtime;

import com.example.codingagent.bootstrap.DirectoryBootstrapService;
import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.model.OpenAiConfigurationResolver;
import org.springframework.stereotype.Service;

/**
 * 汇总运行时诊断信息。
 */
@Service
public class DoctorSummaryService {

    private final AgentProperties agentProperties;
    private final DirectoryBootstrapService directoryBootstrapService;
    private final OpenAiConfigurationResolver openAiConfigurationResolver;

    public DoctorSummaryService(
            AgentProperties agentProperties,
            DirectoryBootstrapService directoryBootstrapService,
            OpenAiConfigurationResolver openAiConfigurationResolver
    ) {
        this.agentProperties = agentProperties;
        this.directoryBootstrapService = directoryBootstrapService;
        this.openAiConfigurationResolver = openAiConfigurationResolver;
    }

    /**
     * 生成诊断摘要。
     *
     * @return 诊断摘要
     */
    public DoctorSummary summarize() {
        directoryBootstrapService.initialize();
        return new DoctorSummary(
                System.getProperty("java.version"),
                agentProperties.getRuntime().getWorkspaceRoot(),
                agentProperties.getRuntime().getSessionsDir(),
                agentProperties.getRuntime().getTranscript().getRecentEntries(),
                agentProperties.getRuntime().getTranscript().getSummaryEntries(),
                agentProperties.getRuntime().getTranscript().getMaxEntryChars(),
                agentProperties.getTool().getBashExec().getTimeoutSeconds(),
                agentProperties.getTool().getBashExec().getBlockedCommandPatterns().size(),
                agentProperties.getModel().getProvider(),
                agentProperties.getModel().getModel(),
                openAiConfigurationResolver.resolveBaseUrl(null)
        );
    }
}
