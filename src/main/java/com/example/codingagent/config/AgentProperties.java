package com.example.codingagent.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Agent 运行配置。
 */
@Validated
@ConfigurationProperties(prefix = "agent")
public class AgentProperties {

    private final Runtime runtime = new Runtime();
    private final Model model = new Model();
    private final Tool tool = new Tool();

    public Runtime getRuntime() {
        return runtime;
    }

    public Model getModel() {
        return model;
    }

    public Tool getTool() {
        return tool;
    }

    /**
     * 运行时配置。
     */
    public static class Runtime {

        @NotBlank
        private String workspaceRoot = ".";

        @NotBlank
        private String sessionsDir = ".agent/sessions";

        @Min(1)
        private int maxTurns = 8;

        private final Transcript transcript = new Transcript();

        public String getWorkspaceRoot() {
            return workspaceRoot;
        }

        public void setWorkspaceRoot(String workspaceRoot) {
            this.workspaceRoot = workspaceRoot;
        }

        public String getSessionsDir() {
            return sessionsDir;
        }

        public void setSessionsDir(String sessionsDir) {
            this.sessionsDir = sessionsDir;
        }

        public int getMaxTurns() {
            return maxTurns;
        }

        public void setMaxTurns(int maxTurns) {
            this.maxTurns = maxTurns;
        }

        public Transcript getTranscript() {
            return transcript;
        }
    }

    /**
     * Transcript 注入模型前的裁剪配置。
     */
    public static class Transcript {

        @Min(1)
        private int recentEntries = 8;

        @Min(0)
        private int summaryEntries = 4;

        @Min(20)
        private int maxEntryChars = 160;

        public int getRecentEntries() {
            return recentEntries;
        }

        public void setRecentEntries(int recentEntries) {
            this.recentEntries = recentEntries;
        }

        public int getSummaryEntries() {
            return summaryEntries;
        }

        public void setSummaryEntries(int summaryEntries) {
            this.summaryEntries = summaryEntries;
        }

        public int getMaxEntryChars() {
            return maxEntryChars;
        }

        public void setMaxEntryChars(int maxEntryChars) {
            this.maxEntryChars = maxEntryChars;
        }
    }

    /**
     * 模型配置。
     */
    public static class Model {

        @NotBlank
        private String provider = "mock";

        @NotBlank
        private String model = "mock-coder";

        private String baseUrl = "https://api.openai.com";

        private String apiKey;

        private Double temperature = 0.2D;

        private final Map<String, String> providerDefaults = createProviderDefaults();

        private final Map<String, List<String>> providerPresets = createProviderPresets();

        private final Prompt prompt = new Prompt();

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        /**
         * 返回按 provider 划分的默认模型设置。
         *
         * @return provider 到默认模型的映射
         */
        public Map<String, String> getProviderDefaults() {
            return providerDefaults;
        }

        /**
         * 返回按 provider 划分的推荐模型列表。
         *
         * @return provider 到推荐模型列表的映射
         */
        public Map<String, List<String>> getProviderPresets() {
            return providerPresets;
        }

        public Prompt getPrompt() {
            return prompt;
        }

        private Map<String, String> createProviderDefaults() {
            Map<String, String> defaults = new LinkedHashMap<>();
            defaults.put("mock", "mock-coder");
            defaults.put("openai", "gpt-4.1-mini");
            return defaults;
        }

        private Map<String, List<String>> createProviderPresets() {
            Map<String, List<String>> presets = new LinkedHashMap<>();
            presets.put("mock", List.of("mock-coder"));
            presets.put("openai", List.of("gpt-4.1-mini", "gpt-4.1"));
            return presets;
        }
    }

    /**
     * 模型提示词配置。
     */
    public static class Prompt {

        private List<String> systemLines = List.of(
                "你是一个 Java-first Coding Agent CLI 的决策器。",
                "你的职责是在本地工作区内选择下一步：直接回答，或调用一个工具。",
                "必须只输出 JSON，不要输出 Markdown、解释或代码块。",
                "JSON 结构固定如下：",
                "{\"summary\":\"一句话总结当前动作\",\"finalAnswer\":\"最终回答，若需调用工具则填 null\",\"toolCall\":{\"toolName\":\"工具名\",\"argument\":\"字符串参数\"}}",
                "规则：",
                "1. 如果已经拿到足够的工具结果，优先输出 finalAnswer，并将 toolCall 设为 null。",
                "2. 如果需要查看更多上下文，只能选择一个工具。",
                "3. toolCall.argument 必须是单个字符串。",
                "4. 所有路径都使用相对工作区路径。"
        );

        public List<String> getSystemLines() {
            return systemLines;
        }

        public void setSystemLines(List<String> systemLines) {
            this.systemLines = systemLines;
        }
    }

    /**
     * 工具配置。
     */
    public static class Tool {

        private final BashExec bashExec = new BashExec();

        public BashExec getBashExec() {
            return bashExec;
        }
    }

    /**
     * Shell 工具配置。
     */
    public static class BashExec {

        @Min(1)
        private long timeoutSeconds = 20L;

        private List<String> blockedCommandPatterns = List.of(
                "(^|\\s)rm\\s+-rf(\\s|$)",
                "(^|\\s)sudo\\s+rm\\s+-rf(\\s|$)",
                "(^|\\s)shutdown(\\s|$)",
                "(^|\\s)reboot(\\s|$)",
                "(^|\\s)mkfs(\\.[^\\s]+)?(\\s|$)"
        );

        public long getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(long timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public List<String> getBlockedCommandPatterns() {
            return blockedCommandPatterns;
        }

        public void setBlockedCommandPatterns(List<String> blockedCommandPatterns) {
            this.blockedCommandPatterns = blockedCommandPatterns;
        }
    }
}
