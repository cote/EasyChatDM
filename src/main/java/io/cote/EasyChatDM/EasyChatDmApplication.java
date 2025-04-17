package io.cote.EasyChatDM;


import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.ToolCallback;
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

    /**
     * Loads the {@link OracleTools} tool.
     *
     * @param tool {@link OracleTools} tool to register
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
