package com.example.codingagent.model;

/**
 * 支持按 provider 路由的模型网关。
 */
public interface ProviderAgentModelGateway extends AgentModelGateway {

    /**
     * 是否支持指定 provider。
     *
     * @param provider 模型提供方
     * @return true 表示支持
     */
    boolean supports(String provider);
}
