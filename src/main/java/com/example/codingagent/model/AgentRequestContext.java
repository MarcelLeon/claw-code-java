package com.example.codingagent.model;

import com.example.codingagent.runtime.AgentRuntimeContext;
import com.example.codingagent.runtime.ConversationState;

/**
 * 模型请求上下文。
 */
public record AgentRequestContext(
        AgentRuntimeContext runtimeContext,
        String prompt,
        ConversationState conversationState
) {
}
