package com.example.codingagent.cli.chat.command;

import com.example.codingagent.runtime.DurationFormatter;
import com.example.codingagent.runtime.SessionCostSummary;
import com.example.codingagent.runtime.SessionService;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 展示当前会话的本地成本统计。
 */
@Component
public class CostSlashCommand implements ChatSlashCommand {

    private final SessionService sessionService;

    public CostSlashCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String name() {
        return "cost";
    }

    @Override
    public String description() {
        return "查看当前会话的本地成本统计";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        SessionCostSummary summary = sessionService.summarizeCost(request.sessionState().sessionId());
        return ChatSlashCommandResult.output(List.of(
                "session: " + request.sessionState().sessionId(),
                "messages: total=" + summary.totalMessages()
                        + ", user=" + summary.userMessages()
                        + ", assistant=" + summary.assistantMessages()
                        + ", tool=" + summary.toolMessages(),
                "duration: " + renderDuration(summary),
                "transcript-chars: " + summary.totalCharacters(),
                "tool-output-chars: " + summary.toolOutputCharacters(),
                "context-files: " + summary.contextFileCount(),
                "billing: unavailable (provider token usage not tracked yet)",
                "note: this is a local transcript-based estimate"
        ));
    }

    private String renderDuration(SessionCostSummary summary) {
        if (summary.startedAt() == null || summary.lastUpdatedAt() == null) {
            return "unavailable (legacy transcript has no timestamps)";
        }
        return DurationFormatter.formatSeconds(summary.durationSeconds());
    }
}
