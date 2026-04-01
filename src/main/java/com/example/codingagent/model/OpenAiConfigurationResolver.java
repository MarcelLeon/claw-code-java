package com.example.codingagent.model;

import com.example.codingagent.config.AgentProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 统一解析 OpenAI 相关配置优先级。
 */
@Component
public class OpenAiConfigurationResolver {

    public static final String DEFAULT_BASE_URL = "https://api.openai.com";
    public static final String OPENAI_BASE_URL_ENV = "OPENAI_BASE_URL";
    public static final String OPENAI_API_KEY_ENV = "OPENAI_API_KEY";

    private final AgentProperties agentProperties;
    private final Environment environment;

    public OpenAiConfigurationResolver(AgentProperties agentProperties, Environment environment) {
        this.agentProperties = agentProperties;
        this.environment = environment;
    }

    /**
     * 解析本次运行应使用的基础地址。
     *
     * @param override CLI 传入的覆盖值
     * @return 生效的基础地址
     */
    public String resolveBaseUrl(String override) {
        if (StringUtils.isNotBlank(override)) {
            return override;
        }
        String envBaseUrl = environment.getProperty(OPENAI_BASE_URL_ENV);
        if (StringUtils.isNotBlank(envBaseUrl)) {
            return envBaseUrl;
        }
        if (StringUtils.isNotBlank(agentProperties.getModel().getBaseUrl())) {
            return agentProperties.getModel().getBaseUrl();
        }
        return DEFAULT_BASE_URL;
    }

    /**
     * 解析 API Key。
     *
     * @return 生效的 API Key
     */
    public String resolveApiKey() {
        if (StringUtils.isNotBlank(agentProperties.getModel().getApiKey())) {
            return agentProperties.getModel().getApiKey();
        }
        String apiKey = environment.getProperty(OPENAI_API_KEY_ENV);
        if (StringUtils.isBlank(apiKey)) {
            throw new IllegalStateException("provider=openai 时必须设置 agent.model.api-key 或 OPENAI_API_KEY");
        }
        return apiKey;
    }
}
