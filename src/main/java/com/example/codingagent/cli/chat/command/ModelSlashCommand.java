package com.example.codingagent.cli.chat.command;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 查看或切换当前 chat 会话模型。
 */
@Component
public class ModelSlashCommand implements ChatSlashCommand {

    @Override
    public String name() {
        return "model";
    }

    @Override
    public String description() {
        return "查看或切换当前会话模型";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        String argument = request.argument().trim();
        if (argument.isBlank()) {
            return ChatSlashCommandResult.output(List.of(
                    "Current model: " + fallback(request.sessionState().model(), "(default)")
            ));
        }
        if ("default".equalsIgnoreCase(argument)) {
            request.sessionState().updateModel(null);
            return ChatSlashCommandResult.output(List.of("Set model to (default)"));
        }
        request.sessionState().updateModel(argument);
        return ChatSlashCommandResult.output(List.of("Set model to " + argument));
    }

    private String fallback(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
