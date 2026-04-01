package com.example.codingagent.tool;

import com.example.codingagent.runtime.AgentRuntimeContext;
import java.nio.file.Path;

/**
 * 负责把相对路径解析到工作区内，并阻止越界访问。
 */
public final class WorkspacePathResolver {

    private WorkspacePathResolver() {
    }

    /**
     * 解析相对工作区路径。
     *
     * @param context 运行上下文
     * @param rawPath 原始相对路径
     * @return 工作区内的绝对路径
     */
    public static Path resolve(AgentRuntimeContext context, String rawPath) {
        Path workspaceRoot = context.workspaceRoot().toAbsolutePath().normalize();
        Path target = workspaceRoot.resolve(rawPath).normalize();
        if (!target.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("路径越界，禁止访问工作区外文件: " + rawPath);
        }
        return target;
    }
}
