package com.example.codingagent.tool;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 提供工具目录查询能力。
 */
@Component
public class ToolCatalog {

    private final List<WorkspaceTool> tools;

    public ToolCatalog(List<WorkspaceTool> tools) {
        this.tools = tools;
    }

    /**
     * 返回当前可用工具列表。
     *
     * @return 工具描述列表
     */
    public List<ToolDescriptor> listTools() {
        return tools.stream()
                .map(tool -> new ToolDescriptor(tool.name(), tool.description()))
                .sorted(Comparator.comparing(ToolDescriptor::name))
                .toList();
    }
}
