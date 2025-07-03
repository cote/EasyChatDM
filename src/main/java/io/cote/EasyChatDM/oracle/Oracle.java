package io.cote.EasyChatDM.oracle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An Oracle record that stores a name, metadata, and a list of results. Provides methods to access metadata and
 * descriptions. Random result selection is handled via a static method.
 *
 * @param name name for the Oracle
 * @param description a description for the Oracle, may be empty or null
 * @param metadata key/value metadata. Keys are arbitrary, but description is usually used.
 * @param results these are the list of possible results that will be randomly picked each time the Oracle is consulted.
 */
public record Oracle(String name, String description, Map<String, String> metadata, List<String> results) {

    public Oracle {
        Objects.requireNonNull(name, "Oracle must have a name.");
        Objects.requireNonNull(results, "Oracle must have results. Use an empty list if there are no results.");

        if (description == null) { description = "";}
        if (metadata == null) { metadata = Collections.emptyMap();}
        // Ensure metadata and results are immutable copies
        metadata = Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
        results = Collections.unmodifiableList(new ArrayList<>(results));
    }

    /**
     * Retrieves the value of the metadata with <code>name</code>, or null if there is no metadata by that name.
     * Metadata fields are not specified. But, you might consider using type, author, version, or category.
     * The values must be just a string. If the value of metadata is nested, it will result in something
     * other than just a String, the value will likely be {@link Object#toString()} returns, or the file may fail
     * to parse.
     *
     * @param name name of metadata to attempt to retrieve
     * @return value of the metadata, or null if there is no metadata or value.
     */
    public String metadata(String name) {
        return metadata.get(name);
    }


    /**
     * Picks a random result from the provided Oracle instance.
     *
     * @return A randomly selected result or a fallback message if empty
     */
    public String randomResult() {
        if (results.isEmpty()) {
            return "No results available.";
        }
        return results.get(ThreadLocalRandom.current().nextInt(results.size()));
    }
}
