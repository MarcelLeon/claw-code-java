package com.example.codingagent.model;

import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 根据 provider 选择具体模型网关。
 */
@Primary
@Component
public class RoutingAgentModelGateway implements AgentModelGateway {

    private final List<ProviderAgentModelGateway> gateways;

    public RoutingAgentModelGateway(List<ProviderAgentModelGateway> gateways) {
        this.gateways = gateways;
    }

    @Override
    public AgentDecision decide(AgentRequestContext context) {
        String provider = context.runtimeContext().provider();
        return gateways.stream()
                .filter(gateway -> gateway.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到匹配的模型提供方: " + provider))
                .decide(context);
    }
}
