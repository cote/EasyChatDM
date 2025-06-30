package io.cote.EasyChatDM.oracle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An Oracle record that stores a name, metadata, and a list of results. Provides methods to access metadata and
 * descriptions. Random result selection is handled via a static method.
 *
 * @param name name for the Oracle
 * @param metadata key/value metadata. Keys are arbitrary, but description is usually used.
 * @param results these are the list of possible results that will be randomly picked each time the Oracle is consulted.
 */
public record Oracle(String name, Map<String, String> metadata, List<String> results) {

    public Oracle {
        Objects.requireNonNull(name, "Oracle must have a name.");
        Objects.requireNonNull(metadata, "Oracle must have metadata. Use an empty map if there are no metadata.");
        Objects.requireNonNull(results, "Oracle must have results. Use an empty list if there are no results.");
        // Ensure metadata and results are immutable copies
        metadata = Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
        results = Collections.unmodifiableList(new ArrayList<>(results));
    }

    /**
     * Returns the description of the oracle (pulled from metadata).
     *
     * @return The oracle description, or null if not present
     */
    public String description() {
        return metadata("description");
    }

    /**
     * Retrieves the value of the metadata with <code>name</code>, or null if there is no metadata by that name.
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
     * @param oracle The oracle to pick from
     * @return A randomly selected result or a fallback message if empty
     */
    public static String randomResult(Oracle oracle) {
        if (oracle.results.isEmpty()) {
            return "No results available.";
        }
        return oracle.results.get(ThreadLocalRandom.current().nextInt(oracle.results.size()));
    }

    /**
     * Returns an empty Oracle
     * @return an empty Oracle.
     */
    public static Oracle emptyOracle() {
        return new Oracle("", Map.of(), List.of());}
}
