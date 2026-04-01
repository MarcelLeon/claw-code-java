package com.example.codingagent.cli;

import com.example.codingagent.cli.chat.ChatSessionRequest;
import com.example.codingagent.cli.chat.ChatSessionRunner;
import com.example.codingagent.cli.command.DoctorCommand;
import com.example.codingagent.cli.command.ChatCommand;
import com.example.codingagent.cli.command.RunCommand;
import java.io.InputStream;
import java.io.PrintStream;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * 顶层命令。
 */
@Component
@Command(
        name = "agent",
        mixinStandardHelpOptions = true,
        description = "Java-first Coding Agent CLI",
        subcommands = {
                DoctorCommand.class,
                ChatCommand.class,
                RunCommand.class
        }
)
public class RootCommand implements Runnable {

    @Option(names = "--model", description = "覆盖模型名称")
    private String model;

    @Option(names = "--provider", description = "覆盖模型提供方")
    private String provider;

    @Option(names = "--base-url", description = "覆盖 OpenAI 兼容接口基础地址")
    private String baseUrl;

    @Option(names = "--session-id", description = "指定会话 ID")
    private String sessionId;

    private final ChatSessionRunner chatSessionRunner;

    public RootCommand(ChatSessionRunner chatSessionRunner) {
        this.chatSessionRunner = chatSessionRunner;
    }

    @Override
    public void run() {
        run(System.in, System.out);
    }

    /**
     * 在无子命令时直接进入默认 chat 会话。
     *
     * @param input 输入流
     * @param output 输出流
     */
    void run(InputStream input, PrintStream output) {
        chatSessionRunner.run(
                new ChatSessionRequest(provider, model, baseUrl, sessionId),
                input,
                output
        );
    }
}
