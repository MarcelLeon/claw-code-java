package com.example.codingagent.cli.chat.command;

import com.example.codingagent.runtime.SessionService;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 重命名当前会话。
 */
@Component
public class RenameSlashCommand implements ChatSlashCommand {

    private final SessionService sessionService;

    public RenameSlashCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String name() {
        return "rename";
    }

    @Override
    public String description() {
        return "重命名当前会话";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        String argument = request.argument().trim();
        String title = argument;
        if (argument.isBlank()) {
            title = sessionService.generateSessionTitle(request.sessionState().sessionId());
            if (title == null || title.isBlank()) {
                return ChatSlashCommandResult.output(List.of(
                        "Could not generate a name: no conversation context yet. Usage: /rename <name>"
                ));
            }
        }
        sessionService.renameSession(request.sessionState().sessionId(), title);
        return ChatSlashCommandResult.output(List.of("Session renamed to: " + title));
    }
}
