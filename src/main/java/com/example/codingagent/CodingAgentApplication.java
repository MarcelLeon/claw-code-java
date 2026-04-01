package com.example.codingagent;

import com.example.codingagent.cli.CodingAgentCliCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 应用启动入口。
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ConfigurationPropertiesScan
public class CodingAgentApplication {

    /**
     * 启动 Spring Boot 和 Picocli CLI。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(CodingAgentApplication.class, args)
                .getBean(CodingAgentCliCommand.class)
                .run(args);
    }
}
