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
import java.util.*;

@Service
public class DMMCPPrompt {

    private final Logger logger = LoggerFactory.getLogger(DMMCPPrompt.class);
    private final ChatDMDir chatDMDir;
    private static final Path STARTUP_PROMPT_FILE_PATH = Path.of("prompts/dm_startup.st");

    private final String defaultPrompt = """
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
    List<McpServerFeatures.SyncPromptSpecification> startupPrompt() throws IOException {

        // Sync prompt specification
        MCPPromptBuilder promptBuilder = new MCPPromptBuilder();
        promptBuilder.name("DM_Startup_Prompt");
        promptBuilder.description("""
                      This prompt gives instructions to the Chat DM AI for how to 
                      become, behave, and play as a Dungeons and Dragons DM. Thes instructions
                      should be followed carefully and followed. 
                      """);
        promptBuilder.addArgument("context",
                      "Any relevant context for why you're asking for the DM journal right now, what you're looking for and what you might do with it, etc.");
        promptBuilder.addArgument("ExtraInfo",
                      "Extra information given by the player to help them understand what they're doing.");

        var syncPromptSpecification = new McpServerFeatures.SyncPromptSpecification(promptBuilder.build(), (exchange, request) -> {

            GetPromptResultBuilder promptResultBuilder = new GetPromptResultBuilder();
            promptResultBuilder.description("Prompt to start playing D&D");

            try {

                // Make sure we have required attriutes for ST template
                var fixargs = new HashMap<>(request.arguments());
                fixargs.putIfAbsent("ExtraInfo", "");

                String startupPrompt = chatDMDir.readSTFile(STARTUP_PROMPT_FILE_PATH, fixargs);
                promptResultBuilder.addMessage(startupPrompt);
            } catch (IOException e) {
                logger.error("Using default Startup Prompt. Error reading DM Startup Prompt file {}",
                             STARTUP_PROMPT_FILE_PATH,
                             e);

                // Just for fun! It's see if anything happens that's interesting
                MCPUtils.logError("Using defaults Startup Prompt. Error reading file.");

                promptResultBuilder.addMessage(defaultPrompt);
            }

            return promptResultBuilder.build();
        });

        return List.of(syncPromptSpecification);
    }

    // TK extract these into their own utils when I use them next time.

    class GetPromptResultBuilder {
        private String description = "";
        private List<McpSchema.PromptMessage> args = new ArrayList();

        /**
         * Optional description. If null, will be set to an empty string.
         *
         * @param description
         * @return
         */
        GetPromptResultBuilder description(String description) {
            this.description = Objects.requireNonNullElse(description, "");
            return this;
        }

        GetPromptResultBuilder() {
        }

        GetPromptResultBuilder(String content) {
            this();
            addMessage(content);
        }

        GetPromptResultBuilder addMessage(String content) {
            addMessage(McpSchema.Role.ASSISTANT, content);

            return this;
        }

        GetPromptResultBuilder addMessage(McpSchema.Role role, String content) {
            role = Objects.requireNonNullElse(role, McpSchema.Role.ASSISTANT);
            content = Objects.requireNonNullElse(content, "");
            args.add(new McpSchema.PromptMessage(role, new McpSchema.TextContent(content)));

            return this;
        }

        McpSchema.GetPromptResult build() {
            return new McpSchema.GetPromptResult(description, args);
        }
    }

    class MCPPromptBuilder {
        private Prompt prompt;
        private String name = "";
        private String description = "";
        private List<McpSchema.PromptArgument> args = new ArrayList();

        MCPPromptBuilder name(String name) {
            Objects.requireNonNull(name, "Prompt name cannot be null.");
            this.name = name;

            return this;
        }

        MCPPromptBuilder description(String description) {
            this.description = Objects.requireNonNullElse(description, "");

            return this;
        }

        MCPPromptBuilder addArgument(String name, String description, boolean required) {
            Objects.requireNonNull(name, "Prompt name cannot be null.");

            McpSchema.PromptArgument arg = new McpSchema.PromptArgument(name,
                                                                        Objects.requireNonNullElse(description, ""),
                                                                        required);
            args.add(arg);

            return this;
        }

        /**
         * Convience method for {@link #addArgument(String, String, boolean) addArgument(name, description, true)}.},
         * setting required to <code>true</code>.
         *
         * @param name        cannot be null.
         * @param description if null, will be set to an empty string.
         * @return this builder, for chaining.
         */
        MCPPromptBuilder addArgument(String name, String description) {
            return addArgument(name, description, true);
        }

        Prompt build() {
            Objects.requireNonNull(name, "Prompt name cannot be null.");
            return prompt = new Prompt(name, description, args);
        }
    }
}
