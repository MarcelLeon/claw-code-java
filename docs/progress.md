# 项目进度文档

## 1. 项目目标

构建一个 Java-first、Spring AI 驱动、可持续演进的 Coding Agent CLI。

核心要求：

- 尽量复刻 Claude Code 一类 Coding Agent 的核心运行模式
- 优先个人本地可用，先完成闭环，再逐步补安全和企业治理
- 支持指定底层模型，并为后续扩展更多模型提供统一接口
- 工程结构清晰，方便后续其他大模型继续开发和魔改

## 2. 当前约束

- JDK 17 可用
- 本机暂未安装 Maven/Gradle，因此工程使用 Maven Wrapper
- 当前以本地优先为主，安全策略和企业治理后置
- 已接入 Spring AI OpenAI 依赖，但默认 provider 仍使用 `mock`

## 3. 分阶段计划

### 阶段一：最小可用闭环

目标：

- 能通过 CLI 接收任务
- 能进入基础 Agent Loop
- 能执行少量本地工具
- 能使用 Mock 模型完成闭环验证
- 为接入真实 Spring AI 模型预留清晰接口

预计能力：

- `run` 命令
- `doctor` 命令
- Mock 模型网关
- 基础工具：文件读取、目录列举、文本搜索、Shell 执行
- JSONL 会话落盘的最小实现

### 阶段二：真实模型接入与稳定性增强

目标：

- 接入 Spring AI 模型
- 支持指定模型名
- 加强输出结构化与错误恢复
- 完善会话恢复与日志

### 阶段三：命令体系、技能和扩展机制

目标：

- Slash command
- Skill/Prompt 包装
- 插件/MCP 预留
- 更完整的会话上下文管理

### 阶段四：安全与企业治理

目标：

- Permission Mode
- Workspace trust
- 命令策略与高风险操作拦截
- 多环境运行策略

## 4. 当前进展

### 已完成

- 创建项目目录
- 选定技术栈：Spring Boot + Spring AI + Picocli
- 创建 `pom.xml`
- 创建 Maven Wrapper 配置
- 创建 README 和本进度文档
- 创建架构文档，明确“行为等价复刻”而非文件级翻译
- 建立 CLI 启动骨架：`RootCommand`、`doctor`、`run`
- 调整 root 默认行为：无子命令时直接进入 chat，会话体验更贴近 Claude Code CLI
- 增加 `chat` 交互式 REPL 入口，并复用同一 runtime 主链路
- 抽象 chat slash command 模型，支持 `/help`、`/status`、`/tools`、`/exit`
- 增强 chat 会话控制，支持 `/clear`、`/resume`、`/model`
- 补齐 chat 会话运行时覆盖控制，支持在 REPL 内查看/切换 `/provider`、`/base-url`
- 增加 `/rename`，并抽象独立会话元数据存储，支持显示自定义标题
- 增加 `/files`，并抽象会话级上下文文件索引，跟踪进入上下文的文件
- 增加 `/cost`，基于本地 transcript 输出会话消息量、字符量和上下文文件统计
- 抽象 `ChatSessionState`，把交互式会话状态从 REPL 逻辑中分离
- 建立最小 Agent Loop：`AgentRunnerFacade` -> `CodingAgentEngine`
- 建立模型路由：`mock`、`openai`
- 建立基础工具：`list_files`、`read_file`、`grep_text`、`bash_exec`、`write_file`
- 增加 `patch_file` 局部补丁工具，支持按精确片段替换文件内容
- 建立 JSONL Transcript 持久化
- 抽象 `ConversationState` 作为运行期动态会话状态边界
- 建立基于 transcript 的会话续跑能力
- 建立 CLI 异常统一渲染
- 建立多轮闭环：模型可在拿到工具结果后收敛为最终答案
- OpenAI 兼容 base URL 已支持配置文件、环境变量和单次运行覆盖
- 增加模型决策结构校验，拦截无效 `finalAnswer/toolCall` 组合
- 抽象独立的模型决策协议边界，统一协议说明与 JSON 解码逻辑
- 为 `bash_exec` 增加可配置超时和危险命令模式拦截
- 为文件读写类工具增加工作区边界校验，阻止 `../` 越界访问
- 增加 transcript 压缩窗口，向模型注入“较早历史摘要 + 最近记录”而不是无限增长的原始历史
- OpenAI system prompt 已支持通过配置文件组装，默认文案与规则可覆盖

