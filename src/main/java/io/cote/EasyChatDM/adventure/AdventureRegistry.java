package io.cote.EasyChatDM.adventure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cote.EasyChatDM.ChatDMDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * A place to store the current adventure.
 */
@Service
public class AdventureRegistry {

    private static final Logger logger = LoggerFactory.getLogger(AdventureRegistry.class);

    private final ChatDMDir chatDMDir;
    private final ObjectMapper objectMapper;

    AdventureRegistry(ChatDMDir chatDMDir,
                      ObjectMapper mapper) {
        this.chatDMDir = chatDMDir;
        this.objectMapper = mapper;
    }

    void saveAdventure(Adventure adventure) throws IOException {
        String filePath = makeAdventureFileName(sanitizeFilename(adventure.name()));
        chatDMDir.writeFile(filePath, objectMapper.writeValueAsString(adventure));
        logger.debug("Saved adventure to {}", filePath);
    }

    Adventure loadAdventure(String adventureName) throws IOException {
        String filePath = makeAdventureFileName(sanitizeFilename(adventureName));
        String adventureContents = chatDMDir.loadContents(filePath);
        Adventure adventure = objectMapper.readValue(adventureContents, Adventure.class);
        logger.info("Loaded adventure from {}", filePath);
        return adventure;
    }

    static String sanitizeFilename(String input) {
        if (input == null) return "";
        input = input.trim();
        // Replace all whitespace and punctuation with underscores
        String result = input.replaceAll("[\\s\\p{Punct}]+", "_");
        // Remove any non-word characters (e.g., emojis, symbols) except underscore
        result = result.replaceAll("[^\\w_]", "");
        return result.toLowerCase();
    }

    List<Adventure> loadAllAdventures() throws IOException {
        //        Path adventureDir = chatDMDir.resolve("adventures");
        //
        //        if (!Files.exists(adventureDir) || !Files.isDirectory(adventureDir)) {
        //            logger.warn("Adventure directory does not exist: {}", adventureDir);
        //            return List.of();
        //        }
        //
        //        List<Adventure> adventures = new ArrayList<>();
        //        try (DirectoryStream<Path> files = Files.newDirectoryStream(adventureDir, "*.json")) {
        //            for (Path path : files) {
        //                try {
        //                    String content = Files.readString(path);
        //                    Adventure adventure = objectMapper.readValue(content, Adventure.class);
        //                    adventures.add(adventure);
        //                } catch (IOException e) {
        //                    logger.warn("Failed to load adventure from {}: {}", path, e.getMessage());
        //                }
        //            }
        //        }
        //        return adventures;
        return null;
    }

    private String makeAdventureFileName(@NonNull String adventureName) {
        return "adventures/" + adventureName + ".json";
    }

}
