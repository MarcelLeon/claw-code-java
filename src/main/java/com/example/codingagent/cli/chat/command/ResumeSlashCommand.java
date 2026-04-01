package com.example.codingagent.cli.chat.command;

import com.example.codingagent.runtime.SessionService;
import com.example.codingagent.runtime.SessionSummary;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 恢复已有会话。
 */
@Component
public class ResumeSlashCommand implements ChatSlashCommand {

    private static final int DEFAULT_LIST_LIMIT = 5;

    private final SessionService sessionService;

    public ResumeSlashCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String name() {
        return "resume";
    }

    @Override
    public List<String> aliases() {
        return List.of("continue");
    }

    @Override
    public String description() {
        return "恢复已有会话，或列出最近会话";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        String argument = request.argument().trim();
        if (argument.isBlank()) {
            return ChatSlashCommandResult.output(renderRecentSessions());
        }
        if (!sessionService.sessionExists(argument)) {
            return ChatSlashCommandResult.output(List.of(
                    "Session " + argument + " was not found.",
                    "Run /resume to view recent conversations."
            ));
        }
        request.sessionState().switchSession(argument);
        return ChatSlashCommandResult.output(List.of(
                "Resumed conversation " + argument + ".",
                "session: " + argument
        ));
    }

    private List<String> renderRecentSessions() {
        List<SessionSummary> sessions = sessionService.listRecentSessions(DEFAULT_LIST_LIMIT);
        if (sessions.isEmpty()) {
            return List.of("No conversations found to resume.");
        }
        List<String> lines = new ArrayList<>();
        lines.add("Recent conversations:");
        for (SessionSummary session : sessions) {
            lines.add("- " + session.sessionId() + "  " + session.preview());
        }
        lines.add("Run /resume <session-id> to switch to a conversation.");
        return lines;
    }
}
