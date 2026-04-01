package com.example.codingagent.cli.chat.command;

import com.example.codingagent.runtime.DoctorSummary;
import com.example.codingagent.runtime.DoctorSummaryService;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 在 chat 中输出诊断信息。
 */
@Component
public class DoctorSlashCommand implements ChatSlashCommand {

    private final DoctorSummaryService doctorSummaryService;

    public DoctorSlashCommand(DoctorSummaryService doctorSummaryService) {
        this.doctorSummaryService = doctorSummaryService;
    }

    @Override
    public String name() {
        return "doctor";
    }

    @Override
    public String description() {
        return "诊断当前运行环境";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        DoctorSummary summary = doctorSummaryService.summarize();
        return ChatSlashCommandResult.output(List.of(
                "Java version: " + summary.javaVersion(),
                "workspace-root: " + summary.workspaceRoot(),
                "sessions-dir: " + summary.sessionsDir(),
                "transcript.recent-entries: " + summary.transcriptRecentEntries(),
                "transcript.summary-entries: " + summary.transcriptSummaryEntries(),
                "transcript.max-entry-chars: " + summary.transcriptMaxEntryChars(),
                "shell.timeout-seconds: " + summary.shellTimeoutSeconds(),
                "shell.blocked-patterns: " + summary.shellBlockedPatternCount(),
                "default-provider: " + summary.defaultProvider(),
                "default-model: " + summary.defaultModel(),
                "resolved-base-url: " + summary.resolvedBaseUrl(),
                "status: OK"
        ));
    }
}
