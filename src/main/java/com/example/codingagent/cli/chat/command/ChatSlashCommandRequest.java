package com.example.codingagent.cli.chat.command;

import com.example.codingagent.cli.chat.ChatSessionState;

/**
 * slash command 执行请求。
 */
public record ChatSlashCommandRequest(
        String rawLine,
        String commandName,
        String argument,
        ChatSessionState sessionState
) {
}
