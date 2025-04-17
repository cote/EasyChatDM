package io.cote.EasyChatDM;


import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.modelcontextprotocol.spec.McpSchema.Role.ASSISTANT;

@Service
public class ChatDMDirResource {

    private static final Logger logger = LoggerFactory.getLogger(ChatDMDir.class);

    private ChatDMDir chatDMDir;

    public ChatDMDirResource(ChatDMDir chatDMDir) {
        this.chatDMDir = chatDMDir;
    }

    @Bean
   public List<McpServerFeatures.SyncResourceSpecification> readFile()
   {
       // I don't believe the MCP JDK allows you to do wildcard matching,
       // the URL must be exacty. So, we have to register each file we
       // want people to be able to access, probably a good idea.

       // TK - list all the files in adventure - make it so they're not read until called/read each time called.

       return List.of();
   }
    /**/

    /**
     * Resource Templates are not supported by Spring AI MCP (as best I can tell on April 16th, 2025).

    @Bean
    public List<McpSchema.ResourceTemplate> chatDMDirResourceTemplates() {

        // uri Must be RFC 6570 template
        McpSchema.ResourceTemplate adventureTemplate = new McpSchema.ResourceTemplate(
                "file:///chatdm/adventure/{adventure_filename}",
                "A file of or related to the current adventure.",
                "The name of the adventure file you'd like to retrieve.",
                "text/markdown",
                new McpSchema.Annotations(List.of(ASSISTANT), Double.valueOf(1.0))
        );

        return List.of(adventureTemplate);
    }

     */
}
