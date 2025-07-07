package io.cote.EasyChatDM;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DMJournalTest extends TestThatUsesChatDMDir {

    @Test
    void addEntry() throws IOException {
        DMJournalRepository dmJournalStore = new DMJournalRepository(chatDMDir());

        // should be empty journal, add one entry.
        dmJournalStore.addEntry("test");
        Journal j = dmJournalStore.readJournal();
        assertNotNull(j, "Journal should not be null after adding one entry.");
        assertEquals(1, j.getEntries().size(), "Journal should have one entry.");
        assertEquals("test", j.getEntries().getFirst().content());

        assertTrue(Files.exists(chatDMDir().getChatDMDir().resolve(dmJournalStore.getDMJournalFileNameMarkdown())),
                   "Markdown file should exist after writing.");

        // clean-up file so it starts fresh with each test
        Files.deleteIfExists(chatDMDir().getChatDMDir().resolve(dmJournalStore.getDMJournalFileNameJSON()));
    }

    @Test
    void getJournal() throws IOException {
        DMJournalRepository dmJournalStore = new DMJournalRepository(chatDMDir());

        Journal j = dmJournalStore.readJournal();
        assertNotNull(j, "Journal should not be null after creating a new instance.");

        // create some journal entries.
        dmJournalStore.addEntry("test1");
        dmJournalStore.addEntry("test2");

        // refresh
        j = dmJournalStore.readJournal();

        assertEquals(2, j.getEntries().size(), "Journal should have two entries.");
        // the order should be the this since it is oldest first, newest last
        assertEquals("test1", j.getEntries().getFirst().content());
        assertEquals("test2", j.getEntries().getLast().content());

        // clean-up file so it starts fresh with each test
        Files.deleteIfExists(chatDMDir().getChatDMDir().resolve(dmJournalStore.getDMJournalFileNameJSON()));
    }

    @Test
    void getRecentEntries() {
    }

}