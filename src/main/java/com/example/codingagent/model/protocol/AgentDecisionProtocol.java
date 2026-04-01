package com.example.codingagent.model.protocol;

import com.example.codingagent.model.AgentDecision;
import java.util.List;

/**
 * 定义模型决策的结构化协议。
 */
public interface AgentDecisionProtocol {

    /**
     * 返回需要注入给模型的协议说明。
     *
     * @return 协议说明行
     */
    List<String> instructionLines();

    /**
     * 将模型原始文本解码为领域决策对象。
     *
     * @param rawContent 模型输出
     * @return 决策结果
     */
    AgentDecision decode(String rawContent);
}
