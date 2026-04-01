package com.example.codingagent.model.protocol;

import com.example.codingagent.model.AgentDecision;
import com.example.codingagent.model.AgentDecisionValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 基于 JSON 的模型决策协议。
 */
@Component
public class JsonAgentDecisionProtocol implements AgentDecisionProtocol {

    private final ObjectMapper objectMapper;
    private final AgentDecisionValidator agentDecisionValidator;

    public JsonAgentDecisionProtocol(
            ObjectMapper objectMapper,
            AgentDecisionValidator agentDecisionValidator
    ) {
        this.objectMapper = objectMapper;
        this.agentDecisionValidator = agentDecisionValidator;
    }

    @Override
    public List<String> instructionLines() {
        return List.of(
                "必须只输出 JSON，不要输出 Markdown、解释或代码块。",
                "JSON 结构固定如下：",
                "{\"summary\":\"一句话总结当前动作\",\"finalAnswer\":\"最终回答，若需调用工具则填 null\",\"toolCall\":{\"toolName\":\"工具名\",\"argument\":\"字符串参数\",\"arguments\":{\"path\":\"src/App.java\"}}}",
                "规则：",
                "1. 如果已经拿到足够的工具结果，优先输出 finalAnswer，并将 toolCall 设为 null。",
                "2. 如果需要查看更多上下文，只能选择一个工具。",
                "3. 简单工具可使用 toolCall.argument；需要结构化输入时优先使用 toolCall.arguments。",
                "4. toolCall.argument 和 toolCall.arguments 至少提供一个。",
                "5. 所有路径都使用相对工作区路径。"
        );
    }

    @Override
    public AgentDecision decode(String rawContent) {
        String json = extractJson(rawContent);
        try {
            return agentDecisionValidator.validate(objectMapper.readValue(json, AgentDecision.class));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("模型返回内容不是合法 AgentDecision JSON: " + rawContent, ex);
        }
    }

    private String extractJson(String rawContent) {
        if (StringUtils.isBlank(rawContent)) {
            throw new IllegalStateException("模型返回为空，无法继续决策");
        }
        int start = rawContent.indexOf('{');
        int end = rawContent.lastIndexOf('}');
        if (start < 0 || end < start) {
            throw new IllegalStateException("模型未返回 JSON: " + rawContent);
        }
        return rawContent.substring(start, end + 1);
    }
}
