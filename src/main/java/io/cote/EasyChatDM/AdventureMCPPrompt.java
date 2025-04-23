package io.cote.EasyChatDM;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Prompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

@Service
public class AdventureMCPPrompt {

    private static final Logger logger = LoggerFactory.getLogger(AdventureMCPPrompt.class);
    private final ChatDMDir chatDMDir;
    private static final Path ADVENTURE_PROMPT_FILE_PATH = Path.of("prompts/adventure_maker.st");

    public AdventureMCPPrompt(ChatDMDir chatDMDir) {
        this.chatDMDir = chatDMDir;
    }

    /*
    Tell it Oracles to call and what
    to do with the result.

    Also, questions to ask the PC.

    This is a test to see how much orchistration
    I can do.

    adventure_maker.st
    */

    @Bean
    List<McpServerFeatures.SyncPromptSpecification> adventurePrompt() {
        // @formatter:off
        Prompt prompt = new Prompt("adventure_maker", """
                This is a prompt that will give you a template for creating an adventure.
                Follow the instructions in it carefully
                """, List.of(
          new McpSchema.PromptArgument("context",
                                       MCPUtils.CONTEXT_DESCRIPTION,
                                       false),
          new McpSchema.PromptArgument(
            "Length",
            """
                      How long would you like the adventure to be?
                      """, true)
        ));


        // Build the prompt specificatio, including the return functionality.
        var syncPromptSpecification = new McpServerFeatures.SyncPromptSpecification(
          prompt,
          (exchange, request) ->
          {
              String promptText =
                """
                No adventure was found, so make one up based on a NPC leaving a bag of something
                and a note next to the NPC, either outside their door, at their table, or just
                throwing it at their feet. Be creative and don't use cliches. Use any oracles you
                have to build up parts of your adventure, and remember to NOT share the advenute
                outline with the player. You should also note an outline of your adventure in
                the DM Journal so that you can remember it for later.
                """;

              // Make sure we have ExtraInfo args
              // otherwise ST throws up.
              var fixedArgs = new HashMap<>(
                request.arguments());
              fixedArgs.putIfAbsent("Length", "");

              // Load up full prompt from chatdmdir
              try {
                  promptText = chatDMDir.readSTFile(ADVENTURE_PROMPT_FILE_PATH, fixedArgs);
              } catch (IOException e) {
                  logger.error("Using hard-coded Startup prompt. Could not read DM Startup Prompt file {}",
                               ADVENTURE_PROMPT_FILE_PATH, e);
              }

              McpSchema.GetPromptResult result = new McpSchema.GetPromptResult(
                "Template for creating an adventure. Follow the instructions carefully.",
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
