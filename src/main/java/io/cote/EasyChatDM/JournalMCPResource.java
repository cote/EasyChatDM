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
                """
                          This is a DM Journal Resource. You can use it to read and interpret the state of the game, events, etc.  
                          You have a separate tool used to add to the journal entry, probably called something like 
                          EasyChatDM_addDMJournalEntry
                          
                          ### What each entry contains
                          
                          - **Scene & Date**: anchors the moment in the narrative.
                          - **Summary**: the key events that occurred.
                          - **NPC Highlights**: updates to relationships, new allies or enemies.
                          - **Key Decisions & Threads**: promises, bargains, unresolved plot lines.
                          - **Loot & Resources**: items, clues, and funds acquired.
                          - **PC Status**: health, spells, conditions, exhaustion.
                          - **Plans & Branching Paths**: upcoming options and forks.
                          
                          ### How to use this journal
                          
                          - **Before starting a scene**, review the most recent entries to set context.
                          - **When players ask** "What happened last time?" quote the Summary of the last entry.
                          - **When an NPC reappears**, consult NPC Highlights to keep their attitude consistent.
                          - **If a plot thread is pursued**, refer to Key Decisions & Threads for background.
                          - **When inventory or clues are needed**, draw from Loot & Resources.
                          - **During combat or healing**, honor the PC Status details.
                          - **When offering choices**, present the Plans & Branching Paths forks or create new branches.
                    """,
                "application/json",
                new McpSchema.Annotations(List.of(ASSISTANT), 1.0));

        McpServerFeatures.SyncResourceSpecification reg =
                new McpServerFeatures.SyncResourceSpecification(
                        resource,
                        (exchange, request) -> {

                            logger.debug("JournalMCPResource called with request {}", request);

                            // TK can thus just return the Journal object and
                            // have the framework marshal it to JSON?
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
