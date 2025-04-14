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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
//@TestPropertySource(properties = {"easychatdm.dir=${java.io.tmpdir}"})
class ChatDMDirTest {

    @TempDir
    static Path tempDir;

    @Autowired
    ChatDMDir chatDMDir;

    // make sure we're use the test's tempdir.
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
        assertThatThrownBy(() -> chatDMDir.getAllLines(Path.of("../evil.txt")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("upward traversal");
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
}