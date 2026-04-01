package com.example.codingagent.tool;

import com.example.codingagent.runtime.AgentRuntimeContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 列出目录内容。
 */
@Component
public class ListFilesTool implements WorkspaceTool {

    @Override
    public String name() {
        return "list_files";
    }

    @Override
    public String description() {
        return "列出指定相对目录下的文件和目录名称。参数示例：.";
    }

    @Override
    public ToolExecutionResult execute(AgentRuntimeContext context, String argument) {
        try {
            Path target = WorkspacePathResolver.resolve(context, argument);
            try (var stream = Files.list(target)) {
            String output = stream
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .collect(Collectors.joining(System.lineSeparator()));
            return new ToolExecutionResult(name(), "已列出目录 " + target, output);
            }
        } catch (IllegalArgumentException ex) {
            return new ToolExecutionResult(name(), "列出目录失败", ex.getMessage());
        } catch (IOException ex) {
            return new ToolExecutionResult(name(), "列出目录失败", ex.getMessage());
        }
    }
}
