package io.cote.EasyChatDM.adventure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AdventureTools {

    private final Logger logger = LoggerFactory.getLogger(AdventureTools.class);
    private AdventureRegistry adventureRegistry;

    public AdventureTools(AdventureRegistry adventureRegistry) {
        this.adventureRegistry = adventureRegistry;
    }

    @Tool(name = "EasyChatDM_load_adventure", description = "Loads an adventure from the adventures store.")
    public Adventure loadAdventure(@ToolParam(description = "The name of the adventure to load") String adventureName) throws IOException {
        return adventureRegistry.loadAdventure(adventureName);
    }

    @Tool(name = "EasyChatDM_save_adventure", description = "Saves an adventure to the adventures store.")
    public void saveAdventure(@ToolParam(description = "The adventure to save") Adventure adventure) throws IOException {
        adventureRegistry.saveAdventure(adventure);
    }

    @Tool(name = "EasyChatDM_list_adventures", description = "Lists all adventures in the adventures store.")
    public List<String> listAdventures() {
        return null;
    }
}
