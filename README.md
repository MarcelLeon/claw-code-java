# Coding Agent CLI

Java-first 的 Coding Agent CLI，目标是以 Spring AI 为模型接入层，逐步复刻 Claude Code 一类企业级编码 agent 的关键能力，并保持结构清晰、可学习、可持续迭代。

Java-first Coding Agent CLI built on Spring AI, aiming to recreate the core runtime patterns of Claude Code in a clean-room, behavior-oriented way.

## Claude Code Technical Parity Table / Claude Code 技术点对标表

2026 年 3 月底的 Claude Code 源码泄露，让外界第一次能系统性观察它的运行时设计。短内容平台上也出现了大量“深扒”图文，但这里有一个原则：

- We do not cite unverified like counts or inaccessible reposts.
- 我们不写无法独立核验的“点赞数”“爆文截图”。
- This table only uses verifiable public sources plus code that exists in this repository.
- 下表只使用可验证的公开来源，以及本仓库里确实存在的代码实现。

Verified public sources / 可核验来源：

- [MarcelLeon/claude-code](https://github.com/MarcelLeon/claude-code)
- [Axios: Anthropic leaked 500,000 lines of its own source code](https://www.axios.com/2026/03/31/anthropic-leaked-source-code-ai)
- [每日经济新闻 / 新浪财经：51 万行 Claude Code 源代码泄露](https://finance.sina.com.cn/roll/2026-04-01/doc-inhsywsw1723132.shtml)
- [爱范儿 / 新浪财经：Claude Code 源码泄露，50 万行代码被扒光](https://finance.sina.com.cn/tech/roll/2026-04-01/doc-inhsxvfa2803636.shtml)

The goal of the table below is simple: when someone reads a leak-era technical point, they can immediately see whether this Java version has actually aligned to it, and jump to the exact code.

下表的目标很直接：别人看到一条“Claude Code 泄露后被热议的技术点”，可以立刻判断这个 Java 版本有没有真实对齐，并直接点到代码里核验。

| Public Claude Code clue / 公开线索 | Our actual alignment / 本项目已实际对齐 | Code refs / 代码引用 |
| --- | --- | --- |
| REPL-first startup / 默认进入 REPL | Leak-era browsing showed Claude Code launches into a REPL-style session rather than acting like a one-shot wrapper. / 泄露后的源码浏览显示 Claude Code 默认是 REPL 风格。 | We already do this: no subcommand enters chat directly. / 本项目已对齐：无子命令直接进入 chat。 | [RootCommand.java](src/main/java/com/example/codingagent/cli/RootCommand.java), [ChatSessionRunner.java](src/main/java/com/example/codingagent/cli/chat/ChatSessionRunner.java) |
| Slash-command rich session UX / 丰富的 slash command 会话体验 | Public analyses repeatedly highlighted command-heavy workflow and session control. / 公开深扒里反复提到命令面和会话控制。 | We already support `/help`, `/status`, `/tools`, `/files`, `/cost`, `/doctor`, `/version`, `/clear`, `/resume`, `/rename`, `/model`, `/provider`, `/base-url`, `/exit`, `/quit`. / 本项目已实现一组核心命令。 | [cli/chat/command](src/main/java/com/example/codingagent/cli/chat/command), [ChatSlashCommandDispatcher.java](src/main/java/com/example/codingagent/cli/chat/command/ChatSlashCommandDispatcher.java) |
| Tool-first agent loop / 工具优先的 Agent Loop | Source mirror made it obvious Claude Code is built around tools, not plain answer generation. / 镜像源码非常清楚地说明它是工具优先而不是纯回答生成。 | Our runtime already performs decide -> tool -> transcript -> decide loops. / 我们的 runtime 已经具备决策、调工具、回填 transcript、继续决策的闭环。 | [CodingAgentEngine.java](src/main/java/com/example/codingagent/runtime/CodingAgentEngine.java), [ToolExecutor.java](src/main/java/com/example/codingagent/tool/ToolExecutor.java) |
| Local coding tool surface / 本地编程工具面 | Leak reports and mirrored code both emphasized a large tool surface. / 泄露报道和镜像都强调了 Claude Code 很重的工具层。 | Current Java version already includes `bash_exec`, `read_file`, `write_file`, `patch_file`, `grep_text`, `list_files`. / 当前 Java 版已实现基础 coding 工具面。 | [tool](src/main/java/com/example/codingagent/tool) |
| Session transcript persistence / 会话 transcript 持久化 | Leak discussions repeatedly mentioned transcript, resume, and long-lived sessions. / transcript、resume、长会话是泄露分析里的高频词。 | We persist JSONL transcripts and auto-reload them on the same `session-id`. / 我们已经持久化 JSONL transcript，并支持同 session 自动续跑。 | [TranscriptStore.java](src/main/java/com/example/codingagent/persistence/TranscriptStore.java), [SessionService.java](src/main/java/com/example/codingagent/runtime/SessionService.java) |
| Resume as a first-class workflow / 会话恢复是一等公民 | Public mirror includes `/resume` and session listing behavior. / 公共镜像里明确存在 `/resume` 与最近会话列表。 | We already support listing recent sessions and switching back into an old conversation. / 本项目已支持列出最近会话并恢复。 | [ResumeSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/ResumeSlashCommand.java), [SessionSummary.java](src/main/java/com/example/codingagent/runtime/SessionSummary.java) |
| Session metadata beyond raw messages / 除消息正文外的会话元数据 | Leak-era code browsing showed session identity is more than prompt history. / 泄露后的代码浏览能看到会话不仅仅是 prompt history。 | We already persist custom titles and context file lists, and surface them in `/resume`, `/status`, `/files`. / 本项目已持久化标题和上下文文件列表。 | [SessionMetadataStore.java](src/main/java/com/example/codingagent/persistence/SessionMetadataStore.java), [RenameSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/RenameSlashCommand.java), [FilesSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/FilesSlashCommand.java) |
| Status and cost as session views / `status` 与 `cost` 是会话视图 | Public command browsing showed `status` and `cost` are not incidental, but part of the session UX. / `status` 与 `cost` 不是附属功能，而是会话视图的一部分。 | `/status` shows runtime state; `/cost` shows local transcript-based usage and duration. / 我们已把两者做成独立摘要服务。 | [StatusSummaryService.java](src/main/java/com/example/codingagent/runtime/StatusSummaryService.java), [StatusSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/StatusSlashCommand.java), [SessionCostSummary.java](src/main/java/com/example/codingagent/runtime/SessionCostSummary.java), [CostSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/CostSlashCommand.java) |
| In-session diagnostics / 会话内自诊断 | Public mirror includes `doctor` and version-style runtime introspection commands. / 公共镜像里有 `doctor` 和版本类自检命令。 | We now support both CLI `doctor` and chat `/doctor`, plus chat `/version`. / 本项目现在同时支持 CLI `doctor`、chat `/doctor` 和 `/version`。 | [DoctorSummaryService.java](src/main/java/com/example/codingagent/runtime/DoctorSummaryService.java), [DoctorCommand.java](src/main/java/com/example/codingagent/cli/command/DoctorCommand.java), [DoctorSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/DoctorSlashCommand.java), [VersionSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/VersionSlashCommand.java) |
| Provider/model routing / provider 与模型路由 | Leak-era discussions repeatedly emphasized that Claude Code is not tied to one trivial invocation path. / 泄露后的讨论反复说明 Claude Code 不是单一模型调用壳子。 | Current Java version already supports `mock` and `openai`, with provider/model/base-url overrides. / 当前 Java 版已支持 provider/model/base-url 覆盖。 | [RoutingAgentModelGateway.java](src/main/java/com/example/codingagent/model/RoutingAgentModelGateway.java), [OpenAiAgentModelGateway.java](src/main/java/com/example/codingagent/model/OpenAiAgentModelGateway.java), [ModelSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/ModelSlashCommand.java) |
| Prompt/history compaction / history 压缩 | Public technical write-ups repeatedly mention context handling as a core design problem. / 上下文治理是这波技术深扒里的核心主题之一。 | We already summarize older transcript before prompt injection instead of replaying unlimited raw history. / 我们已经对齐“较早历史先摘要，再注入模型”的策略。 | [OpenAiDecisionPromptBuilder.java](src/main/java/com/example/codingagent/model/OpenAiDecisionPromptBuilder.java), [AgentProperties.java](src/main/java/com/example/codingagent/config/AgentProperties.java) |
| Local-first safety rails / 本地优先也要有边界 | Leak-era discussions made it clear these tools operate with powerful local permissions. / 这类工具天然有本地高权限风险。 | We already enforce workspace path guards and blocked shell patterns. / 我们已实现工作区路径边界和 shell 危险命令拦截。 | [WorkspacePathResolver.java](src/main/java/com/example/codingagent/tool/WorkspacePathResolver.java), [BashExecTool.java](src/main/java/com/example/codingagent/tool/BashExecTool.java) |

## Why This Repo Is Useful / 为什么这个仓库值得读

This repository is designed so that readers can move from a leak-era technical claim to a concrete Java implementation without guessing.

这个仓库的价值，不在于“喊口号说对齐 Claude Code”，而在于你可以把一条泄露后被热议的技术点，直接映射到一份可以运行、可以测试、可以继续改造的 Java 实现上。

That means:

- You can study session runtime design from [runtime](src/main/java/com/example/codingagent/runtime)
- You can study command-surface design from [cli/chat/command](src/main/java/com/example/codingagent/cli/chat/command)
- You can study tool orchestration from [tool](src/main/java/com/example/codingagent/tool)
- You can study transcript and metadata persistence from [persistence](src/main/java/com/example/codingagent/persistence)

## Honest Scope / 诚实边界

The project is intentionally marketed with evidence, not aspiration. We do **not** currently claim full parity with Claude Code.

本项目的宣传原则是“拿已实现事实说话”，不是“拿规划冒充能力”。截至目前，我们**没有**声称已经完整复刻 Claude Code。

Still not aligned yet / 仍未对齐的能力包括：

- Streaming terminal UI and richer TUI rendering / 更完整的流式终端 UI 与 TUI 渲染
- MCP, plugin marketplace, skills loading, daemon, bridge, remote control / MCP、插件市场、skills、daemon、bridge、remote control
- Full structured tool-calling protocol beyond current JSON decision contract / 超越当前 JSON 决策协议的完整结构化工具调用
- Real provider-side token billing, account/auth integration, policy controls / 真实 provider token 计费、账号认证、策略治理
- Multi-provider parity beyond `mock` and `openai` / 超出 `mock` 和 `openai` 之外的多 provider 对齐

## Current Implemented Surface / 当前已落地能力

当前已经落地的能力：

- 默认 root 入口：无子命令时直接进入交互式 chat，更接近 Claude Code CLI 的默认体验
- `doctor` 命令：检查本地运行时配置
- `run` 命令：执行单次 Agent Loop
- `chat` 命令：启动交互式 REPL，会话内持续复用同一 `session-id`
- Slash commands：当前支持 `/help`、`/status`、`/tools`、`/files`、`/cost`、`/doctor`、`/version`、`/exit`、`/quit`、`/clear`、`/resume`、`/rename`、`/model`、`/provider`、`/base-url`
- 模型路由：已支持 `mock`、`openai`
- OpenAI 兼容地址覆盖：支持配置文件、`OPENAI_BASE_URL` 和 `run --base-url`
- 决策协议边界：模型输出由独立 JSON 决策协议负责说明与解码
- 工具闭环：`list_files`、`read_file`、`grep_text`、`bash_exec`、`write_file`
- 精确补丁工具：`patch_file`
- `bash_exec` 安全护栏：支持可配置超时和危险命令模式拦截
- 工作区路径护栏：文件读写与补丁操作禁止越界到 workspace 外
- 会话记录：JSONL 持久化到 `.agent/sessions`
- 会话续跑：同一 `--session-id` 会自动加载历史 transcript 给模型继续决策
- 会话元数据：支持自定义标题，并在 `/resume` 列表中展示
- 上下文文件索引：会话会跟踪已读/已写/已补丁的文件，`/files` 可查看当前上下文文件
- 本地成本统计：`/cost` 基于当前 transcript 输出消息数、持续时间、字符数、工具输出量和上下文文件数
- 状态视图：`/status` 输出应用版本、工作区、模型配置、API key 配置、工具数和上下文文件数
- 会话诊断：`/doctor` 可在 chat 内直接查看当前运行环境诊断
- 运行版本：`/version` 输出当前会话真实运行版本
- Transcript 压缩：较早历史会按角色计数 + 关键片段摘要注入 prompt，避免长会话无限膨胀
- CLI 异常收口：模型配置错误会返回可读错误

快速开始：

```bash
sh ./mvnw test
printf '/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='doctor'
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请读取 README'"
printf '请读取 README\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat'
printf '/status\n/version\n/doctor\n/cost\n/tools\n/files\n/help\n/quit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat-slash'
printf '请读取 README\n/rename\n/resume\n/clear\n/model gpt-4.1-mini\n/rename focused-java-agent\n/provider openai\n/base-url https://openrouter.ai/api\n/status\n/provider default\n/base-url default\n/model\n/resume demo-live-session\n请根据历史继续\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-live-session'
printf '/files\n请读取 README\n/files\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat-files'
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请执行一个 pwd 命令'"
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请帮我创建文件'"
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请帮我打一个补丁'"
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --session-id demo --prompt '请帮我总结当前阶段目标'"
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --session-id demo --prompt '请根据历史继续'"
OPENAI_BASE_URL=https://openrouter.ai/api sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --provider openai --base-url https://openrouter.ai/api --model gpt-4.1-mini --prompt '请分析当前工作区'"
```

如果要切换真实 OpenAI 兼容模型：

```bash
export OPENAI_API_KEY=your_key
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --provider openai --model gpt-4.1-mini --prompt '请分析当前工作区'"
```

当前默认配置位于：

- [application.yml](/Users/wangzq/VsCodeProjects/claude-code/src/main/resources/application.yml)

其中 transcript 相关默认值为：

- `agent.runtime.transcript.recent-entries=8`
- `agent.runtime.transcript.summary-entries=4`
- `agent.runtime.transcript.max-entry-chars=160`
- `agent.model.prompt.system-lines` 可覆写 OpenAI 决策器的 system prompt 文案
- 新写入的 transcript 记录会带 `timestamp`，旧 JSONL 无该字段时仍可兼容读取

更多背景、目标、阶段进度和接手说明见：

- [docs/architecture.md](/Users/wangzq/VsCodeProjects/claude-code/docs/architecture.md)
- [docs/progress.md](/Users/wangzq/VsCodeProjects/claude-code/docs/progress.md)
