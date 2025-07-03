package io.cote.EasyChatDM;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ChatDMDirTest {

    @TempDir
    static Path tempDir;

    @Autowired
    ChatDMDir chatDMDir;

    // make sure we're using the test's tempdir.
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("easychatdm.dir", () -> tempDir.toString());
    }

    @Test
    void testReadFile() throws IOException {
        Path file = tempDir.resolve("testReadFile.txt");
        String fileContents = """
                              # for ChatDMDirTest#testReadFile()
                              # This is a comment
                              line1
                              line2
                              # another comment
                              line3
                              """;
        Files.writeString(file, fileContents);

        String content = chatDMDir.readFile("testReadFile.txt");
        assertThat(content).isEqualToIgnoringWhitespace(fileContents);

    }

    @Test
    void testWriteFile() throws IOException {
        String contents = "# for ChatDMDirTest#testWriteFile\nline 1\nline 2";
        chatDMDir.writeFile("testWriteFile.txt", "test content");

        chatDMDir.writeFile("testWriteFile.txt", contents);

        Path testFile = tempDir.resolve("testWriteFile.txt");
        Files.readString(testFile);
        assertThat(Files.readString(testFile)).isEqualToIgnoringWhitespace(contents);
    }

    @Test
    void testGetAllLines_FileExists() throws IOException {
        // Arrange
        Path file = tempDir.resolve("testfile.txt");
        Files.writeString(file, """
                                # This is a comment
                                line1
                                line2
                                # another comment
                                line3
                                """);

        // Act
        List<String> lines = chatDMDir.getAllLines(file.getFileName());

        // Assert
        assertThat(lines).containsExactly("line1", "line2", "line3");
    }

    @Test
    void testGetAllLines_FileDoesNotExist() throws IOException {
        // Act
        List<String> lines = chatDMDir.getAllLines(Path.of("missing.txt"));

        // Assert
        assertThat(lines).isEmpty();
    }

    @Test
    void testGetAllLines_UpwardTraversal() {
        // Act & Assert
        assertThatThrownBy(() -> chatDMDir.getAllLines(Path.of("../evil.txt"))).isInstanceOf(
          IllegalArgumentException.class).hasMessageContaining("upward traversal");
    }

    @Test
    void testGetAllLines_DirectoryProvided() throws IOException {
        // Arrange
        Path subDir = Files.createDirectory(tempDir.resolve("subdir"));

        // Act
        List<String> lines = chatDMDir.getAllLines(subDir.getFileName());

        // Assert
        assertThat(lines).isEmpty();
    }

    @Test
    void testLoadBundleDir() throws IOException
    {
        // This gets a little confusing if you trace/dig into the code because of the handling
        // I put into the ChatDMDir to keep people from passing in absolute file paths.

        // create some test files
        String oracleOne = "Sad\nHappy\nMad\nBored";
        String oracleTwo = "Tea\nAle\nCoffee\nWater\nWine";
        String oracleThree = "Troll\nOgre\nHill Giant\nBronze Dragon";

        chatDMDir.writeFile(Path.of("oracles/named/", "npc_emotions.txt"), oracleOne);
        chatDMDir.writeFile(Path.of("oracles/named/", "drinks.txt"), oracleTwo);
        chatDMDir.writeFile(Path.of("oracles/named/", "monsters.txt"), oracleThree);

        Map<String, String> files = chatDMDir.loadBundleDir("oracles/named/");

        assertThat(files).withFailMessage("Did not create 3 top-level files.").hasSize(3);

        assertThat( files.get("npc_emotions.txt")).isEqualToIgnoringWhitespace(oracleOne);
        assertThat( files.get("drinks.txt")).isEqualToIgnoringWhitespace(oracleTwo);
        assertThat( files.get("monsters.txt")).isEqualToIgnoringWhitespace(oracleThree);

        // Test nested and that nested don't overlap

        chatDMDir.writeFile(Path.of("oracles/named/npcs/", "npc_emotions.txt"), oracleOne);
        chatDMDir.writeFile(Path.of("oracles/named/drinks/", "drinks.txt"), oracleTwo);
        chatDMDir.writeFile(Path.of("oracles/named/monsters/", "monsters.txt"), oracleThree);

        Map<String, String> filesNested = chatDMDir.loadBundleDir("oracles/named/");
        assertThat(filesNested).withFailMessage("Did not create 6 nested files.").hasSize(6);

        assertThat( filesNested.get("npcs/npc_emotions.txt")).isEqualToIgnoringWhitespace(oracleOne);
        assertThat( filesNested.get("drinks/drinks.txt")).isEqualToIgnoringWhitespace(oracleTwo);
        assertThat( filesNested.get("monsters/monsters.txt")).isEqualToIgnoringWhitespace(oracleThree);

    }


}