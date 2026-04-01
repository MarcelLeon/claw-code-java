package com.example.codingagent.tool;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.runtime.AgentRuntimeContext;
import org.junit.jupiter.api.Test;

/**
 * 工具目录契约测试。
 */
class ToolCatalogTest {

    @Test
    void shouldExposeArgumentDescriptor() {
        ToolCatalog toolCatalog = new ToolCatalog(java.util.List.of(new WorkspaceTool() {
            @Override
            public String name() {
                return "write_file";
            }

            @Override
            public String description() {
                return "写文件";
            }

            @Override
            public ToolArgumentDescriptor argumentDescriptor() {
                return ToolArgumentDescriptor.jsonObject(
                        "JSON 参数",
                        "{\"path\":\"README.md\",\"content\":\"hello\"}",
                        java.util.List.of(
                                new ToolParameterDescriptor("path", "string", true, "路径"),
                                new ToolParameterDescriptor("content", "string", true, "内容")
                        )
                );
            }

            @Override
            public ToolExecutionResult execute(AgentRuntimeContext context, String argument) {
                throw new UnsupportedOperationException();
            }
        }));

        ToolDescriptor descriptor = toolCatalog.listTools().get(0);
        assertThat(descriptor.argumentDescriptor().kind()).isEqualTo(ToolArgumentKind.JSON_OBJECT);
        assertThat(descriptor.argumentDescriptor().parameters()).hasSize(2);
        assertThat(descriptor.argumentDescriptor().example()).contains("\"path\"");
    }
}
