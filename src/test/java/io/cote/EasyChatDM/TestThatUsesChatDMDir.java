package io.cote.EasyChatDM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Does all of the Spring and test setup stuff to create a temporary
 * chatdmdir. Test sub-classes can then use {@link #chatDMDir}
 */
public abstract class TestThatUsesChatDMDir {

    @Autowired
    private ChatDMDir chatDMDir;

    protected ChatDMDir chatDMDir() { return chatDMDir; }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        registry.add("easychatdm.dir", () -> "target/test/easychatdm-" + timestamp);
    }
}
