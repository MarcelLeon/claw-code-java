package com.example.codingagent.cli.command;

import com.example.codingagent.bootstrap.DirectoryBootstrapService;
import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.model.OpenAiConfigurationResolver;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * 诊断当前运行环境。
 */
@Component
@Command(name = "doctor", description = "检查本地运行环境")
public class DoctorCommand implements Runnable {

    private final AgentProperties agentProperties;
    private final DirectoryBootstrapService directoryBootstrapService;
    private final OpenAiConfigurationResolver openAiConfigurationResolver;

    public DoctorCommand(
            AgentProperties agentProperties,
            DirectoryBootstrapService directoryBootstrapService,
            OpenAiConfigurationResolver openAiConfigurationResolver
    ) {
        this.agentProperties = agentProperties;
        this.directoryBootstrapService = directoryBootstrapService;
        this.openAiConfigurationResolver = openAiConfigurationResolver;
    }

    @Override
    public void run() {
        directoryBootstrapService.initialize();
        System.out.println("Java 版本: " + System.getProperty("java.version"));
        System.out.println("工作目录: " + agentProperties.getRuntime().getWorkspaceRoot());
        System.out.println("会话目录: " + agentProperties.getRuntime().getSessionsDir());
        System.out.println("Transcript 最近记录数: " + agentProperties.getRuntime().getTranscript().getRecentEntries());
        System.out.println("Transcript 摘要片段数: " + agentProperties.getRuntime().getTranscript().getSummaryEntries());
        System.out.println("Transcript 单条最大字符数: " + agentProperties.getRuntime().getTranscript().getMaxEntryChars());
        System.out.println("Shell 超时(秒): " + agentProperties.getTool().getBashExec().getTimeoutSeconds());
        System.out.println("Shell 禁止模式数: " + agentProperties.getTool().getBashExec().getBlockedCommandPatterns().size());
        System.out.println("默认模型提供方: " + agentProperties.getModel().getProvider());
        System.out.println("默认模型: " + agentProperties.getModel().getModel());
        System.out.println("模型基础地址: " + openAiConfigurationResolver.resolveBaseUrl(null));
        System.out.println("状态: OK");
    }
}
