package io.cote.EasyChatDM;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.*;

/**
 * A journal used to keep track of the game progress.
 */
public class Journal {

    private final String title;

    // Using TreeSet with a comparator to maintain chronological order
    private final Set<Entry> entries = new TreeSet<>(
            Comparator.comparing(Entry::date)
                    // Add secondary sort criteria for entries with the same date
                    .thenComparing(Entry::author)
                    .thenComparing(Entry::content)
    );


    public Journal(String title) {
        this.title = title;
    }

    // For JSON serialization - Jackson will use this
    // No args constructor may be needed by some frameworks
    Journal() {
        this.title = "";
    }

    @JsonCreator
    public Journal(@JsonProperty("title") String title,
                   @JsonProperty("entries") List<Entry> entries) {
        this.title = title;
        if (entries != null) {
            this.entries.addAll(entries);
        }
    }

    public String getTitle() {
        return title;
    }

    /**
     * Returns all the journal entries, oldest first, newest last.
     *
     * @return an unmodifiable List of entries.
     */
    public List<Entry> getEntries() {
        return entries.stream().toList();
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    /**
     * Adds an entry with the defaults of {@link LocalDate#now}, {@link Entry#AUTHOR_DM, and an empty summary}
     *
     * @param content the log entry. It can be blank, but that would be weird.
     */
    public void addEntry(String content) {
        addEntry(new Entry(new Date(), Entry.AUTHOR_DM, "", content));
    }

    /**
     * @param date    the real-world date of the journal entry. If null, {@link LocalDate#now()} will be used.
     * @param author  the author. If null or empty, will use {@link Journal.Entry#AUTHOR_DM}, the default author.
     * @param summary a brief summary of the journal. This is intended to be used when worrying about the context window
     *                for the AI. This may be null or empty. It will be set to an empty string if null.
     * @param content full content of the journal entry. This may be null or empty, while permitted, that would be
     *                weird. If it's null, it will be set to an empty string.
     */
    public record Entry(Date date,
                        String author,
                        String summary,
                        String content) {

        /**
         * For use when the author is the DM.
         */
        public static final String AUTHOR_DM = "DM";

        public Entry {
            if (date == null) {
                date = new Date();
            }
            if (author == null || author.isBlank()) {
                author = AUTHOR_DM;
            }
            if (summary == null || summary.isBlank()) {
                summary = "";
            }
            if (content == null || content.isBlank()) {
                content = "";
            }

        }
    }


}



