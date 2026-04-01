package com.example.codingagent.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.model.protocol.JsonAgentDecisionProtocol;
import com.example.codingagent.persistence.TranscriptEntry;
import com.example.codingagent.runtime.AgentRuntimeContext;
import com.example.codingagent.runtime.ConversationState;
import com.example.codingagent.runtime.AgentSession;
import com.example.codingagent.tool.ToolExecutionResult;
import com.example.codingagent.tool.WorkspaceTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;

class OpenAiDecisionPromptBuilderTest {

    @Test
    void shouldUseConfiguredSystemPromptLines() {
        AgentProperties properties = new AgentProperties();
        properties.getModel().getPrompt().setSystemLines(List.of(
                "自定义身份",
                "自定义规则"
        ));
        OpenAiDecisionPromptBuilder builder = new OpenAiDecisionPromptBuilder(
                properties,
                List.of(fakeTool()),
                protocol()
        );

        Prompt prompt = builder.build(new AgentRequestContext(
                runtimeContext(),
                "请继续",
                conversationState(List.of(), List.of())
        ));

        String systemText = messageText(prompt, 0);
        assertThat(systemText).contains("自定义身份");
        assertThat(systemText).contains("自定义规则");
        assertThat(systemText).contains("必须只输出 JSON");
        assertThat(systemText).contains("可用工具：");
        assertThat(systemText).contains("- read_file: 读取文件");
    }

    @Test
    void shouldSummarizeOlderTranscriptEntriesAndKeepRecentRecords() {
        AgentProperties properties = new AgentProperties();
        properties.getRuntime().getTranscript().setRecentEntries(3);
        properties.getRuntime().getTranscript().setSummaryEntries(2);
        properties.getRuntime().getTranscript().setMaxEntryChars(24);
        OpenAiDecisionPromptBuilder builder = new OpenAiDecisionPromptBuilder(
                properties,
                List.of(fakeTool()),
                protocol()
        );

        List<TranscriptEntry> transcriptEntries = List.of(
                new TranscriptEntry("user", "第一轮用户问题需要较长描述", null),
                new TranscriptEntry("assistant", "第一轮回答说明已经完成初始分析", null),
                new TranscriptEntry("tool", "read_file -> README 第一段内容", null),
                new TranscriptEntry("user", "第二轮继续追问更多细节", null),
                new TranscriptEntry("assistant", "第二轮回答给出新的方向", null),
                new TranscriptEntry("tool", "grep_text -> docs/progress.md 命中", null)
        );

        Prompt prompt = builder.build(new AgentRequestContext(
                runtimeContext(),
                "请继续",
                conversationState(
                        transcriptEntries,
                        List.of(new ToolExecutionResult("read_file", "读取完成", "README 内容"))
                )
        ));

        String userText = messageText(prompt, 1);
        assertThat(userText).contains("较早历史摘要: 共 3 条，user 1 条，assistant 1 条，tool 1 条");
        assertThat(userText).contains("较早片段: assistant: 第一轮回答说明已经完成初始分析");
        assertThat(userText).contains("较早片段: tool: read_file -> README 第一段内...");
        assertThat(userText).contains("最近记录: user: 第二轮继续追问更多细节");
        assertThat(userText).contains("最近记录: assistant: 第二轮回答给出新的方向");
        assertThat(userText).contains("最近记录: tool: grep_text -> docs/progre...");
        assertThat(userText).doesNotContain("较早片段: user: 第一轮用户问题需要较长描述");
    }

    @Test
    void shouldKeepAllTranscriptEntriesWhenWindowIsLargeEnough() {
        AgentProperties properties = new AgentProperties();
        properties.getRuntime().getTranscript().setRecentEntries(10);
        OpenAiDecisionPromptBuilder builder = new OpenAiDecisionPromptBuilder(
                properties,
                List.of(fakeTool()),
                protocol()
        );

        Prompt prompt = builder.build(new AgentRequestContext(
                runtimeContext(),
                "请继续",
                conversationState(
                        List.of(
                                new TranscriptEntry("user", "第一条", null),
                                new TranscriptEntry("assistant", "第二条", null)
                        ),
                        List.of()
                )
        ));

        String userText = messageText(prompt, 1);
        assertThat(userText).doesNotContain("较早历史摘要");
        assertThat(userText).contains("最近记录: user: 第一条");
        assertThat(userText).contains("最近记录: assistant: 第二条");
    }

    private AgentRuntimeContext runtimeContext() {
        return new AgentRuntimeContext(
                Path.of(".").toAbsolutePath().normalize(),
                "openai",
                "gpt-4.1-mini",
                "https://api.openai.com",
                8,
                new AgentSession("session-1", Path.of(".agent/sessions/session-1.jsonl").toAbsolutePath().normalize())
        );
    }

    private WorkspaceTool fakeTool() {
        return new WorkspaceTool() {
            @Override
            public String name() {
                return "read_file";
            }

            @Override
            public String description() {
                return "读取文件";
            }

            @Override
            public ToolExecutionResult execute(AgentRuntimeContext context, String argument) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private JsonAgentDecisionProtocol protocol() {
        return new JsonAgentDecisionProtocol(new ObjectMapper(), new AgentDecisionValidator());
    }

    private ConversationState conversationState(
            List<TranscriptEntry> transcriptEntries,
            List<ToolExecutionResult> toolResults
    ) {
        ConversationState conversationState = new ConversationState(transcriptEntries);
        for (ToolExecutionResult toolResult : toolResults) {
            conversationState.addToolResult(toolResult);
        }
        return conversationState;
    }

    private String messageText(Prompt prompt, int index) {
        return prompt.getInstructions().stream()
                .map(Message::getText)
                .collect(Collectors.toList())
                .get(index);
    }
}
