package com.example.codingagent.cli;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

/**
 * Picocli 顶层入口。
 */
@Component
public class CodingAgentCliCommand {

    private final RootCommand rootCommand;
    private final IFactory factory;
    private final CliExecutionExceptionHandler exceptionHandler;
    private CommandLine commandLine;

    public CodingAgentCliCommand(
            RootCommand rootCommand,
            IFactory factory,
            CliExecutionExceptionHandler exceptionHandler
    ) {
        this.rootCommand = rootCommand;
        this.factory = factory;
        this.exceptionHandler = exceptionHandler;
    }

    @PostConstruct
    void init() {
        this.commandLine = new CommandLine(rootCommand, factory);
        this.commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        this.commandLine.setExecutionExceptionHandler(exceptionHandler);
    }

    /**
     * 执行命令行。
     *
     * @param args 命令参数
     */
    public void run(String[] args) {
        int exitCode = commandLine.execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
