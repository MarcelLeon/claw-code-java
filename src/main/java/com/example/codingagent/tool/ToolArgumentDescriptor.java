package com.example.codingagent.tool;

import java.util.List;

/**
 * 工具参数契约描述。
 */
public record ToolArgumentDescriptor(
        ToolArgumentKind kind,
        String summary,
        String example,
        List<ToolParameterDescriptor> parameters
) {

    public ToolArgumentDescriptor {
        parameters = parameters == null ? List.of() : List.copyOf(parameters);
    }

    /**
     * 构造纯文本参数契约。
     *
     * @param summary 参数摘要
     * @param example 参数示例
     * @return 契约描述
     */
    public static ToolArgumentDescriptor plainText(String summary, String example) {
        return new ToolArgumentDescriptor(ToolArgumentKind.PLAIN_TEXT, summary, example, List.of());
    }

    /**
     * 构造 JSON 对象参数契约。
     *
     * @param summary 参数摘要
     * @param example JSON 示例
     * @param parameters 字段描述
     * @return 契约描述
     */
    public static ToolArgumentDescriptor jsonObject(
            String summary,
            String example,
            List<ToolParameterDescriptor> parameters
    ) {
        return new ToolArgumentDescriptor(ToolArgumentKind.JSON_OBJECT, summary, example, parameters);
    }
}
