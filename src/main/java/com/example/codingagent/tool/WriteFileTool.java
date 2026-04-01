package com.example.codingagent.tool;

import com.example.codingagent.runtime.AgentRuntimeContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 把文本内容写入工作区文件。
 */
@Component
public class WriteFileTool implements WorkspaceTool {

    private final ObjectMapper objectMapper;

    public WriteFileTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return "write_file";
    }

    @Override
    public String description() {
        return "写入文本文件。argument 需为 JSON 字符串，例如：{\"path\":\"notes/todo.txt\",\"content\":\"hello\"}";
    }

    @Override
    public ToolArgumentDescriptor argumentDescriptor() {
        return ToolArgumentDescriptor.jsonObject(
                "JSON 对象，写入文件路径与内容",
                "{\"path\":\"notes/todo.txt\",\"content\":\"hello\"}",
                List.of(
                        new ToolParameterDescriptor("path", "string", true, "工作区内相对文件路径"),
                        new ToolParameterDescriptor("content", "string", true, "要写入的完整文本内容")
                )
        );
    }

    @Override
    public ToolExecutionResult execute(AgentRuntimeContext context, String argument) {
        try {
            WriteFileRequest request = objectMapper.readValue(argument, WriteFileRequest.class);
            Path target = WorkspacePathResolver.resolve(context, request.path());
            if (target.getParent() != null) {
                Files.createDirectories(target.getParent());
            }
            Files.writeString(target, request.content(), StandardCharsets.UTF_8);
            return new ToolExecutionResult(name(), "已写入文件 " + request.path(), target.toString());
        } catch (IllegalArgumentException ex) {
            return new ToolExecutionResult(name(), "写入文件失败", ex.getMessage());
        } catch (IOException ex) {
            return new ToolExecutionResult(name(), "写入文件失败", ex.getMessage());
        }
    }

    /**
     * 写文件参数。
     */
    private record WriteFileRequest(String path, String content) {
    }
}
