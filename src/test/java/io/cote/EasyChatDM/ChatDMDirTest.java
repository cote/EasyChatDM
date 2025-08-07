package io.cote.EasyChatDM;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ChatDMDirTest extends TestThatUsesChatDMDir {

    // Extending TestThatUsesChatDMDir does not work here for some reason,
    // so I've left it like this for now.


    @Value("${easychatdm.dir}")
    private Path actualChatDMDir;

    protected Path getActualChatDMDir() {
        return actualChatDMDir;
    }

    @Autowired
    private ChatDMDir chatDMDir;

    protected ChatDMDir chatDMDir() {
        return chatDMDir;
    }

    @Test
    void testReadFile() throws IOException {
        Path file = getActualChatDMDir().resolve("testReadFile.txt");
        String fileContents = """
                              # for ChatDMDirTest#testReadFile()
                              # This is a comment
                              line1
                              line2
                              # another comment
                              line3
                              """;
        Files.writeString(file, fileContents);

        String content = chatDMDir().readFile("testReadFile.txt");
        assertThat(content).isEqualToIgnoringWhitespace(fileContents);

    }

    @Test
    void testWriteFile() throws IOException {
        String contents = "# for ChatDMDirTest#testWriteFile\nline 1\nline 2";
        chatDMDir().writeFile("testWriteFile.txt", "test content");

        chatDMDir().writeFile("testWriteFile.txt", contents);

        Path testFile = getActualChatDMDir().resolve("testWriteFile.txt");
        Files.readString(testFile);
        assertThat(Files.readString(testFile)).isEqualToIgnoringWhitespace(contents);
    }

    @Test
    void testGetAllLines_FileExists() throws IOException {
        // Arrange
        Path file = getActualChatDMDir().resolve("testfile.txt");
        Files.writeString(file, """
                                # This is a comment
                                line1
                                line2
                                # another comment
                                line3
                                """);

        // Act
        List<String> lines = chatDMDir().getAllLines(file.getFileName());

        // Assert
        assertThat(lines).containsExactly("line1", "line2", "line3");
    }

    @Test
    void testGetAllLines_FileDoesNotExist() throws IOException {
        // Act
        List<String> lines = chatDMDir().getAllLines(Path.of("missing.txt"));

        // Assert
        assertThat(lines).isEmpty();
    }

    @Test
    void testGetAllLines_UpwardTraversal() {
        // Act & Assert
        assertThatThrownBy(() -> chatDMDir().getAllLines(Path.of("../evil.txt"))).isInstanceOf(
          IllegalArgumentException.class).hasMessageContaining("upward traversal");
    }

    @Test
    void testGetAllLines_DirectoryProvided() throws IOException {
        // Arrange
        Path subDir = Files.createDirectory(getActualChatDMDir().resolve("subdir"));

        // Act
        List<String> lines = chatDMDir().getAllLines(subDir.getFileName());

        // Assert
        assertThat(lines).isEmpty();
    }

    @Test
    void testLoadBundleDir() throws IOException {
        // This gets a little confusing if you trace/dig into the code because of the handling
        // I put into the ChatDMDir to keep people from passing in absolute file paths.

        // create some test files
        String oracleOne = "Sad\nHappy\nMad\nBored";
        String oracleTwo = "Tea\nAle\nCoffee\nWater\nWine";
        String oracleThree = "Troll\nOgre\nHill Giant\nBronze Dragon";

        chatDMDir().writeFile(Path.of("oracles2/named/", "npc_emotions.txt"), oracleOne);
        chatDMDir().writeFile(Path.of("oracles2/named/", "drinks.txt"), oracleTwo);
        chatDMDir().writeFile(Path.of("oracles2/named/", "monsters.txt"), oracleThree);

        Map<String, String> files = chatDMDir().loadBundleDir("oracles2/named/");

        assertThat(files).withFailMessage("Did not create 3 top-level files.").hasSize(3);

        assertThat(files.get("npc_emotions.txt")).isEqualToIgnoringWhitespace(oracleOne);
        assertThat(files.get("drinks.txt")).isEqualToIgnoringWhitespace(oracleTwo);
        assertThat(files.get("monsters.txt")).isEqualToIgnoringWhitespace(oracleThree);

        // Test nested and that nested don't overlap

        chatDMDir().writeFile(Path.of("oracles2/named/npcs/", "npc_emotions.txt"), oracleOne);
        chatDMDir().writeFile(Path.of("oracles2/named/drinks/", "drinks.txt"), oracleTwo);
        chatDMDir().writeFile(Path.of("oracles2/named/monsters/", "monsters.txt"), oracleThree);

        Map<String, String> filesNested = chatDMDir().loadBundleDir("oracles2/named/");
        assertThat(filesNested).withFailMessage("Did not create 6 nested files.").hasSize(6);

        assertThat(filesNested.get("npcs/npc_emotions.txt")).isEqualToIgnoringWhitespace(oracleOne);
        assertThat(filesNested.get("drinks/drinks.txt")).isEqualToIgnoringWhitespace(oracleTwo);
        assertThat(filesNested.get("monsters/monsters.txt")).isEqualToIgnoringWhitespace(oracleThree);

    }

    @Test
    void testClasspathLoading() throws IOException {
        // the ChatDMDir should check for files in the classpath first
        // and then, if the files are in the file system, override that
        // default from the classpath.

        // First test with single, known file.

        String classpathShopsOracle = chatDMDir().loadContents("oracles/shops.txt");
        assertThat(classpathShopsOracle).isNotNull().withFailMessage(
          "Should have oracles/shops.txt oracle from classpath.");

        String overrideOracle = "Magic Shop\nWine Shop\nArmory\nBook Shop";

        chatDMDir().writeFile(Path.of("oracles/", "shops.txt"), overrideOracle);

        String overrideOracleFromFiles = chatDMDir().loadContents("oracles/shops.txt");
        assertThat(overrideOracleFromFiles).isEqualTo(overrideOracle).withFailMessage(
          "Did not load Oracle from file system");

        assertThat(overrideOracleFromFiles).isNotEqualTo(classpathShopsOracle).withFailMessage(
          "Oracle from classpath and file should not be equal. File system should overwrite classpath.");

        // Test for loading all files in a bundle.
        // We've overwritten one shops.txt, so we should just see two.
        Map<String, String> files = chatDMDir().loadBundleDir("oracles/");
        assertThat(files).withFailMessage("Incorrect number of oracles: " + files.size()).hasSize(14);
    }

}