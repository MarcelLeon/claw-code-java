package com.example.codingagent.runtime;

import java.util.List;

/**
 * 单次执行结果。
 */
public record RunResult(
        String sessionId,
        String finalAnswer,
        int turns,
        List<String> steps
) {

    /**
     * 渲染为终端输出。
     *
     * @return 文本结果
     */
    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append("session: ").append(sessionId).append(System.lineSeparator());
        builder.append("turns: ").append(turns).append(System.lineSeparator());
        builder.append("steps:").append(System.lineSeparator());
        for (String step : steps) {
            builder.append("- ").append(step).append(System.lineSeparator());
        }
        builder.append("answer:").append(System.lineSeparator());
        builder.append(finalAnswer);
        return builder.toString();
    }
}
