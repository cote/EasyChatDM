package io.cote.EasyChatDM.oracle;


import io.cote.EasyChatDM.TestThatUsesChatDMDir;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OracleRegistryTest extends TestThatUsesChatDMDir {

    @Test
    public void testOracleLoading() throws IOException {
        // create test files

        // the txt files
        String oracleOne = "Sad\nHappy\nMad\nBored";
        String oracleTwo = "Tea\nAle\nCoffee\nWater\nWine";
        String oracleThree = "Troll\nOgre\nHill Giant\nBronze Dragon";

        chatDMDir().writeFile(Path.of("oracles/", "npc_emotions.txt"), oracleOne);
        chatDMDir().writeFile(Path.of("oracles/named/", "drinks.txt"), oracleTwo);
        chatDMDir().writeFile(Path.of("oracles/", "monsters.txt"), oracleThree);

        // The yaml file
        String npcMood = """
                         "NPC Mood":
                           description: Use this oracle to determine an NPC's mood.
                           type: oracle
                           author: Coté's Tests
                           version: 1.0
                           category: NPC
                           results:
                             - Angry
                             - Sad
                             - Winsome
                             - Neutral
                             - Neutral
                             - Neutral
                             - Neutral
                             - Pleasant
                             - Happy
                             - Ecstatic
                         """;

        chatDMDir().writeFile(Path.of("oracles/named/", "npc_mood.yml"), npcMood);

        // Should be testing Spring doing all of this, but it's annoying
        // to control when the files above are made, so we'll compromise
        // and do it manually here.
        OracleRegistry oracleRegistry = new OracleRegistry(chatDMDir());
        oracleRegistry.init();

        assertThat(oracleRegistry.size()).isEqualTo(4).withFailMessage("Should have 4 oracles.");

        // test the plain text
        assertThat(oracleRegistry.get("npc_emotions")).isNotNull().withFailMessage("Should have npc_emotions oracle.");
        assertThat(oracleRegistry.get("named/drinks")).isNotNull().withFailMessage("Should have npc_emotions oracle.");
        assertThat(oracleRegistry.get("monsters")).isNotNull().withFailMessage("Should have npc_emotions oracle.");

        Oracle npcEmotions = oracleRegistry.get("npc_emotions");
        assertThat(npcEmotions.results().size()).isEqualTo(4);
        npcEmotions.results().containsAll(List.of("Sad", "Happy", "Mad", "Bored"));

        // We don't test the exact values, just that they exist
        Oracle drinks = oracleRegistry.get("named/drinks");
        assertThat(drinks.results().size()).isEqualTo(5);

        Oracle monsters = oracleRegistry.get("monsters");
        assertThat(monsters.results().size()).isEqualTo(4);

        // test the yaml
        Oracle npcMoodFromYaml = oracleRegistry.get("NPC Mood");
        assertThat(npcMoodFromYaml).isNotNull().withFailMessage("Should have NPC Mood oracle, from yaml");

        assertThat(npcMoodFromYaml.results().size()).isEqualTo(10).withFailMessage(
          "Expected 10 results in yaml oracle.");

        assertThat(npcMoodFromYaml.description()).isEqualTo("Use this oracle to determine an NPC's mood.");

        assertThat(npcMoodFromYaml.metadata().size()).isEqualTo(4).withFailMessage("Expected 4 meta data values.");
        assertThat(npcMoodFromYaml.metadata("type")).isEqualTo("oracle");
        assertThat(npcMoodFromYaml.metadata("author")).isEqualTo("Coté's Tests");
        assertThat(npcMoodFromYaml.metadata("version")).isEqualTo("1.0");
        assertThat(npcMoodFromYaml.metadata("category")).isEqualTo("NPC");

    }

}
