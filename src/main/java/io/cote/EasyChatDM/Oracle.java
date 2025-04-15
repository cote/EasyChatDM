package io.cote.EasyChatDM;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record Oracle(String name, String description, List<String> results) {

    /**
     * Picks one of the random results in the oracle, returning an empty string if there are no results or the Oracle
     * passed in was null.
     *
     * @param oracle the oracle to pick from
     * @return a randomly selected result from the Oracle.
     */
    public static String randomResult(Oracle oracle) {
        if (oracle != null && oracle.results != null && !oracle.results.isEmpty()) {
            return oracle.results.get(ThreadLocalRandom.current().nextInt(oracle.results.size()));
        } else {
            return "";
        }
    }
}
