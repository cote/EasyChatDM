package io.cote.EasyChatDM;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static io.modelcontextprotocol.spec.McpSchema.Role.ASSISTANT;

@Component
public class JournalMCPResource {

    private final Logger logger = LoggerFactory.getLogger(JournalMCPResource.class);
    private final DMJournalRepository dmRepository;

    public JournalMCPResource(DMJournalRepository dmRepository) {
        this.dmRepository = dmRepository;
    }


    @Bean
    List<McpServerFeatures.SyncResourceSpecification> loadDMJournalResource() {

        // Here's the resource that is going to be returned.
        McpSchema.Resource resource = new McpSchema.Resource("file:///easychatdm/journal/dm-journal.json",
                "DM Journal",
                "The DM Journal is a place to keep notes for the DM to use when writing adventures.",
                "application/json",
                new McpSchema.Annotations(List.of(ASSISTANT), 1.0));

        McpServerFeatures.SyncResourceSpecification reg =
                new McpServerFeatures.SyncResourceSpecification(
                        resource,
                        (exchange, request) -> {

                            logger.debug("JournalMCPResource called with request {}", request);

                            String journalJSON = "";
                            try {
                                journalJSON = dmRepository.readJournalAsJSON();
                            } catch (IOException e) {
                                // It'd be fun to return a log error through exchange.log...?
                                exchange.loggingNotification(new McpSchema.LoggingMessageNotification(McpSchema.LoggingLevel.ERROR, "easychatdm", e.getMessage()));
                                logger.error("Error reading DM Journal file. Returning empty journal.", e);
                            }

                            McpSchema.TextResourceContents contents = new McpSchema.TextResourceContents(
                                    resource.uri(),
                                    resource.mimeType(),
                                    journalJSON
                            );

                            return new McpSchema.ReadResourceResult(
                                    List.of(contents));
                        });


        return List.of(reg);
    }


}
