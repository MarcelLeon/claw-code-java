package com.example.codingagent.model;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.model.protocol.AgentDecisionProtocol;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 基于 Spring AI OpenAI 模型的决策网关。
 */
@Component
public class OpenAiAgentModelGateway implements ProviderAgentModelGateway {

    private final AgentProperties agentProperties;
    private final OpenAiDecisionPromptBuilder promptBuilder;
    private final OpenAiConfigurationResolver openAiConfigurationResolver;
    private final AgentDecisionProtocol agentDecisionProtocol;

    public OpenAiAgentModelGateway(
            AgentProperties agentProperties,
            OpenAiDecisionPromptBuilder promptBuilder,
            OpenAiConfigurationResolver openAiConfigurationResolver,
            AgentDecisionProtocol agentDecisionProtocol
    ) {
        this.agentProperties = agentProperties;
        this.promptBuilder = promptBuilder;
        this.openAiConfigurationResolver = openAiConfigurationResolver;
        this.agentDecisionProtocol = agentDecisionProtocol;
    }

    @Override
    public boolean supports(String provider) {
        return "openai".equalsIgnoreCase(provider);
    }

    @Override
    public AgentDecision decide(AgentRequestContext context) {
        Prompt prompt = promptBuilder.build(context);
        ChatResponse response = buildChatModel(context).call(prompt);
        String content = response.getResult().getOutput().getText();
        return agentDecisionProtocol.decode(content);
    }

    private OpenAiChatModel buildChatModel(AgentRequestContext context) {
        OpenAiApi api = OpenAiApi.builder()
                .baseUrl(context.runtimeContext().baseUrl())
                .apiKey(openAiConfigurationResolver.resolveApiKey())
                .restClientBuilder(RestClient.builder())
                .webClientBuilder(WebClient.builder())
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(context.runtimeContext().model())
                .temperature(agentProperties.getModel().getTemperature())
                .build();

        ToolCallingManager toolCallingManager = ToolCallingManager.builder()
                .observationRegistry(ObservationRegistry.NOOP)
                .toolCallbackResolver(toolName -> null)
                .toolExecutionExceptionProcessor(ex -> ex.getMessage())
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .toolCallingManager(toolCallingManager)
                .toolExecutionEligibilityPredicate(new DefaultToolExecutionEligibilityPredicate())
                .retryTemplate(RetryTemplate.builder().maxAttempts(2).fixedBackoff(200L).build())
                .observationRegistry(ObservationRegistry.NOOP)
                .build();
    }
}
