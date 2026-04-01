package com.example.codingagent.cli.chat.command;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 退出交互式会话。
 */
@Component
public class ExitSlashCommand implements ChatSlashCommand {

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public String description() {
        return "结束当前 chat 会话";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        return ChatSlashCommandResult.exit(List.of("", "chat ended."));
    }
}
