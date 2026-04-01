package com.example.codingagent.model;

/**
 * 模型网关统一接口。
 */
public interface AgentModelGateway {

    /**
     * 基于上下文做出下一步决策。
     *
     * @param context 请求上下文
     * @return 决策结果
     */
    AgentDecision decide(AgentRequestContext context);
}
