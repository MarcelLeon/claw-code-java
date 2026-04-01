# CLI 示例

这个文件收纳 `README` 之外的进阶命令示例，方便按需复制，不让首页被长命令列表淹没。

## 基础命令

```bash
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='doctor'
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请读取 README'"
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请执行一个 pwd 命令'"
```

## chat 会话示例

```bash
printf '请读取 README\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat'
printf '/status\n/version\n/doctor\n/cost\n/tools\n/files\n/help\n/quit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat-slash'
printf '/files\n请读取 README\n/files\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-chat-files'
```

## 会话恢复与会话级配置示例

```bash
printf '请读取 README\n/rename\n/resume\n/clear\n/model gpt-4.1-mini\n/rename focused-java-agent\n/provider openai\n/base-url https://openrouter.ai/api\n/status\n/provider default\n/base-url default\n/model\n/resume demo-live-session\n请根据历史继续\n/exit\n' | sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments='chat --provider mock --session-id demo-live-session'
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --session-id demo --prompt '请帮我总结当前阶段目标'"
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --session-id demo --prompt '请根据历史继续'"
```

## 文件修改工具示例

```bash
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请帮我创建文件'"
sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --prompt '请帮我打一个补丁'"
```

## OpenAI 兼容服务示例

```bash
OPENAI_BASE_URL=https://openrouter.ai/api sh ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="run --provider openai --base-url https://openrouter.ai/api --model gpt-4.1-mini --prompt '请分析当前工作区'"
```

更多背景说明见：

- [README.md](../README.md)
- [architecture.md](architecture.md)
- [progress.md](progress.md)
