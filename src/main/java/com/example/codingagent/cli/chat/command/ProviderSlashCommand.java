package com.example.codingagent.cli.chat.command;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 查看或切换当前 chat 会话 provider。
 */
@Component
public class ProviderSlashCommand implements ChatSlashCommand {

    @Override
    public String name() {
        return "provider";
    }

    @Override
    public String description() {
        return "查看或切换当前会话 provider";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        String argument = request.argument().trim();
        if (argument.isBlank()) {
            return ChatSlashCommandResult.output(List.of(
                    "Current provider: " + fallback(request.sessionState().provider(), "(default)")
            ));
        }
        if ("default".equalsIgnoreCase(argument)) {
            request.sessionState().updateProvider(null);
            return ChatSlashCommandResult.output(List.of("Set provider to (default)"));
        }
        request.sessionState().updateProvider(argument);
        return ChatSlashCommandResult.output(List.of("Set provider to " + argument));
    }

    private String fallback(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
