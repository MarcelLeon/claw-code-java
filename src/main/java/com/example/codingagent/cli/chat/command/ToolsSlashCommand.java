package com.example.codingagent.cli.chat.command;

import com.example.codingagent.tool.ToolArgumentKind;
import com.example.codingagent.tool.ToolCatalog;
import com.example.codingagent.tool.ToolDescriptor;
import java.util.ArrayList;
import java.util.List;
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
        List<String> lines = new ArrayList<>();
        for (ToolDescriptor tool : toolCatalog.listTools()) {
            lines.add("- " + tool.name() + ": " + tool.description());
            lines.add("  argument-kind: " + renderArgumentKind(tool));
            if (!tool.argumentDescriptor().parameters().isEmpty()) {
                tool.argumentDescriptor().parameters().forEach(parameter -> lines.add("  field: "
                        + parameter.name()
                        + " <" + parameter.type() + ">"
                        + (parameter.required() ? " required" : " optional")));
            }
            if (tool.argumentDescriptor().example() != null && !tool.argumentDescriptor().example().isBlank()) {
                lines.add("  example: " + tool.argumentDescriptor().example());
            }
        }
        return ChatSlashCommandResult.output(lines);
    }

    private String renderArgumentKind(ToolDescriptor tool) {
        return tool.argumentDescriptor().kind() == ToolArgumentKind.JSON_OBJECT
                ? "json_object"
                : "plain_text";
    }
}
