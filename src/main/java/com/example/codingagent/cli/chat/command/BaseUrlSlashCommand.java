package com.example.codingagent.cli.chat.command;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 查看或切换当前 chat 会话 base URL。
 */
@Component
public class BaseUrlSlashCommand implements ChatSlashCommand {

    @Override
    public String name() {
        return "base-url";
    }

    @Override
    public String description() {
        return "查看或切换当前会话 base URL";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        String argument = request.argument().trim();
        if (argument.isBlank()) {
            return ChatSlashCommandResult.output(List.of(
                    "Current base-url: " + fallback(request.sessionState().baseUrl(), "(default)")
            ));
        }
        if ("default".equalsIgnoreCase(argument)) {
            request.sessionState().updateBaseUrl(null);
            return ChatSlashCommandResult.output(List.of("Set base-url to (default)"));
        }
        request.sessionState().updateBaseUrl(argument);
        return ChatSlashCommandResult.output(List.of("Set base-url to " + argument));
    }

    private String fallback(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
