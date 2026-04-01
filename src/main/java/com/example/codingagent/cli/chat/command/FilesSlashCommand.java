package com.example.codingagent.cli.chat.command;

import com.example.codingagent.runtime.SessionService;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 查看当前会话上下文中的文件。
 */
@Component
public class FilesSlashCommand implements ChatSlashCommand {

    private final SessionService sessionService;

    public FilesSlashCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String name() {
        return "files";
    }

    @Override
    public String description() {
        return "查看当前上下文中的文件";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        List<String> files = sessionService.listContextFiles(request.sessionState().sessionId());
        if (files.isEmpty()) {
            return ChatSlashCommandResult.output(List.of("No files in context"));
        }
        return ChatSlashCommandResult.output(List.of(
                "Files in context:",
                String.join(System.lineSeparator(), files)
        ));
    }
}
