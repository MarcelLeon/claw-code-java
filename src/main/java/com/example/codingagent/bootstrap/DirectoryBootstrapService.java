package com.example.codingagent.bootstrap;

import com.example.codingagent.config.AgentProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;

/**
 * 初始化运行目录。
 */
@Service
public class DirectoryBootstrapService {

    private final AgentProperties agentProperties;

    public DirectoryBootstrapService(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    /**
     * 确保运行所需目录存在。
     */
    public void initialize() {
        createDirectory(Path.of(agentProperties.getRuntime().getSessionsDir()));
    }

    private void createDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException ex) {
            throw new IllegalStateException("初始化目录失败: " + path, ex);
        }
    }
}
