package com.example.codingagent.cli;

import java.io.PrintWriter;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * 统一渲染 CLI 执行异常。
 */
@Component
public class CliExecutionExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    @Override
    public int handleExecutionException(
            Exception ex,
            CommandLine commandLine,
            CommandLine.ParseResult parseResult
    ) {
        PrintWriter writer = commandLine.getErr();
        writer.println("执行失败: " + ex.getMessage());
        writer.println("可先运行 `doctor` 检查配置，或切换到 `--provider mock` 做本地闭环验证。");
        writer.flush();
        return commandLine.getCommandSpec().exitCodeOnExecutionException();
    }
}