### 当前阶段状态

- 阶段一：已完成并通过测试
- 阶段二：已完成“真实模型接入骨架”、`openai` provider 路由、base URL 覆盖链路、基础会话续跑、transcript 压缩窗口、可配置 system prompt 组装，以及工作区内安全文件修改能力；chat 会话内的 provider / model / base URL 覆盖也已打通，正在继续增强工具协议和恢复能力
- 当前增量重点：继续补足 Claude Code 风格的会话命令面，最新已补 `/rename`、`/files`、`/cost` 与会话级上下文统计

## 5. 当前代码地图

建议按下面顺序阅读：

1. 启动入口
   - [CodingAgentApplication.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/CodingAgentApplication.java)
   - [CodingAgentCliCommand.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/cli/CodingAgentCliCommand.java)
   - [architecture.md](/Users/wangzq/VsCodeProjects/claude-code/docs/architecture.md)
2. CLI 命令
   - [RootCommand.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/cli/RootCommand.java)
   - [DoctorCommand.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/cli/command/DoctorCommand.java)
   - [ChatCommand.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/cli/command/ChatCommand.java)
   - [RunCommand.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/cli/command/RunCommand.java)
   - [ChatSessionRunner.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/cli/chat/ChatSessionRunner.java)
   - `cli/chat/command/*`
3. 运行时主链路
   - [AgentRunnerFacade.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/runtime/AgentRunnerFacade.java)
   - [CodingAgentEngine.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/runtime/CodingAgentEngine.java)
   - [AgentRuntimeFactory.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/runtime/AgentRuntimeFactory.java)
4. 模型层
   - [RoutingAgentModelGateway.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/model/RoutingAgentModelGateway.java)
   - [MockAgentModelGateway.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/model/MockAgentModelGateway.java)
   - [OpenAiAgentModelGateway.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/model/OpenAiAgentModelGateway.java)
   - [OpenAiDecisionPromptBuilder.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/model/OpenAiDecisionPromptBuilder.java)
   - `model/protocol/*`
5. 工具层
   - [WorkspaceTool.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/tool/WorkspaceTool.java)
   - [ToolExecutor.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/tool/ToolExecutor.java)
   - [BashExecTool.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/tool/BashExecTool.java)
6. 持久化与测试
   - [TranscriptStore.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/persistence/TranscriptStore.java)
   - [SessionMetadataStore.java](/Users/wangzq/VsCodeProjects/claude-code/src/main/java/com/example/codingagent/persistence/SessionMetadataStore.java)
   - [CodingAgentEngineTest.java](/Users/wangzq/VsCodeProjects/claude-code/src/test/java/com/example/codingagent/runtime/CodingAgentEngineTest.java)

## 6. 已验证结果

验证日期：2026-04-01

已通过：

- `sh ./mvnw test`
- `sh ./mvnw -q -DskipTests package`
- `doctor` CLI 实跑
- 无子命令直接进入 chat 的 root CLI 实跑
- `chat --provider mock --session-id demo-chat` CLI 实跑
- `chat --provider mock --session-id demo-chat-slash` 下的 `/status`、`/tools`、`/help`、`/exit` CLI 实跑
- `chat --provider mock --session-id demo-live-session` 下的 `/resume`、`/clear`、`/model` CLI 实跑
- `chat --provider mock --session-id smoke-rename-*` 下的 `/rename`、标题生成与 `/resume` 标题展示 CLI 实跑
- `chat --provider mock --session-id smoke-files-*` 下的 `/files` 前后状态与上下文文件登记 CLI 实跑
- `chat --provider mock --session-id smoke-cost-*` 下的 `/cost` 会话统计 CLI 实跑
- `chat` 会话内 `/provider`、`/base-url` 的查看、切换与恢复默认值测试
- `java -jar target/coding-agent-cli-0.1.0-SNAPSHOT.jar doctor` 实跑
- `run --prompt '请读取 README'` CLI 实跑
- `run --prompt '请执行一个 pwd 命令'` CLI 实跑
- `run --prompt '请帮我创建文件'` CLI 实跑
- 同一 `--session-id` 的二次运行可读取历史 transcript 并续跑（测试已覆盖）
- OpenAI base URL 优先级测试：`run --base-url` > `OPENAI_BASE_URL` > `agent.model.base-url` > 默认值
- 模型决策结构校验测试：拒绝空 summary、冲突动作、缺失工具参数
- JSON 决策协议测试：支持纯 JSON、包裹 JSON，并拒绝空输出与非 JSON 输出
- Shell 工具安全测试：危险命令拦截、超时控制和安全命令执行
- Prompt transcript 压缩测试：较早历史摘要与最近记录窗口注入
- OpenAI prompt 组装测试：支持读取 `agent.model.prompt.system-lines` 自定义 system prompt

