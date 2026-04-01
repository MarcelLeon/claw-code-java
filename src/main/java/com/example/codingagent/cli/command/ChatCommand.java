package com.example.codingagent.cli.command;

import com.example.codingagent.cli.chat.ChatSessionRequest;
import com.example.codingagent.cli.chat.ChatSessionRunner;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * 启动交互式 chat 会话。
 */
@Component
@Command(name = "chat", description = "启动交互式 Coding Agent 会话")
public class ChatCommand implements Runnable {

    @Option(names = "--model", description = "覆盖模型名称")
    private String model;

    @Option(names = "--provider", description = "覆盖模型提供方")
    private String provider;

    @Option(names = "--base-url", description = "覆盖 OpenAI 兼容接口基础地址")
    private String baseUrl;

    @Option(names = "--session-id", description = "指定会话 ID")
    private String sessionId;

    private final ChatSessionRunner chatSessionRunner;

    public ChatCommand(ChatSessionRunner chatSessionRunner) {
        this.chatSessionRunner = chatSessionRunner;
    }

    @Override
    public void run() {
        chatSessionRunner.run(
                new ChatSessionRequest(provider, model, baseUrl, sessionId),
                System.in,
                System.out
        );
    }
}
