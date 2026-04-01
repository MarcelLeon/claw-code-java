package com.example.codingagent.model;

import com.example.codingagent.config.AgentProperties;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 统一管理模型设置、默认值解析与 `/model` 命令语义。
 */
@Service
public class ModelSettingService {

    private static final Set<String> HELP_ARGS = Set.of("help", "-h", "--help");

    private static final Set<String> INFO_ARGS = Set.of(
            "list",
            "show",
            "display",
            "current",
            "view",
            "get",
            "check",
            "describe",
            "print",
            "version",
            "about",
            "status",
            "?"
    );

    private final AgentProperties agentProperties;

    public ModelSettingService(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    /**
     * 判断参数是否表示帮助请求。
     *
     * @param argument 原始参数
     * @return true 表示帮助
     */
    public boolean isHelpArgument(String argument) {
        return HELP_ARGS.contains(normalizeArgument(argument));
    }

    /**
     * 判断参数是否表示“查看当前状态”请求。
     *
     * @param argument 原始参数
     * @return true 表示查看当前状态
     */
    public boolean isInfoArgument(String argument) {
        return INFO_ARGS.contains(normalizeArgument(argument));
    }

    /**
     * 解析运行时应使用的实际模型。
     *
     * @param provider provider
     * @param modelSetting 用户指定的模型设置
     * @return 实际模型名
     */
    public String resolveRuntimeModel(String provider, String modelSetting) {
        if (StringUtils.isNotBlank(modelSetting)) {
            return modelSetting.trim();
        }
        return resolveDefaultModel(provider);
    }

    /**
     * 构造当前 `/model` 命令的显示快照。
     *
     * @param provider 当前 provider
     * @param sessionModelSetting 当前会话模型设置
     * @return 模型快照
     */
    public ModelCommandSnapshot snapshot(String provider, String sessionModelSetting) {
        String resolvedProvider = normalizeProvider(provider);
        String defaultModel = resolveDefaultModel(resolvedProvider);
        String effectiveModel = resolveRuntimeModel(resolvedProvider, sessionModelSetting);
        return new ModelCommandSnapshot(
                resolvedProvider,
                normalizeModelSetting(sessionModelSetting),
                effectiveModel,
                defaultModel,
                listPresetModels(resolvedProvider)
        );
    }

    /**
     * 解析一次新的模型设置。
     *
     * @param provider 当前 provider
     * @param argument 用户输入
     * @return 新的模型设置结果
     */
    public ModelSelectionResult select(String provider, String argument) {
        String normalized = normalizeModelSetting(argument);
        if (StringUtils.isBlank(normalized)) {
            throw new IllegalArgumentException("Model name cannot be empty");
        }
        if (containsWhitespace(normalized)) {
            throw new IllegalArgumentException("Model name cannot contain spaces");
        }
        return new ModelSelectionResult(normalized, normalized);
    }

    /**
     * 返回当前 provider 的默认模型。
     *
     * @param provider provider
     * @return 默认模型
     */
    public String resolveDefaultModel(String provider) {
        String resolvedProvider = normalizeProvider(provider);
        String configuredDefault = agentProperties.getModel().getProviderDefaults().get(resolvedProvider);
        if (StringUtils.isNotBlank(configuredDefault)) {
            return configuredDefault.trim();
        }
        return agentProperties.getModel().getModel();
    }

    /**
     * 返回当前 provider 的推荐模型列表。
     *
     * @param provider provider
     * @return 推荐模型列表
     */
    public List<String> listPresetModels(String provider) {
        String resolvedProvider = normalizeProvider(provider);
        Set<String> models = new LinkedHashSet<>();
        models.add(resolveDefaultModel(resolvedProvider));
        List<String> configuredModels = agentProperties.getModel().getProviderPresets().get(resolvedProvider);
        if (configuredModels != null) {
            configuredModels.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(String::trim)
                    .forEach(models::add);
        }
        return List.copyOf(models);
    }

    private String normalizeProvider(String provider) {
        if (StringUtils.isBlank(provider)) {
            return agentProperties.getModel().getProvider();
        }
        return provider.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeArgument(String argument) {
        if (argument == null) {
            return "";
        }
        return argument.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeModelSetting(String modelSetting) {
        if (StringUtils.isBlank(modelSetting)) {
            return null;
        }
        return modelSetting.trim();
    }

    private boolean containsWhitespace(String value) {
        for (int index = 0; index < value.length(); index++) {
            if (Character.isWhitespace(value.charAt(index))) {
                return true;
            }
        }
        return false;
    }
}
