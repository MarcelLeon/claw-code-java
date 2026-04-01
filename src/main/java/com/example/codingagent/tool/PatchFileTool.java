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
 * 基于精确片段对文件做局部替换。
 */
@Component
public class PatchFileTool implements WorkspaceTool {

    private final ObjectMapper objectMapper;

    public PatchFileTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return "patch_file";
    }

    @Override
    public String description() {
        return "按精确文本片段修改文件。argument 需为 JSON 字符串，例如：{\"path\":\"src/App.java\",\"findText\":\"old\",\"replaceText\":\"new\",\"expectedMatches\":1}";
    }

    @Override
    public ToolArgumentDescriptor argumentDescriptor() {
        return ToolArgumentDescriptor.jsonObject(
                "JSON 对象，按精确文本片段修改文件",
                "{\"path\":\"src/App.java\",\"findText\":\"old\",\"replaceText\":\"new\",\"expectedMatches\":1}",
                List.of(
                        new ToolParameterDescriptor("path", "string", true, "工作区内相对文件路径"),
                        new ToolParameterDescriptor("findText", "string", true, "要匹配的原始文本"),
                        new ToolParameterDescriptor("replaceText", "string", true, "替换后的文本"),
                        new ToolParameterDescriptor("expectedMatches", "integer", false, "期望匹配次数，默认 1")
                )
        );
    }

    @Override
    public ToolExecutionResult execute(AgentRuntimeContext context, String argument) {
        try {
            PatchFileRequest request = objectMapper.readValue(argument, PatchFileRequest.class);
            Path target = WorkspacePathResolver.resolve(context, request.path());
            String original = Files.readString(target);
            int actualMatches = countMatches(original, request.findText());
            int expectedMatches = request.expectedMatches() == null ? 1 : request.expectedMatches();
            if (actualMatches != expectedMatches) {
                return new ToolExecutionResult(
                        name(),
                        "补丁未应用",
                        "匹配次数不符合预期，expected=" + expectedMatches + ", actual=" + actualMatches
                );
            }
            String patched = original.replace(request.findText(), request.replaceText());
            Files.writeString(target, patched, StandardCharsets.UTF_8);
            return new ToolExecutionResult(
                    name(),
                    "已应用补丁 " + request.path(),
                    "匹配 " + actualMatches + " 处并完成替换"
            );
        } catch (IllegalArgumentException ex) {
            return new ToolExecutionResult(name(), "补丁应用失败", ex.getMessage());
        } catch (IOException ex) {
            return new ToolExecutionResult(name(), "补丁应用失败", ex.getMessage());
        }
    }

    private int countMatches(String source, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("findText 不能为空");
        }
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(keyword, index)) >= 0) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    /**
     * 补丁参数。
     */
    private record PatchFileRequest(
            String path,
            String findText,
            String replaceText,
            Integer expectedMatches
    ) {
    }
}
