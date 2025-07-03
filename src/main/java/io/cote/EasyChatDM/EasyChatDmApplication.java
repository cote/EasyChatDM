package io.cote.EasyChatDM;

import io.cote.EasyChatDM.oracle.OracleTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EasyChatDmApplication {

    //Logger logger = LoggerFactory.getLogger(EasyChatDmApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EasyChatDmApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider oracle(OracleTools tool) {
        return MethodToolCallbackProvider.builder().toolObjects(tool).build();
    }

    @Bean
    public ToolCallbackProvider journals(JournalTool tool) {
        return MethodToolCallbackProvider.builder().toolObjects(tool).build();
    }

    @Bean
    public ToolCallbackProvider dice(DiceTool tool) {
        return MethodToolCallbackProvider.builder().toolObjects(tool).build();
    }
}
