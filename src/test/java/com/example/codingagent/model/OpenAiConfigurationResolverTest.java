package com.example.codingagent.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.codingagent.config.AgentProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class OpenAiConfigurationResolverTest {

    @Test
    void shouldPreferCliBaseUrlOverride() {
        AgentProperties properties = new AgentProperties();
        properties.getModel().setBaseUrl("https://configured.example");
        MockEnvironment environment = new MockEnvironment()
                .withProperty(OpenAiConfigurationResolver.OPENAI_BASE_URL_ENV, "https://env.example");

        OpenAiConfigurationResolver resolver = new OpenAiConfigurationResolver(properties, environment);

        assertThat(resolver.resolveBaseUrl("https://cli.example")).isEqualTo("https://cli.example");
    }

    @Test
    void shouldPreferEnvironmentBaseUrlOverConfiguredValue() {
        AgentProperties properties = new AgentProperties();
        properties.getModel().setBaseUrl("https://configured.example");
        MockEnvironment environment = new MockEnvironment()
                .withProperty(OpenAiConfigurationResolver.OPENAI_BASE_URL_ENV, "https://env.example");

        OpenAiConfigurationResolver resolver = new OpenAiConfigurationResolver(properties, environment);

        assertThat(resolver.resolveBaseUrl(null)).isEqualTo("https://env.example");
    }

    @Test
    void shouldFallbackToDefaultBaseUrlWhenNothingConfigured() {
        AgentProperties properties = new AgentProperties();
        properties.getModel().setBaseUrl(" ");

        OpenAiConfigurationResolver resolver = new OpenAiConfigurationResolver(properties, new MockEnvironment());

        assertThat(resolver.resolveBaseUrl(null)).isEqualTo(OpenAiConfigurationResolver.DEFAULT_BASE_URL);
    }

    @Test
    void shouldPreferConfiguredApiKey() {
        AgentProperties properties = new AgentProperties();
        properties.getModel().setApiKey("configured-key");
        MockEnvironment environment = new MockEnvironment()
                .withProperty(OpenAiConfigurationResolver.OPENAI_API_KEY_ENV, "env-key");

        OpenAiConfigurationResolver resolver = new OpenAiConfigurationResolver(properties, environment);

        assertThat(resolver.resolveApiKey()).isEqualTo("configured-key");
    }

    @Test
    void shouldFallbackToEnvironmentApiKey() {
        AgentProperties properties = new AgentProperties();
        MockEnvironment environment = new MockEnvironment()
                .withProperty(OpenAiConfigurationResolver.OPENAI_API_KEY_ENV, "env-key");

        OpenAiConfigurationResolver resolver = new OpenAiConfigurationResolver(properties, environment);

        assertThat(resolver.resolveApiKey()).isEqualTo("env-key");
    }

    @Test
    void shouldFailWhenApiKeyMissing() {
        OpenAiConfigurationResolver resolver = new OpenAiConfigurationResolver(new AgentProperties(), new MockEnvironment());

        assertThatThrownBy(resolver::resolveApiKey)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("provider=openai");
    }
}
