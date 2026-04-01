package com.example.codingagent.cli.chat;

/**
 * 表示交互式 chat 的当前会话状态。
 */
public class ChatSessionState {

    private String sessionId;
    private String provider;
    private String model;
    private String baseUrl;

    public ChatSessionState(String sessionId, String provider, String model, String baseUrl) {
        this.sessionId = sessionId;
        this.provider = provider;
        this.model = model;
        this.baseUrl = baseUrl;
    }

    /**
     * 返回当前会话 ID。
     *
     * @return 会话 ID
     */
    public String sessionId() {
        return sessionId;
    }

    /**
     * 返回当前 provider 覆盖值。
     *
     * @return provider
     */
    public String provider() {
        return provider;
    }

    /**
     * 返回当前 model 覆盖值。
     *
     * @return model
     */
    public String model() {
        return model;
    }

    /**
     * 返回当前 baseUrl 覆盖值。
     *
     * @return baseUrl
     */
    public String baseUrl() {
        return baseUrl;
    }

    /**
     * 切换到新会话。
     *
     * @param newSessionId 新会话 ID
     */
    public void switchSession(String newSessionId) {
        this.sessionId = newSessionId;
    }

    /**
     * 更新当前模型覆盖值。
     *
     * @param newModel 新模型名，允许为 null 表示清空覆盖
     */
    public void updateModel(String newModel) {
        this.model = newModel;
    }
}
