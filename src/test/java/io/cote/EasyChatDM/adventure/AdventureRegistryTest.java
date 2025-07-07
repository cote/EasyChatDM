package io.cote.EasyChatDM.adventure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cote.EasyChatDM.TestThatUsesChatDMDir;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AdventureRegistryTest extends TestThatUsesChatDMDir {

    @Test
    public void testAdventureFileRoundTrip() throws Exception {

        List scenes = List.of("Scene 01: a gnome walking into a bar.", "Scene 02: a dragon eats a gnome");
        Adventure a = new Adventure("A testing adventure", scenes, 2);

        // should use the JSON ObjectMapper.
        AdventureRegistry registry = new AdventureRegistry(chatDMDir(), new ObjectMapper());

        registry.saveAdventure(a);
        Adventure loaded = registry.loadAdventure("A testing adventure");

        assertThat(loaded).isEqualTo(a).withFailMessage(
          "Adventure should be equal after round trip of saving and then loading");

    }

    @Test
    void testSanitizeFilename() {
        assertEquals("my_adventure_name", AdventureRegistry.sanitizeFilename("My Adventure Name"));
        assertEquals("adventure_2025_07_07_", AdventureRegistry.sanitizeFilename("Adventure @2025-07-07!"));
        assertEquals("notes_3", AdventureRegistry.sanitizeFilename("Notes #3"));
        assertEquals("weird_spacing", AdventureRegistry.sanitizeFilename("   weird   spacing  "));
        assertEquals("", AdventureRegistry.sanitizeFilename(null));
        assertEquals("", AdventureRegistry.sanitizeFilename(""));
    }

}
