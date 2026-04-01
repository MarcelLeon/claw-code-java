package com.example.codingagent.cli.chat.command;

import com.example.codingagent.tool.ToolCatalog;
import org.springframework.stereotype.Component;

/**
 * 展示当前可用工具。
 */
@Component
public class ToolsSlashCommand implements ChatSlashCommand {

    private final ToolCatalog toolCatalog;

    public ToolsSlashCommand(ToolCatalog toolCatalog) {
        this.toolCatalog = toolCatalog;
    }

    @Override
    public String name() {
        return "tools";
    }

    @Override
    public String description() {
        return "查看当前可用工具";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        return ChatSlashCommandResult.output(toolCatalog.listTools().stream()
                .map(tool -> "- " + tool.name() + ": " + tool.description())
                .toList());
    }
}
