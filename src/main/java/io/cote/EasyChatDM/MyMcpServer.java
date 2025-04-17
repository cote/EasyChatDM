package io.cote.EasyChatDM;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.server.autoconfigure.McpServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * An incomplete MCP Server to test out functionality not in Spring AI MCP.
 * This is done as a bean for Spring auto-magic.
 *
 * To use this server
 */
@Service
@ConditionalOnProperty(prefix = "io.cote.EasyChatDM.MyMcpServer", name = "enabled", havingValue = "true")
public class MyMcpServer {

    private static final Logger logger = LoggerFactory.getLogger(MyMcpServer.class);

    @Bean
    McpSyncServer mcpServer(List<McpServerFeatures.SyncResourceSpecification> resourceSpecifications,
                            List<McpSchema.ResourceTemplate> resourceTemplates) {
        StdioServerTransportProvider transport = new StdioServerTransportProvider();

        McpServer.SyncSpecification builder = McpServer.sync(transport);
        McpSchema.Implementation serverInfo = new McpSchema.Implementation("easychatdm-mcp-server", "0.0.1");
        builder.serverInfo(serverInfo);

        McpSchema.ServerCapabilities.Builder capabilitiesBuilder = new McpSchema.ServerCapabilities.Builder();

        // add resources
        if (!resourceSpecifications.isEmpty()) {
            capabilitiesBuilder.resources(false, true);
            builder.resources(resourceSpecifications);
        }

        // add resource templates
        if (!resourceTemplates.isEmpty()) {
            builder.resourceTemplates(resourceTemplates);
        }

        builder.instructions("This is a set of tools, resources, and prompts used to help play Dungeons and Dragons. Look through them and decide when to use them during play.");
        builder.capabilities(capabilitiesBuilder.build());

        // this should start everything running...?
        McpSyncServer server = builder.build();


        return server;
        // add tools


        // add prompts

        // add listeners?
    }

}
