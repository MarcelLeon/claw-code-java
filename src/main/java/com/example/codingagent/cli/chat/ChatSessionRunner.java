package com.example.codingagent.cli.chat;

import com.example.codingagent.cli.chat.command.ChatSlashCommandDispatcher;
import com.example.codingagent.cli.chat.command.ChatSlashCommandRequest;
import com.example.codingagent.cli.chat.command.ChatSlashCommandResult;
import com.example.codingagent.runtime.AgentRunnerFacade;
import com.example.codingagent.runtime.RunRequest;
import com.example.codingagent.runtime.RunResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 负责执行交互式 CLI 会话。
 */
@Component
public class ChatSessionRunner {

    private final AgentRunnerFacade agentRunnerFacade;
    private final ChatSlashCommandDispatcher slashCommandDispatcher;

    public ChatSessionRunner(
            AgentRunnerFacade agentRunnerFacade,
            ChatSlashCommandDispatcher slashCommandDispatcher
    ) {
        this.agentRunnerFacade = agentRunnerFacade;
        this.slashCommandDispatcher = slashCommandDispatcher;
    }

    /**
     * 运行 REPL 式会话。
     *
     * @param request 会话参数
     * @param input 输入流
     * @param output 输出流
     */
    public void run(ChatSessionRequest request, InputStream input, PrintStream output) {
        String resolvedSessionId = resolveSessionId(request.sessionId());
        ChatSessionState sessionState = new ChatSessionState(
                resolvedSessionId,
                request.provider(),
                request.model(),
                request.baseUrl()
        );
        output.println("session: " + resolvedSessionId);
        output.println("输入任务后回车执行，输入 /help 查看命令。");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            while (true) {
                output.print("agent> ");
                output.flush();

                String line = reader.readLine();
                if (line == null) {
                    output.println();
                    output.println("chat ended.");
                    return;
                }
                if (line.isBlank()) {
                    continue;
                }

                if (line.startsWith("/")) {
                    ChatSlashCommandResult commandResult = slashCommandDispatcher.dispatch(parseSlashCommand(
                            line,
                            sessionState
                    ));
                    printLines(output, commandResult.outputLines());
                    if (commandResult.shouldExit()) {
                        return;
                    }
                    continue;
                }

                RunResult result = agentRunnerFacade.run(new RunRequest(
                        line,
                        sessionState.provider(),
                        sessionState.model(),
                        sessionState.baseUrl(),
                        sessionState.sessionId()
                ));
                output.println(result.finalAnswer());
                output.println();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("读取交互式输入失败", ex);
        }
    }

    private ChatSlashCommandRequest parseSlashCommand(String line, ChatSessionState sessionState) {
        String normalized = line.substring(1).trim();
        if ("quit".equalsIgnoreCase(normalized)) {
            normalized = "exit";
        }
        String commandName = normalized;
        String argument = "";
        int blankIndex = normalized.indexOf(' ');
        if (blankIndex >= 0) {
            commandName = normalized.substring(0, blankIndex);
            argument = normalized.substring(blankIndex + 1).trim();
        }
        return new ChatSlashCommandRequest(line, commandName, argument, sessionState);
    }

    private void printLines(PrintStream output, java.util.List<String> lines) {
        for (String line : lines) {
            output.println(line);
        }
    }

    private String resolveSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return sessionId;
    }
}
