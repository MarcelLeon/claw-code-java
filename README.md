# Coding Agent CLI

Java-first 的 Coding Agent CLI，目标是以 Spring AI 为模型接入层，逐步复刻 Claude Code 一类企业级编码 agent 的关键能力，并保持结构清晰、可学习、可持续迭代。

Java-first Coding Agent CLI built on Spring AI, aiming to recreate the core runtime patterns of Claude Code in a clean-room, behavior-oriented way.

## Claude Code Leak Survey / Claude Code 泄露技术调研汇总

2026 年 3 月 31 日到 4 月 2 日，Claude Code 源码泄露事件迅速从开发者社区蔓延到科技媒体和自媒体。我们没有把这波热度直接写成“本项目已经复刻”，而是把传播最广、技术点最密集的公开材料做了归纳，再只对外展示**已经真实对齐**的部分。

Hot articles and public mirrors / 热门文章与公开镜像：

- Mirror / 镜像：
  - [MarcelLeon/claude-code](https://github.com/MarcelLeon/claude-code)
- News / 新闻：
  - [Axios: Anthropic leaked 500,000 lines of its own source code](https://www.axios.com/2026/03/31/anthropic-leaked-source-code-ai)
  - [Sina / 每日经济新闻：51 万行 Claude Code 源代码泄露](https://finance.sina.com.cn/roll/2026-04-01/doc-inhsywsw1723132.shtml)
- Deep-dive / 技术深扒：
  - [Claude Lab: Inside KAIROS, Daemon Mode, and Unreleased Models](https://claudelab.net/en/articles/claude-code/claude-code-sourcemap-kairos-internal-architecture)

这些文章和镜像反复提到、且对工程实现最有价值的技术点包括：

1. Claude Code is a tool-first coding agent, not a plain chat shell.
   Claude Code 更像“工具驱动的编程代理”，而不是普通聊天壳。
2. Its runtime is session-centric: transcript, resume, status, cost, commands, and file context are all first-class concepts.
   它的运行时是“会话中心”的：transcript、resume、status、cost、commands、file context 都是一级概念。
3. The CLI is REPL-first by default, then layered with slash commands and richer runtime services.
   它的 CLI 默认就是 REPL，会在此基础上叠加 slash commands 和更完整的 runtime 服务。
4. The codebase separates bootstrap, runtime loop, tools, API/provider routing, and persistence.
   它在工程上明确拆分 bootstrap、runtime loop、tools、provider routing 和 persistence。
5. Public leak analyses repeatedly highlight a large tool surface, a central query/runtime engine, and long-running agent ambitions such as daemon mode, persistent threads, remote control, and memory transfer.
   这波泄露分析反复提到的关键词还包括：庞大的工具面、中心化的查询/运行时引擎，以及 daemon、persistent threads、remote control、跨会话记忆迁移等“长时自治”能力。

Concrete leak-era observations / 这波泄露中被反复讨论的具体技术点：

| Leak-era observation / 泄露后被热议的技术点 | Where it appeared / 公开来源 | What it means for builders / 对复刻者的启示 |
| --- | --- | --- |
| Roughly 1,900 files and 500k+ lines were exposed via source maps. / 通过 source map 暴露出约 1900 个文件、50 万行以上代码。 | Axios, 新浪/爱范儿 | Claude Code is not “just prompts”; it is a serious production runtime with many subsystems. / Claude Code 不是“提示词壳子”，而是完整产品级 runtime。 |
| The leaked architecture surfaced a big tool layer plus a central `QueryEngine`. / 泄露架构里有很大的工具层和中心化 `QueryEngine`。 | 新浪/爱范儿、镜像源码 | The core moat is orchestration quality: tool loop, context handling, retries, and session control. / 核心护城河在编排质量，不只是模型本身。 |
| KAIROS feature flags suggested daemon mode, persistent threads, async sessions, and background queues. / KAIROS 特性标志暗示 daemon、持久线程、异步会话和后台任务队列。 | Claude Lab | Long-running agent architecture matters if you want enterprise-grade autonomy. / 如果目标是企业级自治 agent，就必须预留长生命周期架构。 |
| Public reports said the leak showed persistent assistant behavior and cross-session learning/review. / 公开报道提到泄露代码里有 persistent assistant 和跨会话复盘/迁移学习。 | Axios | Session memory is a first-class runtime concern, not an add-on. / 会话记忆是运行时核心，不是外挂。 |
| Remote control from phone/browser and multi-agent collaboration appeared repeatedly in analyses. / 手机/浏览器远程控制与多 agent 协作是反复出现的话题。 | Axios, Claude Lab, mirror code browsing | The future direction is “always-on coding runtime”, not a one-shot CLI wrapper. / 方向是“常驻型 coding runtime”，不是一次性命令包装器。 |

## Reality-Based Parity Map / 基于事实的对齐说明

下面不是“我们想做什么”，而是“截至现在已经真实落地并可用什么”。每一条都给出本仓库代码引用，方便你直接核验。

| Public Claude Code clue / 公开线索 | Our actual alignment / 本项目已实际对齐 | Code refs / 代码引用 |
| --- | --- | --- |
| Default REPL-first CLI startup / 默认进入 REPL 会话 | No subcommand now drops directly into chat, instead of just printing help. / 现在无子命令会直接进入 chat，而不是只打印帮助。 | [RootCommand.java](src/main/java/com/example/codingagent/cli/RootCommand.java), [ChatSessionRunner.java](src/main/java/com/example/codingagent/cli/chat/ChatSessionRunner.java) |
| Slash-command driven session UX / slash command 驱动的会话体验 | We already support `/help`, `/status`, `/tools`, `/files`, `/cost`, `/clear`, `/resume`, `/rename`, `/model`, `/provider`, `/base-url`, `/exit`. / 当前已经支持一组核心 slash commands。 | [cli/chat/command](src/main/java/com/example/codingagent/cli/chat/command), [ChatSlashCommandDispatcher.java](src/main/java/com/example/codingagent/cli/chat/command/ChatSlashCommandDispatcher.java) |
| Tool-first coding runtime / 工具优先的 coding runtime | The agent loop can choose tools, feed results back, and converge to a final answer. / Agent loop 可以调用工具、回填结果并收敛到最终回答。 | [CodingAgentEngine.java](src/main/java/com/example/codingagent/runtime/CodingAgentEngine.java), [ToolExecutor.java](src/main/java/com/example/codingagent/tool/ToolExecutor.java) |
| Local coding tools: shell, read, write, patch, grep / 本地编程工具集 | Implemented `bash_exec`, `read_file`, `write_file`, `patch_file`, `grep_text`, `list_files`. / 已实现 shell、读写、补丁、搜索、列目录工具。 | [tool](src/main/java/com/example/codingagent/tool) |
| Session transcript and resume / 会话 transcript 与恢复 | Same `session-id` automatically reloads transcript for continued runs and chat resume. / 同一 `session-id` 会自动复用 transcript 并续跑。 | [TranscriptStore.java](src/main/java/com/example/codingagent/persistence/TranscriptStore.java), [SessionService.java](src/main/java/com/example/codingagent/runtime/SessionService.java), [ResumeSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/ResumeSlashCommand.java) |
| Session metadata beyond raw chat / 除聊天记录外的会话元数据 | We persist custom titles and contextual file lists, then surface them in `/resume`, `/status`, and `/files`. / 我们已经持久化会话标题和上下文文件列表。 | [SessionMetadataStore.java](src/main/java/com/example/codingagent/persistence/SessionMetadataStore.java), [RenameSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/RenameSlashCommand.java), [FilesSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/FilesSlashCommand.java) |
| Status and cost are first-class session views / `status` 与 `cost` 是一级会话视图 | `/status` shows app/runtime state; `/cost` shows transcript-based local usage stats and duration. / `/status` 和 `/cost` 已经成为独立会话视图。 | [StatusSummaryService.java](src/main/java/com/example/codingagent/runtime/StatusSummaryService.java), [StatusSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/StatusSlashCommand.java), [SessionCostSummary.java](src/main/java/com/example/codingagent/runtime/SessionCostSummary.java), [CostSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/CostSlashCommand.java) |
| Provider/model routing layer / provider 与模型路由层 | Current Java version already routes between `mock` and `openai`, with provider/model/base-url overrides. / 当前已支持 `mock` 与 `openai` 路由，以及 provider/model/base-url 覆盖。 | [RoutingAgentModelGateway.java](src/main/java/com/example/codingagent/model/RoutingAgentModelGateway.java), [OpenAiAgentModelGateway.java](src/main/java/com/example/codingagent/model/OpenAiAgentModelGateway.java), [ModelSlashCommand.java](src/main/java/com/example/codingagent/cli/chat/command/ModelSlashCommand.java) |
| Prompt/history compaction matters / history 压缩非常重要 | Older transcript is summarized before prompt injection, instead of replaying unlimited raw history. / 较早历史会先压缩摘要后再注入模型。 | [OpenAiDecisionPromptBuilder.java](src/main/java/com/example/codingagent/model/OpenAiDecisionPromptBuilder.java), [AgentProperties.java](src/main/java/com/example/codingagent/config/AgentProperties.java) |
| Safety boundaries exist even for local-first workflows / 即使本地优先也要有边界 | Workspace path guards and blocked shell patterns are already in place. / 已实现工作区路径越界防护和 shell 危险命令拦截。 | [WorkspacePathResolver.java](src/main/java/com/example/codingagent/tool/WorkspacePathResolver.java), [BashExecTool.java](src/main/java/com/example/codingagent/tool/BashExecTool.java) |

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
- Slash commands：当前支持 `/help`、`/status`、`/tools`、`/files`、`/cost`、`/exit`、`/clear`、`/resume`、`/rename`、`/model`、`/provider`、`/base-url`
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
- Transcript 压缩：较早历史会按角色计数 + 关键片段摘要注入 prompt，避免长会话无限膨胀
- CLI 异常收口：模型配置错误会返回可读错误

快速开始：

```bash
sh ./mvnw test
printf '/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='doctor'
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请读取 README'"
printf '请读取 README\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat'
printf '/status\n/cost\n/tools\n/files\n/help\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat-slash'
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
