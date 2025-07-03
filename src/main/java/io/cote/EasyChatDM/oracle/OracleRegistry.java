package io.cote.EasyChatDM.oracle;

import io.cote.EasyChatDM.ChatDMDir;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

@Service
public class OracleRegistry {

    private static final Logger logger = LoggerFactory.getLogger(OracleRegistry.class);

    private Map<String, Oracle> oracles = new HashMap<>();

    private final ChatDMDir chatDMDir;

    public OracleRegistry(ChatDMDir chatDMDir) {
        this.chatDMDir = chatDMDir;
    }

    /**
     * Get the named oracle if it exists.
     *
     * @param name name of the Oracle.
     * @return the Oracle, or null if it does not exist.
     */
    public Oracle get(String name) {
        return oracles.get(name);
    }

    public Set<String> listNames() {
        return Collections.unmodifiableSet(oracles.keySet());
    }

    public Collection<Oracle> getAll() {
        return Collections.unmodifiableCollection(oracles.values());
    }

    public int size() {
        return oracles.size();
    }

    /**
     * Loads the files and makes the Oracles. It will do this each time.
     * This method is package private for testing reasons.
     *
     * Warning: It's possible that some oracles will overwrite each other if they have the same names
     * in their <code>yaml</code> declarations and the filename used for plain text oracles.
     * This has not been tested.
     */
    @PostConstruct
    synchronized void init() {
        Map<String, String> oracleFiles = chatDMDir.loadBundleDir("oracles/named/");
        // reset in case this is being called again so that we match the bundle dir.
        oracles = new HashMap<>();
        for (Map.Entry<String, String> entry : oracleFiles.entrySet()) {
            String fileName = entry.getKey();
            String fileContent = entry.getValue();

            try {
                if (fileName.endsWith(".txt") || fileName.endsWith(".st")) {
                    String oracleName = fileName.replaceFirst("\\.[^.]+$", ""); // strip extension

                    Oracle oracle = parseST(oracleName, fileContent, null);
                    oracles.put(oracleName, oracle);
                    logger.info("Loaded oracle: {}", oracleName);

                } else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
                    List<Oracle> parsedOracles = parseYaml(fileContent);

                    for (Oracle oracle : parsedOracles) {
                        oracles.put(oracle.name(), oracle);
                    }
                }
            } catch (Exception e) {
                logger.error("Error loading {}. Skipping oracle file. Exception:", fileName, e);
                logger.debug("Error loading {}. With content {}", fileName, fileContent);
            }

        }

    }

    /**
     * Parses a plain text file (as an ST file). Each line is treated as an Oracle result. A line that begins with a #
     * is a comment and is ignored.
     *
     * @param text in ST file format.
     * @param args args to apply to the ST file
     * @return Oracle read from the file where each line is an Oracle result.
     */
    private Oracle parseST(String oracleName,
                           String text,
                           Map<String, Object> args) {
        ST st = new ST(text);
        // add in any args passed in to use in the template
        if (args != null) {
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                st.add(entry.getKey(), entry.getValue());
                logger.debug("Added {} to ST {}", entry.getKey(), entry.getValue());
            }
        }

        logger.debug("Read oracle file {} as ST {}", oracleName, st);
        String renderedText = st.render();
        List<String> lines = new ArrayList<>();
        for (String line : renderedText.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }

        return new Oracle(oracleName, "", Map.of(), lines);
    }

    private List<Oracle> parseYaml(String yamlContent) {
        List<Oracle> oracles = new ArrayList<>();
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(yamlContent);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String oracleName = entry.getKey();

            Map<String, Object> oracleValues = (Map<String, Object>) entry.getValue();
            List<String> results = (List<String>) oracleValues.get("results");

            String description = (String) oracleValues.get("description");

            // collect the metadata - all the fields except name, description, and results
            Map<String, String> metadata = new HashMap<>();

            for (Map.Entry<String, Object> metadataEntry : oracleValues.entrySet()) {
                String metadataKey = metadataEntry.getKey();
                if (!metadataKey.equals("description") && !metadataKey.equals("results")) {
                    metadata.put(metadataKey, metadataEntry.getValue().toString());
                }
            }

            Oracle oracle = new Oracle(oracleName, description, metadata, results);
            oracles.add(oracle);
        } return oracles;
    }

}