关键结论：

- 本地 `mock` provider 已完成 CLI -> Agent Loop -> Tool -> Final Answer 的闭环
- `chat` 已具备最小持续会话体验，并在同一 session 内复用历史 transcript
- `chat` 的会话控制命令已被抽象为独立模块，而不是散落在 REPL 循环中的条件分支
- root 默认入口已不再只是帮助页，而是直接落到默认 chat，会话式体验更贴近 Claude Code
- `chat` 已支持会话级控制：新建会话、切换到已有会话、查看/切换当前 provider、模型和 base URL
- `chat` 已支持会话标题：可通过 `/rename` 显式设置，也可基于历史首条用户消息生成 kebab-case 标题
- `chat` 已支持会话上下文文件索引：`read_file`、`write_file`、`patch_file` 命中的文件会登记到会话元数据，`/files` 可列出当前上下文文件
- `chat` 已支持基础 `/cost` 视图：当前先基于本地 transcript 统计消息数、字符数和工具输出量，尚未接入真实 provider token/billing
- 工具执行结果已能反馈回下一轮模型决策
- 读、搜、执行命令、写文件四类基础动作均已闭环验证
- `bash_exec` 已具备基础安全护栏，可拦截典型危险命令并限制执行时长
- `read_file`、`list_files`、`write_file`、`patch_file` 已限制在 workspace 内，避免简单路径穿越
- 会话记录已正确写入 `.agent/sessions/*.jsonl`
- 同一 session 的历史消息已可重新注入模型上下文，具备最小续跑能力
- 长会话 prompt 不再简单塞入固定条数原文，较早 transcript 会先压缩为角色统计和关键片段摘要
- OpenAI 决策 prompt 不再只能依赖代码内硬编码文案，已可通过配置覆写 identity / JSON 规则 / 行为约束
- JSON 决策协议已从 provider 网关中抽离，prompt 约束与响应解码共用同一协议边界
- `openai` provider 已打通真实 API 调用路径；当前本机环境返回的是配额不足错误，而不是接线错误
- OpenAI 兼容服务的切换不再依赖改配置文件，适合本地临时接 OpenRouter、代理或自建网关
- 运行时现在会在执行前校验模型决策结构，可更早暴露提示词或模型输出问题
- Agent 已具备“整文件写入 + 精确片段补丁”两种基础代码修改手段，适合继续往更强的结构化工具协议演进
- 当前实现已明确采用“领域模型 + 模块边界 + 行为等价”策略，而不是按上游 TypeScript 文件逐个翻译

## 7. 已知问题与下一步

优先级最高：

- 把当前“JSON 约定式工具调用”升级为更稳的结构化协议
- 为真实模型增加流式输出

第二优先级：

- 把当前基于正则的 `bash_exec` 安全策略扩展为更明确的权限模式、白名单和审计
- 增加阶段性架构文档，方便继续魔改
- 继续补足 Claude Code 风格的 session / status / cost / files 命令面
- 把上下文文件索引继续从“已触达文件”提升为“更接近真实上下文缓存”的表示

## 8. 接手说明

后续无论是人类开发者还是其他大模型，接手时优先阅读：

1. 本文档，了解目标与当前阶段
2. `README.md`，了解项目定位
3. `src/main/java` 下的 `bootstrap`、`cli`、`runtime`、`model`、`tool` 包
4. `src/test/java`，先看测试覆盖了哪些闭环

接手原则：

- 单类尽量不超过 500 行
- 类和方法加精简中文描述
- 保持包职责清晰
- 做完一个大阶段，先跑测试，再更新本文档
