package io.cote.EasyChatDM;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

/**
 * An ongoing DM Journal.
 *
 * This journal is backed by a JSON file which is re-created each time an entry is added. This
 * means that if you (attempt) to add a new entry to any returned {@link Journal}, it will not
 * be saved and will be discarded. Only use {@link #addEntry} to create new Journal entries.
 */
@Service
public class DMJournalRepository {

    // TK in the future, will need rotating DM Journals. For now, who cares?
    // TK also, can make a version that stores the DM Journal in a database instead.
    // TK also, a version that uses RAG to just retrieve relevant stuff.

    Logger logger = LoggerFactory.getLogger(DMJournalRepository.class);

    private final ChatDMDir chatDMDir;

    public DMJournalRepository(ChatDMDir chatDMDir) {
        this.chatDMDir = chatDMDir;
        logger.info("DMJournalRepository created with chatDMDir dir {}", chatDMDir.getChatDMDir());
    }

    /**
     * Creates an entry with {@link Journal.Entry} defaults.
     *
     * @param content the contents of the entry
     */
    public void addEntry(String content) throws IOException {
        // First, need to read in the JSON file
        Journal dmJournal = readJournal();

        Journal.Entry entry = new Journal.Entry(new Date(), Journal.Entry.AUTHOR_DM, "", content);
        dmJournal.addEntry(entry);
        logger.debug("Added entry to DM Journal: {}", entry);

        // Write out to JSON file.
        // We just rewrite the entire file for now.
        ObjectMapper mapper = new ObjectMapper();
        chatDMDir.writeFile(getDMJournalFileNameJSON(), mapper.writeValueAsString(dmJournal));
        writeJournal(dmJournal);
    }

    private void writeJournal(Journal journal) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        chatDMDir.writeFile(getDMJournalFileNameJSON(), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(journal));
        logger.debug("Wrote full DM Journal to file {}", getDMJournalFileNameJSON());
        chatDMDir.writeFile(getDMJournalFileNameMarkdown(), convertToMarkdown(journal));
        logger.debug("Wrote full DM Journal to markdown {}", getDMJournalFileNameMarkdown());
    }

    private String convertToMarkdown(Journal journal) {
        // We save a markdown file for easier human reading.
        StringBuilder sb = new StringBuilder();
        sb.append("# DM Journal: ");
        sb.append(journal.getTitle());
        sb.append("\n\n_This file is for human easy reading. Any modifications will be ignored and overwritten._\n\n");
        for(Journal.Entry entry : journal.getEntries()) {
            sb.append("## ");
            sb.append(entry.date());
            sb.append("\n\n");

            if (entry.summary() != null && !entry.summary().isBlank()) {
                sb.append("_");
                sb.append(entry.summary());
                sb.append("_\n\n");
            }

            sb.append(entry.content());
            sb.append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Returns the current journal. The journal is re-read from the backing JSON file every
     * time. Any changes made to Journal will be discarded. Instead, use {@link #addEntry(String)}.
     *
     * @return the current DM journal.
     */
    public Journal readJournal() {
        Path journalFile = chatDMDir.getChatDMDir().resolve(getDMJournalFileNameJSON());
        String charCode = chatDMDir.getChatDMDir().relativize(journalFile).toString();
        logger.debug("Reading DM Journal from file {}", charCode);
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            return mapper.readValue(chatDMDir.readFile(charCode), Journal.class);
        } catch (IOException e) {
            logger.error("Error reading DM Journal from file {}", charCode, e);
            return new Journal();
        }
    }

    String getDMJournalFileNameJSON() { return "journals/" + Journal.Entry.AUTHOR_DM + "_journal.json"; }

    String getDMJournalFileNameMarkdown() { return "journals/" + Journal.Entry.AUTHOR_DM + "_journal.md"; }


    public String toString() {
        return "DMJournalRepository[chatDMDir=" + chatDMDir + ", dmJournalFileName=" + getDMJournalFileNameJSON() + "]";
    }
}
