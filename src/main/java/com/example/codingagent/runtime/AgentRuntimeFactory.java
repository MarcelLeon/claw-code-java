package com.example.codingagent.runtime;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.model.OpenAiConfigurationResolver;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

/**
 * 构建运行期上下文。
 */
@Component
public class AgentRuntimeFactory {

    private final AgentProperties agentProperties;
    private final OpenAiConfigurationResolver openAiConfigurationResolver;

    public AgentRuntimeFactory(
            AgentProperties agentProperties,
            OpenAiConfigurationResolver openAiConfigurationResolver
    ) {
        this.agentProperties = agentProperties;
        this.openAiConfigurationResolver = openAiConfigurationResolver;
    }

    /**
     * 创建上下文。
     *
     * @param session 会话
     * @param request 运行请求
     * @return 上下文
     */
    public AgentRuntimeContext create(AgentSession session, RunRequest request) {
        String provider = request.providerOverride() == null || request.providerOverride().isBlank()
                ? agentProperties.getModel().getProvider()
                : request.providerOverride();
        String model = request.modelOverride() == null || request.modelOverride().isBlank()
                ? agentProperties.getModel().getModel()
                : request.modelOverride();
        String baseUrl = openAiConfigurationResolver.resolveBaseUrl(request.baseUrlOverride());
        return new AgentRuntimeContext(
                Path.of(agentProperties.getRuntime().getWorkspaceRoot()).toAbsolutePath().normalize(),
                provider,
                model,
                baseUrl,
                agentProperties.getRuntime().getMaxTurns(),
                session
        );
    }
}
