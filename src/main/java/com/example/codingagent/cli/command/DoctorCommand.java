package com.example.codingagent.cli.command;

import com.example.codingagent.runtime.DoctorSummary;
import com.example.codingagent.runtime.DoctorSummaryService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * 诊断当前运行环境。
 */
@Component
@Command(name = "doctor", description = "检查本地运行环境")
public class DoctorCommand implements Runnable {

    private final DoctorSummaryService doctorSummaryService;

    public DoctorCommand(DoctorSummaryService doctorSummaryService) {
        this.doctorSummaryService = doctorSummaryService;
    }

    @Override
    public void run() {
        DoctorSummary summary = doctorSummaryService.summarize();
        System.out.println("Java 版本: " + summary.javaVersion());
        System.out.println("工作目录: " + summary.workspaceRoot());
        System.out.println("会话目录: " + summary.sessionsDir());
        System.out.println("Transcript 最近记录数: " + summary.transcriptRecentEntries());
        System.out.println("Transcript 摘要片段数: " + summary.transcriptSummaryEntries());
        System.out.println("Transcript 单条最大字符数: " + summary.transcriptMaxEntryChars());
        System.out.println("Shell 超时(秒): " + summary.shellTimeoutSeconds());
        System.out.println("Shell 禁止模式数: " + summary.shellBlockedPatternCount());
        System.out.println("默认模型提供方: " + summary.defaultProvider());
        System.out.println("默认模型: " + summary.defaultModel());
        System.out.println("模型基础地址: " + summary.resolvedBaseUrl());
        System.out.println("状态: OK");
    }
}
