package com.example.codingagent.cli.chat.command;

import java.util.List;

/**
 * slash command 执行结果。
 */
public record ChatSlashCommandResult(
        List<String> outputLines,
        boolean shouldExit
) {

    /**
     * 构造普通输出结果。
     *
     * @param lines 输出行
     * @return 结果
     */
    public static ChatSlashCommandResult output(List<String> lines) {
        return new ChatSlashCommandResult(lines, false);
    }

    /**
     * 构造退出结果。
     *
     * @param lines 输出行
     * @return 结果
     */
    public static ChatSlashCommandResult exit(List<String> lines) {
        return new ChatSlashCommandResult(lines, true);
    }
}
