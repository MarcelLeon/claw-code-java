package com.example.codingagent.cli.chat.command;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 输出当前 chat 会话状态。
 */
@Component
public class StatusSlashCommand implements ChatSlashCommand {

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
        return ChatSlashCommandResult.output(List.of(
                "session: " + request.sessionState().sessionId(),
                "provider: " + fallback(request.sessionState().provider(), "(default)"),
                "model: " + fallback(request.sessionState().model(), "(default)"),
                "base-url: " + fallback(request.sessionState().baseUrl(), "(default)")
        ));
    }

    private String fallback(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
