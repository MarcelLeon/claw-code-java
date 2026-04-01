package com.example.codingagent.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.model.ModelSettingService;
import com.example.codingagent.model.OpenAiConfigurationResolver;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class AgentRuntimeFactoryTest {

    @Test
    void shouldApplyRequestOverrides() {
        AgentProperties properties = new AgentProperties();
        properties.getRuntime().setWorkspaceRoot(".");
        properties.getRuntime().setMaxTurns(8);
        properties.getModel().setProvider("mock");
        properties.getModel().setModel("mock-coder");

        AgentRuntimeFactory factory = new AgentRuntimeFactory(
                properties,
                new OpenAiConfigurationResolver(properties, new MockEnvironment()),
                new ModelSettingService(properties)
        );

        AgentRuntimeContext context = factory.create(
                new AgentSession("session-1", java.nio.file.Path.of(".agent/sessions/session-1.jsonl")),
                new RunRequest("prompt", "openai", "gpt-4.1-mini", "https://cli.example", "session-1")
        );

        assertThat(context.provider()).isEqualTo("openai");
        assertThat(context.model()).isEqualTo("gpt-4.1-mini");
        assertThat(context.baseUrl()).isEqualTo("https://cli.example");
        assertThat(context.maxTurns()).isEqualTo(8);
    }

    @Test
    void shouldUseResolvedDefaultBaseUrlWhenOverrideMissing() {
        AgentProperties properties = new AgentProperties();
        properties.getModel().setBaseUrl(" ");
        MockEnvironment environment = new MockEnvironment()
                .withProperty(OpenAiConfigurationResolver.OPENAI_BASE_URL_ENV, "https://env.example");

        AgentRuntimeFactory factory = new AgentRuntimeFactory(
                properties,
                new OpenAiConfigurationResolver(properties, environment),
                new ModelSettingService(properties)
        );

        AgentRuntimeContext context = factory.create(
                new AgentSession("session-2", java.nio.file.Path.of(".agent/sessions/session-2.jsonl")),
                new RunRequest("prompt", null, null, null, "session-2")
        );

        assertThat(context.provider()).isEqualTo("mock");
        assertThat(context.model()).isEqualTo("mock-coder");
        assertThat(context.baseUrl()).isEqualTo("https://env.example");
    }

    @Test
    void shouldResolveProviderSpecificDefaultModelWhenProviderChanges() {
        AgentProperties properties = new AgentProperties();
        properties.getModel().setProvider("mock");
        properties.getModel().setModel("mock-coder");

        AgentRuntimeFactory factory = new AgentRuntimeFactory(
                properties,
                new OpenAiConfigurationResolver(properties, new MockEnvironment()),
                new ModelSettingService(properties)
        );

        AgentRuntimeContext context = factory.create(
                new AgentSession("session-3", java.nio.file.Path.of(".agent/sessions/session-3.jsonl")),
                new RunRequest("prompt", "openai", null, null, "session-3")
        );

        assertThat(context.provider()).isEqualTo("openai");
        assertThat(context.model()).isEqualTo("gpt-4.1-mini");
    }
}
