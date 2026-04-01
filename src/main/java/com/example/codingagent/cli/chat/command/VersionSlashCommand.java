package com.example.codingagent.cli.chat.command;

import com.example.codingagent.runtime.StatusSummaryService;
import org.springframework.stereotype.Component;

/**
 * 输出当前会话运行版本。
 */
@Component
public class VersionSlashCommand implements ChatSlashCommand {

    private final StatusSummaryService statusSummaryService;

    public VersionSlashCommand(StatusSummaryService statusSummaryService) {
        this.statusSummaryService = statusSummaryService;
    }

    @Override
    public String name() {
        return "version";
    }

    @Override
    public String description() {
        return "查看当前会话运行版本";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        String version = statusSummaryService.currentVersion();
        return ChatSlashCommandResult.output(java.util.List.of(version));
    }
}
