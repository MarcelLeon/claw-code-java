package com.example.codingagent.cli.chat.command;

import com.example.codingagent.runtime.SessionStatusSummary;
import com.example.codingagent.runtime.StatusSummaryService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 输出当前 chat 会话状态。
 */
@Component
public class StatusSlashCommand implements ChatSlashCommand {

    private final StatusSummaryService statusSummaryService;

    public StatusSlashCommand(StatusSummaryService statusSummaryService) {
        this.statusSummaryService = statusSummaryService;
    }

    @Override
    public String name() {
        return "status";
    }

    @Override
    public String description() {
        return "查看当前会话与模型配置";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        SessionStatusSummary summary = statusSummaryService.summarize(
                request.sessionState().sessionId(),
                request.sessionState().provider(),
                request.sessionState().model(),
                request.sessionState().baseUrl()
        );
        return ChatSlashCommandResult.output(List.of(
                "app: " + summary.applicationName() + " " + fallback(summary.applicationVersion(), "(dev)"),
                "workspace: " + summary.workspaceRoot(),
                "session: " + summary.sessionId(),
                "title: " + fallback(summary.title(), "(untitled)"),
                "provider: " + fallback(summary.provider(), "(default)"),
                "model: " + fallback(summary.model(), "(default)"),
                "base-url: " + fallback(summary.baseUrl(), "(default)"),
                "api-key: " + (summary.apiKeyConfigured() ? "configured" : "not configured"),
                "connectivity: not checked (run doctor to verify)",
                "tools: " + summary.toolCount(),
                "context-files: " + summary.contextFileCount(),
                "max-turns: " + summary.maxTurns()
        ));
    }

    private String fallback(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
