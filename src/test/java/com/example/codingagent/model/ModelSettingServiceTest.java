package com.example.codingagent.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.codingagent.config.AgentProperties;
import org.junit.jupiter.api.Test;

/**
 * 模型设置服务测试。
 */
class ModelSettingServiceTest {

    private final AgentProperties properties = new AgentProperties();

    private final ModelSettingService service = new ModelSettingService(properties);

    @Test
    void shouldRecognizeHelpAndInfoArguments() {
        assertThat(service.isHelpArgument("help")).isTrue();
        assertThat(service.isHelpArgument("--help")).isTrue();
        assertThat(service.isInfoArgument("current")).isTrue();
        assertThat(service.isInfoArgument("?")).isTrue();
    }

    @Test
    void shouldResolveProviderSpecificDefaults() {
        assertThat(service.resolveDefaultModel("mock")).isEqualTo("mock-coder");
        assertThat(service.resolveDefaultModel("openai")).isEqualTo("gpt-4.1-mini");
    }

    @Test
    void shouldBuildSnapshotWithEffectiveModelAndPresets() {
        ModelCommandSnapshot snapshot = service.snapshot("openai", null);

        assertThat(snapshot.usesDefaultModel()).isTrue();
        assertThat(snapshot.effectiveModel()).isEqualTo("gpt-4.1-mini");
        assertThat(snapshot.presetModels()).containsExactly("gpt-4.1-mini", "gpt-4.1");
    }

    @Test
    void shouldResolveCustomModelSetting() {
        ModelSelectionResult result = service.select("openai", "gpt-4.1");

        assertThat(result.modelSetting()).isEqualTo("gpt-4.1");
        assertThat(result.displayLabel()).isEqualTo("gpt-4.1");
    }

    @Test
    void shouldRejectBlankOrWhitespaceSeparatedModelNames() {
        assertThatThrownBy(() -> service.select("openai", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
        assertThatThrownBy(() -> service.select("openai", "gpt 4.1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot contain spaces");
    }
}
