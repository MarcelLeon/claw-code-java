package com.example.codingagent.cli.command;

import com.example.codingagent.runtime.AgentRunnerFacade;
import com.example.codingagent.runtime.RunRequest;
import com.example.codingagent.runtime.RunResult;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * 执行一次 agent 任务。
 */
@Component
@Command(name = "run", description = "执行一次编码 Agent 任务")
public class RunCommand implements Runnable {

    @Option(names = {"-p", "--prompt"}, required = true, description = "用户任务")
    private String prompt;

    @Option(names = "--model", description = "覆盖模型名称")
    private String model;

    @Option(names = "--provider", description = "覆盖模型提供方")
    private String provider;

    @Option(names = "--base-url", description = "覆盖 OpenAI 兼容接口基础地址")
    private String baseUrl;

    @Option(names = "--session-id", description = "指定会话 ID")
    private String sessionId;

    private final AgentRunnerFacade agentRunnerFacade;

    public RunCommand(AgentRunnerFacade agentRunnerFacade) {
        this.agentRunnerFacade = agentRunnerFacade;
    }

    @Override
    public void run() {
        RunRequest request = new RunRequest(prompt, provider, model, baseUrl, sessionId);
        RunResult result = agentRunnerFacade.run(request);
        System.out.println(result.render());
    }
}
