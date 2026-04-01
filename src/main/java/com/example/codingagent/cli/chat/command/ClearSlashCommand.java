package com.example.codingagent.cli.chat.command;

import com.example.codingagent.runtime.SessionService;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 清空当前对话并切换到新会话。
 */
@Component
public class ClearSlashCommand implements ChatSlashCommand {

    private final SessionService sessionService;

    public ClearSlashCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public List<String> aliases() {
        return List.of("reset", "new");
    }

    @Override
    public String description() {
        return "清空当前对话并创建新会话";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        String newSessionId = sessionService.createSessionId();
        request.sessionState().switchSession(newSessionId);
        return ChatSlashCommandResult.output(List.of(
                "Started a new conversation.",
                "session: " + newSessionId
        ));
    }
}
