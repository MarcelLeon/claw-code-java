package com.example.codingagent.cli.chat.command;

import java.util.List;

/**
 * chat 会话中的 slash command。
 */
public interface ChatSlashCommand {

    /**
     * 命令名，不包含前导 `/`。
     *
     * @return 命令名
     */
    String name();

    /**
     * 命令别名，不包含前导 `/`。
     *
     * @return 别名列表
     */
    default List<String> aliases() {
        return List.of();
    }

    /**
     * 命令帮助描述。
     *
     * @return 命令说明
     */
    String description();

    /**
     * 执行 slash command。
     *
     * @param request 命令请求
     * @return 命令结果
     */
    ChatSlashCommandResult execute(ChatSlashCommandRequest request);
}
