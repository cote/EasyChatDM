package io.cote.EasyChatDM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class EasyChatDmApplication {

    Logger logger = LoggerFactory.getLogger(EasyChatDmApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EasyChatDmApplication.class, args);
    }

    /**
     * Loads the {@link OracleTools} tool.
     *
     * @param {@link OracleTools} tool to register
     * @return List of {@link ToolCallback}s to register as MCP tools.
     */
    @Bean
    public List<ToolCallback> oracles(OracleTools tool) {
        return List.of(ToolCallbacks.from(tool));
    }

    @Bean
    public List<ToolCallback> journals(JournalTool tool) { return List.of(ToolCallbacks.from(tool)); }

}
