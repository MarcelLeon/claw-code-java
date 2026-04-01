# Coding Agent CLI

Java-first 的 Coding Agent CLI，目标是以 Spring AI 为模型接入层，逐步复刻 Claude Code 一类企业级编码 agent 的关键能力，并保持结构清晰、可学习、可持续迭代。

当前已经落地的能力：

- `doctor` 命令：检查本地运行时配置
- `run` 命令：执行单次 Agent Loop
- `chat` 命令：启动交互式 REPL，会话内持续复用同一 `session-id`
- Slash commands：当前支持 `/help`、`/status`、`/tools`、`/exit`、`/clear`、`/resume`、`/model`
- 模型路由：已支持 `mock`、`openai`
- OpenAI 兼容地址覆盖：支持配置文件、`OPENAI_BASE_URL` 和 `run --base-url`
- 决策协议边界：模型输出由独立 JSON 决策协议负责说明与解码
- 工具闭环：`list_files`、`read_file`、`grep_text`、`bash_exec`、`write_file`
- 精确补丁工具：`patch_file`
- `bash_exec` 安全护栏：支持可配置超时和危险命令模式拦截
- 工作区路径护栏：文件读写与补丁操作禁止越界到 workspace 外
- 会话记录：JSONL 持久化到 `.agent/sessions`
- 会话续跑：同一 `--session-id` 会自动加载历史 transcript 给模型继续决策
- Transcript 压缩：较早历史会按角色计数 + 关键片段摘要注入 prompt，避免长会话无限膨胀
- CLI 异常收口：模型配置错误会返回可读错误

快速开始：

```bash
sh ./mvnw test
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='doctor'
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请读取 README'"
printf '请读取 README\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat'
printf '/status\n/tools\n/help\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat-slash'
printf '请读取 README\n/resume\n/clear\n/model gpt-4.1-mini\n/model\n/resume demo-live-session\n请根据历史继续\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-live-session'
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

更多背景、目标、阶段进度和接手说明见：

- [docs/architecture.md](/Users/wangzq/VsCodeProjects/claude-code/docs/architecture.md)
- [docs/progress.md](/Users/wangzq/VsCodeProjects/claude-code/docs/progress.md)
