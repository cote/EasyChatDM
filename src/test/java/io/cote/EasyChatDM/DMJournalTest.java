package io.cote.EasyChatDM;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@TestPropertySource(properties = {"easychatdm.dir=${java.io.tmpdir}"})
class DMJournalTest {

    @Autowired
    ChatDMDir chatDMDir;

    @TempDir
    static Path tempDir;

    // make sure we're use the test's tempdir.
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("easychatdm.dir", () -> tempDir.toString());
    }

    @Test
    void addEntry() throws IOException {
        DMJournalRepository dmJournalStore = new DMJournalRepository(chatDMDir);

        // should be empty journal, add one entry.
        dmJournalStore.addEntry("test");
        Journal j = dmJournalStore.readJournal();
        assertNotNull(j, "Journal should not be null after adding one entry.");
        assertEquals(1, j.getEntries().size(), "Journal should have one entry.");
        assertEquals("test", j.getEntries().getFirst().content());

        assertTrue(Files.exists(chatDMDir.getChatDMDir().resolve(dmJournalStore.getDMJournalFileNameMarkdown())),
                "Markdown file should exist after writing.");

        // clean-up file so it starts fresh with each test
        Files.deleteIfExists(chatDMDir.getChatDMDir().resolve(dmJournalStore.getDMJournalFileNameJSON()));
    }

    @Test
    void getJournal() throws IOException {
        DMJournalRepository dmJournalStore = new DMJournalRepository(chatDMDir);

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

        // make sure they are written out to JSON.
        Path jsonFile = tempDir.resolve(dmJournalStore.getDMJournalFileNameJSON());
        assertTrue(jsonFile.toFile().exists(), "JSON file should exist after writing.");

        // writing out to JSON should use the author in the filename?
        // load them back up to check.

        // clean-up file so it starts fresh with each test
        Files.deleteIfExists(chatDMDir.getChatDMDir().resolve(dmJournalStore.getDMJournalFileNameJSON()));
    }

    @Test
    void getRecentEntries() {
    }

}