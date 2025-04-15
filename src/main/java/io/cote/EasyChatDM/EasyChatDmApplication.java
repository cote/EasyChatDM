package io.cote.EasyChatDM;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;


import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;

@SpringBootApplication
public class EasyChatDmApplication {

    //Logger logger = LoggerFactory.getLogger(EasyChatDmApplication.class);

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
    public ToolCallbackProvider oracle(OracleTools tool) {
        return MethodToolCallbackProvider.builder().toolObjects(tool).build();
    }

    @Bean
    public ToolCallbackProvider journals(JournalTool tool) {
        return MethodToolCallbackProvider.builder().toolObjects(tool).build();
    }

}
