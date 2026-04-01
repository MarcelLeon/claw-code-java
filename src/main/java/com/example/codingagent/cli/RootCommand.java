package com.example.codingagent.cli;

import com.example.codingagent.cli.command.DoctorCommand;
import com.example.codingagent.cli.command.ChatCommand;
import com.example.codingagent.cli.command.RunCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

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

    @Override
    public void run() {
        System.out.println("请使用子命令，例如: agent doctor、agent run -p \"帮我分析项目\" 或 agent chat");
    }
}
