package com.example.codingagent.cli.chat.command;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 分发交互式 slash command。
 */
@Component
public class ChatSlashCommandDispatcher {

    private final List<ChatSlashCommand> commands;

    public ChatSlashCommandDispatcher(List<ChatSlashCommand> commands) {
        this.commands = commands;
    }

    /**
     * 处理一条 slash command 输入。
     *
     * @param request 命令请求
     * @return 命令结果
     */
    public ChatSlashCommandResult dispatch(ChatSlashCommandRequest request) {
        return commands.stream()
                .filter(command -> command.name().equalsIgnoreCase(request.commandName()))
                .findFirst()
                .orElseGet(() -> unknownCommand(request.commandName()))
                .execute(request);
    }

    private ChatSlashCommand unknownCommand(String commandName) {
        return new ChatSlashCommand() {
            @Override
            public String name() {
                return "__unknown__";
            }

            @Override
            public String description() {
                return "未知命令";
            }

            @Override
            public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
                return ChatSlashCommandResult.output(List.of(
                        "未知命令: /" + commandName,
                        "输入 /help 查看可用命令。"
                ));
            }
        };
    }
}
