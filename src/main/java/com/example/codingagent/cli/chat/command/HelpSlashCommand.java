package com.example.codingagent.cli.chat.command;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * 输出交互式命令帮助。
 */
@Component
public class HelpSlashCommand implements ChatSlashCommand {

    private final List<ChatSlashCommand> commands;

    public HelpSlashCommand(List<ChatSlashCommand> commands) {
        this.commands = commands;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "查看可用 slash commands";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        List<String> lines = Stream.concat(Stream.of(this), commands.stream())
                .distinct()
                .sorted(Comparator.comparing(ChatSlashCommand::name))
                .map(command -> "/" + command.name() + "  " + command.description())
                .toList();
        return ChatSlashCommandResult.output(lines);
    }
}
