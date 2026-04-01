# coding-agent-cli

`coding-agent-cli` 是一个 Java-first、Spring AI 驱动的 Coding Agent CLI，也是一个以 clean-room 方式实现的 Claude Code 核心运行时参考项目。

它的目标不是宣称“完整替代 Claude Code”，而是让 Java/Spring 团队可以直接研究这些关键问题如何在 JVM 生态落地：

- REPL-first 的会话入口如何组织
- tool-first 的 Agent Loop 如何闭环
- transcript 持久化、会话续跑、上下文压缩如何实现

这个仓库所有“已对齐”的结论都要求能被代码直接核验；未实现的部分会明确写成未对齐，而不是拿规划冒充能力。

## 技术点对齐速览

对齐判断只基于两类信息：

- 可独立核验的公开资料
- 本仓库里真实存在、可运行或可测试的代码

可核验公开来源：

- [MarcelLeon/claude-code](https://github.com/MarcelLeon/claude-code)
- [Axios: Anthropic leaked 500,000 lines of its own source code](https://www.axios.com/2026/03/31/anthropic-leaked-source-code-ai)
- [每日经济新闻 / 新浪财经：51 万行 Claude Code 源代码泄露](https://finance.sina.com.cn/roll/2026-04-01/doc-inhsywsw1723132.shtml)
- [爱范儿 / 新浪财经：Claude Code 源码泄露，50 万行代码被扒光](https://finance.sina.com.cn/tech/roll/2026-04-01/doc-inhsxvfa2803636.shtml)

| 技术点 | 对齐状态 | 代码入口 |
| --- | --- | --- |
| REPL-first 启动 | 已对齐 | [RootCommand.java](src/main/java/com/example/codingagent/cli/RootCommand.java), [ChatSessionRunner.java](src/main/java/com/example/codingagent/cli/chat/ChatSessionRunner.java) |
| Slash command 会话体验 | 已对齐 | [cli/chat/command](src/main/java/com/example/codingagent/cli/chat/command), [ChatSlashCommandDispatcher.java](src/main/java/com/example/codingagent/cli/chat/command/ChatSlashCommandDispatcher.java) |
| Tool-first Agent Loop | 已对齐 | [CodingAgentEngine.java](src/main/java/com/example/codingagent/runtime/CodingAgentEngine.java), [ToolExecutor.java](src/main/java/com/example/codingagent/tool/ToolExecutor.java) |
| 本地 coding 工具面 | 已对齐 | [tool](src/main/java/com/example/codingagent/tool) |
| Transcript 持久化与会话续跑 | 已对齐 | [TranscriptStore.java](src/main/java/com/example/codingagent/persistence/TranscriptStore.java), [SessionService.java](src/main/java/com/example/codingagent/runtime/SessionService.java) |
| 会话标题与上下文文件索引 | 已对齐 | [SessionMetadataStore.java](src/main/java/com/example/codingagent/persistence/SessionMetadataStore.java), [RenameSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/RenameSlashCommand.java), [FilesSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/FilesSlashCommand.java) |
| `status` / `cost` / `doctor` / `version` 会话视图 | 已对齐 | [StatusSummaryService.java](src/main/java/com/example/codingagent/runtime/StatusSummaryService.java), [CostSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/CostSlashCommand.java), [DoctorSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/DoctorSlashCommand.java), [VersionSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/VersionSlashCommand.java) |
| Provider / model / base-url 路由 | 已对齐 | [RoutingAgentModelGateway.java](src/main/java/com/example/codingagent/model/RoutingAgentModelGateway.java), [OpenAiAgentModelGateway.java](src/main/java/com/example/codingagent/model/OpenAiAgentModelGateway.java), [ModelSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/ModelSlashCommand.java) |
| 历史压缩与 prompt 注入治理 | 已对齐 | [OpenAiDecisionPromptBuilder.java](src/main/java/com/example/codingagent/model/OpenAiDecisionPromptBuilder.java), [AgentProperties.java](src/main/java/com/example/codingagent/config/AgentProperties.java) |
| 工作区路径和 shell 安全护栏 | 已对齐 | [WorkspacePathResolver.java](src/main/java/com/example/codingagent/tool/WorkspacePathResolver.java), [BashExecTool.java](src/main/java/com/example/codingagent/tool/BashExecTool.java) |
| 更完整的流式终端 UI / TUI 渲染 | 未对齐 | - |
| MCP / plugin marketplace / skills / daemon / bridge / remote control | 未对齐 | - |
| 超越当前 JSON 决策协议的完整结构化工具调用 | 未对齐 | - |

对齐状态说明：

- 已对齐：仓库中已有可运行或可测试的实际实现
- 未对齐：当前版本明确不声称具备该能力

## 诚实边界

这个项目的差异化，不是“喊口号说像 Claude Code”，而是把已完成和未完成的边界说清楚。

当前仍未对齐的重点能力包括：

- 更完整的流式终端 UI 与 richer TUI 渲染
- MCP、插件市场、skills 加载、daemon、bridge、remote control
- 超越当前 JSON 决策协议的完整结构化 tool-calling protocol
- 真实 provider token 计费、账号认证、策略治理
- 超出 `mock` 和 `openai` 之外的多 provider 对齐

如果你要找的是一个“现成完整替代 Claude Code 的生产工具”，这个仓库还不是。

如果你要找的是一个“能跑、能读、能改、能继续演进的 Java/Spring Agent runtime 参考实现”，这个仓库就是为这个目标准备的。

## 快速开始

最小验证：

```bash
sh ./mvnw test
printf '/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run
```

这两条命令分别用于验证测试闭环，以及验证默认 root 入口会直接进入 chat 会话。

如果你要接真实 OpenAI 兼容模型：

```bash
export OPENAI_API_KEY=your_key
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --provider openai --model gpt-4.1-mini --prompt '请分析当前工作区'"
```

更多 CLI 示例、chat 示例、会话恢复示例和 OpenAI 兼容地址示例见 [docs/examples.md](docs/examples.md)。

## 当前已落地能力

- 默认 root 入口直接进入交互式 chat
- `run`、`chat`、`doctor` 三类核心 CLI 命令
- `/help`、`/status`、`/tools`、`/files`、`/cost`、`/doctor`、`/version`、`/clear`、`/resume`、`/rename`、`/model`、`/provider`、`/base-url`、`/exit`、`/quit`
- `mock`、`openai` 两种 provider，以及配置文件、环境变量、命令行三级 base URL 覆盖
- `list_files`、`read_file`、`grep_text`、`bash_exec`、`write_file`、`patch_file` 六类本地工具
- `.agent/sessions` 下的 JSONL transcript 持久化、同 `session-id` 历史续跑、会话标题和上下文文件索引
- `/status`、`/cost`、`/doctor`、`/version` 这类会话内运行时视图
- transcript 压缩、工作区路径护栏、`bash_exec` 危险命令拦截

## 配置与继续阅读

当前默认配置见 [src/main/resources/application.yml](src/main/resources/application.yml)。

其中 transcript 相关默认值为：

- `agent.runtime.transcript.recent-entries=8`
- `agent.runtime.transcript.summary-entries=4`
- `agent.runtime.transcript.max-entry-chars=160`
- `agent.model.prompt.system-lines` 可覆写 OpenAI 决策器的 system prompt 文案

建议继续阅读：

- [docs/architecture.md](docs/architecture.md)
- [docs/progress.md](docs/progress.md)
- [docs/examples.md](docs/examples.md)
