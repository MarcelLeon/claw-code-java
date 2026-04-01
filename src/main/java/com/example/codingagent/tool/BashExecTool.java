package com.example.codingagent.tool;

import com.example.codingagent.config.AgentProperties;
import com.example.codingagent.runtime.AgentRuntimeContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * 在工作区内执行本地 Shell 命令。
 */
@Component
public class BashExecTool implements WorkspaceTool {

    private final AgentProperties agentProperties;

    public BashExecTool(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    @Override
    public String name() {
        return "bash_exec";
    }

    @Override
    public String description() {
        return "在工作区目录中执行单条 shell 命令，并返回标准输出与错误输出。参数示例：pwd";
    }

    @Override
    public ToolExecutionResult execute(AgentRuntimeContext context, String argument) {
        Optional<String> matchedPattern = findBlockedPattern(argument);
        if (matchedPattern.isPresent()) {
            return new ToolExecutionResult(
                    name(),
                    "命令被安全策略拦截",
                    "命中禁止模式: " + matchedPattern.get()
            );
        }

        long timeoutSeconds = agentProperties.getTool().getBashExec().getTimeoutSeconds();
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/zsh", "-lc", argument);
        processBuilder.directory(context.workspaceRoot().toFile());
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new ToolExecutionResult(name(), "命令执行超时", "命令超过 " + timeoutSeconds + " 秒仍未结束");
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return new ToolExecutionResult(
                    name(),
                    "命令执行完成，退出码 " + process.exitValue(),
                    output.isBlank() ? "(命令无输出)" : output.trim()
            );
        } catch (IOException ex) {
            return new ToolExecutionResult(name(), "命令执行失败", ex.getMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return new ToolExecutionResult(name(), "命令执行被中断", ex.getMessage());
        }
    }

    private Optional<String> findBlockedPattern(String argument) {
        return agentProperties.getTool().getBashExec().getBlockedCommandPatterns().stream()
                .filter(pattern -> Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(argument).find())
                .findFirst();
    }
}
