package com.example.codingagent.tool;

import com.example.codingagent.runtime.AgentRuntimeContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

/**
 * 读取文件内容。
 */
@Component
public class ReadFileTool implements WorkspaceTool {

    @Override
    public String name() {
        return "read_file";
    }

    @Override
    public String description() {
        return "读取指定相对文件路径的完整文本内容。参数示例：README.md";
    }

    @Override
    public ToolExecutionResult execute(AgentRuntimeContext context, String argument) {
        try {
            Path target = WorkspacePathResolver.resolve(context, argument);
            String content = Files.readString(target);
            return new ToolExecutionResult(name(), "已读取文件 " + target.getFileName(), content);
        } catch (IllegalArgumentException ex) {
            return new ToolExecutionResult(name(), "读取文件失败", ex.getMessage());
        } catch (IOException ex) {
            return new ToolExecutionResult(name(), "读取文件失败", ex.getMessage());
        }
    }
}
