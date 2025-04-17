package io.cote.EasyChatDM;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCPToolUtils {

    // Here, I was trying to create my own tool maker so that I could load the tool instructions from
    // a file. But things got so wild after a lot of work figuring out undocumented code that I gave
    // up so that I could get to making a video. I think I was close, though. I just needed
    // to define the last part. This would need a builder to make it sane. Specfiying the
    // JSON Schema is especially horrific and a lot of work. --Coté, April 17th, 2025.


//    public McpServerFeatures.SyncToolSpecification makeMCPTool(String name, String description) {
//
//        // make a JsonSchema for each paramter, or for a group of paramters?
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("entry", Map.of("type", "string", "description", "The DM journal entry."))
//        properties.put("context",
//                Map.of("type", "string",
//                        "description", "Any interesting context for why you are writing this entry right now"));
//        List<String> required = List.of("context");
//        McpSchema.JsonSchema toolSchema = new McpSchema.JsonSchema("object", properties, required, null);
//
//        McpSchema.Tool tool = new McpSchema.Tool(name, description, toolSchema);
//
//    }
}
