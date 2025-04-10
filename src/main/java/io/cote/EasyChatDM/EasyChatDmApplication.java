package io.cote.EasyChatDM;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class EasyChatDmApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyChatDmApplication.class, args);
    }

    /**
     * Loads the {@link Oracle} tool.
     *
     * @param the {@link Oracle} tool to register
     * @return List of {@link ToolCallback}s to register as MCP tools.
     */
    @Bean
    public List<ToolCallback> oracles(Oracle tool) {
        return List.of(ToolCallbacks.from(tool));
    }


}
