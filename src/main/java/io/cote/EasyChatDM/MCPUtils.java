package io.cote.EasyChatDM;

import io.modelcontextprotocol.spec.McpSchema;

public class MCPUtils {

    static final String CONTEXT_DESCRIPTION = "Any interesting context for why you are doing this right now.";

    static McpSchema.LoggingMessageNotification logInfo(String message) {
        return new McpSchema.LoggingMessageNotification(McpSchema.LoggingLevel.INFO, "easychatdm", message);
    }

    static McpSchema.LoggingMessageNotification logError(String message) {
        return new McpSchema.LoggingMessageNotification(McpSchema.LoggingLevel.ERROR, "easychatdm", message);
    }
}
