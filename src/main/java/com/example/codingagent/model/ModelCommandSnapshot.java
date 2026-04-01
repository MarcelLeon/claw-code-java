package com.example.codingagent.model;

import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 表示 `/model` 命令当前应展示的模型状态。
 */
public record ModelCommandSnapshot(
        String provider,
        String sessionModelSetting,
        String effectiveModel,
        String defaultModel,
        List<String> presetModels
) {

    /**
     * 是否仍在使用 provider 默认模型。
     *
     * @return true 表示当前未设置会话级覆盖
     */
    public boolean usesDefaultModel() {
        return StringUtils.isBlank(sessionModelSetting);
    }
}
