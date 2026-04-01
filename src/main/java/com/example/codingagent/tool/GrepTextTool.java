package com.example.codingagent.tool;

import com.example.codingagent.runtime.AgentRuntimeContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * 在工作区中搜索文本。
 */
@Component
public class GrepTextTool implements WorkspaceTool {

    @Override
    public String name() {
        return "grep_text";
    }

    @Override
    public String description() {
        return "在工作区中递归搜索关键字，返回命中的文件名、行号和内容。参数示例：Agent";
    }

    @Override
    public ToolArgumentDescriptor argumentDescriptor() {
        return ToolArgumentDescriptor.plainText("要搜索的关键字", "Agent");
    }

    @Override
    public ToolExecutionResult execute(AgentRuntimeContext context, String argument) {
        List<String> matches = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(context.workspaceRoot(), 3)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> collectMatches(argument, path, matches));
            String output = matches.isEmpty() ? "未找到匹配项" : String.join(System.lineSeparator(), matches);
            return new ToolExecutionResult(name(), "已完成关键字搜索", output);
        } catch (IOException ex) {
            return new ToolExecutionResult(name(), "搜索失败", ex.getMessage());
        }
    }

    private void collectMatches(String keyword, Path path, List<String> matches) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.contains(keyword)) {
                    matches.add(path.getFileName() + ":" + (i + 1) + ": " + line.trim());
                }
            }
        } catch (IOException ignored) {
            // 跳过不可读文件，保持本地工具尽量 fail-soft。
        }
    }
}
