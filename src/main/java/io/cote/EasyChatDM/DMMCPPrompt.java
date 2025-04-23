package io.cote.EasyChatDM;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncPromptSpecification;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.Prompt;
import io.modelcontextprotocol.spec.McpSchema.PromptArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

@Service
public class DMMCPPrompt {

    private static final Logger logger = LoggerFactory.getLogger(DMMCPPrompt.class);
    private final ChatDMDir chatDMDir;
    private static final Path STARTUP_PROMPT_FILE_PATH = Path.of("prompts/dm_startup.st");

    private static final String defaultPrompt = """
                                         You are a Dungeons and Dragons DM, folling D&D 5th edition rules.
                                         The player has complete control over their character's actions.
                                         Be inventive and fun, follow the rules, and follow the rule of cool.
                                         Limit responses to three or less sentences unless directed otherwise.
                                         You likely have access to several MCP tools, resources, and prompts.
                                         Asses what you have and use them wisely, not excessivly 
                                         """;

    public DMMCPPrompt(ChatDMDir chatDMDir) {
        this.chatDMDir = chatDMDir;
    }


    @Bean
    List<McpServerFeatures.SyncPromptSpecification> startupPrompt() {
        // @formatter:off
        // This is the prompt that will be shown to the MCP Client (LLM),
        // listing the name, purpose, and two arguments
        // for passing in context and extra information to add to the
        // the text given back to the MCP Client
        var prompt = new Prompt(
                "DM_Startup_Prompt",
           """
                     This prompt gives instructions to the Chat DM AI for how to 
                     become, behave, and play as a Dungeons and Dragons DM. These instructions
                     should be followed carefully and followed. 
                     """,
           List.of(
                   new PromptArgument("context",
                                                MCPUtils.CONTEXT_DESCRIPTION,
                                        false),
                   new PromptArgument(
                           "ExtraInfo",
                           """
                                     Extra instructions given by the player about 
                                     how they'd like to play, ongoing information,
                                     or anything they'd like the ChatDM to know.
                                     """, true)
                   ));

        // Build the prompt specificatio, including the return functionality.
        var syncPromptSpecification = new SyncPromptSpecification(
                prompt,
                (exchange, request) ->
                {
                    String promptText =
                            """
                            No instructions given. Be an awesome D&D 5e Dungeon Master. 
                            Ask the player for more guidence.
                            """;

                    // Make sure we have ExtraInfo args
                    // otherwise ST throws up.
                    var fixedArgs = new HashMap<>(
                            request.arguments());
                       fixedArgs.putIfAbsent("ExtraInfo", "");

                    // Load up full prompt from chatdmdir
                    try {
                        promptText = chatDMDir.readSTFile(STARTUP_PROMPT_FILE_PATH, fixedArgs);
                    } catch (IOException e) {
                        logger.error("Using hard-coded Startup prompt. Could not read DM Startup Prompt file {}",
                                     STARTUP_PROMPT_FILE_PATH, e);
                    }

                    GetPromptResult result = new GetPromptResult(
                            "Prompt to start playing D&D",
                            List.of(new McpSchema.PromptMessage(
                                    McpSchema.Role.ASSISTANT,
                                    new McpSchema.TextContent(promptText)))
                    );

                return result;
                });

        // @formatter:on
        return List.of(syncPromptSpecification);
    }

}
