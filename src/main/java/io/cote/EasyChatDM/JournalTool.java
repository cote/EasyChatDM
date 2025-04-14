package io.cote.EasyChatDM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
public class JournalTool {

    private final Logger logger = LoggerFactory.getLogger(JournalTool.class);
    private final DMJournalRepository dmRepository;

    public JournalTool(DMJournalRepository dmRepository) {
        this.dmRepository = dmRepository;
    }

    @Tool(name = "EasyChatDM_addDMJournalEntry",
            description = """
                    Used by the Dungeon Master (DM) to note down important things to remember and use for playing and DM'ing. 
                    DM journal entries are usually for DM eyes only and are things like state of play, 
                    interesting developments to the plot, new NPCs, and other things a DM and story teller 
                    would keep in their notebook to remember and use ongoing. You would add an entry when something
                    signigicant happens, especially when you start playing or a milestone has been acheived. If the player
                    takes break or hasn't written to you in a long time, write down a journal entry. The Journal is there to 
                    help you remember what's happening between sessions, so update it with yourself in mind.
                    The journal entry should be in markdown text with each entry starting at header three (three hashmarks like "### journal header"). Set 
                    the header value to something date based, in-game date, session number, whatever makes sense to you. Do not use emojis but
                    you can mark up text with bold, italic, etc. And, if there are relavant links you know about for lore or background, create
                    inline links.""")
    public void addDMJournalEntry(@ToolParam(description = "The DM journal entry.") String entry,
                                  @ToolParam(description = "Any interesting context for why you are writing this entry right now") String context)
            throws IOException {
        dmRepository.addEntry(entry);
        logger.debug("addDMJournalEntry called with context {}", context);
    }

    @Tool(name = "EasyChatDM_readDMJournal",
            description = """
                    Used to get entries from the Dungeon Master (DM) journal. These are notes the DM wanted to keep for later
                    and may want to refer back to as they are crafting the adventure. Journal entries are often notfor players 
                    eyes and are often for DM's eyes only. So, use good judgement in what you share.
                    You should read the Journal when you are starting an adventure back up, when you are uncertain what has happened,
                    or otherwise want to refresh your memory.""")
    public Journal readDMJournal(@ToolParam(description = """
            Any relevant context for why you're asking for the DM journal right now, 
            what you're looking for and what you might do with it, etc.
            """) String context) {
        logger.info("readDMJournal called with context {}", context);
        Journal dmJournal = dmRepository.readJournal();

        if (dmJournal != null) {
            logger.debug("readDMJournal returning DM journal file {} with {} entries", dmJournal, dmJournal.getEntries().size());
            return dmJournal;
        } else {
            logger.warn("readDMJournal returning empty DM journal");
            return new Journal(Journal.Entry.AUTHOR_DM);
        }
    }


}
