package io.cote.EasyChatDM;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages file access for the EasyChatDM.
 */
@Service
public class ChatDMDir {

    private static final Logger logger = LoggerFactory.getLogger(ChatDMDir.class);

    @Value("${easychatdm.dir:${user.dir}/.easychatdm}")
    private String dirProperty;

    private Path easyChatDir;

    @PostConstruct
    private void init() throws IOException {
        // TK could do some checking and create a dir if it
        // doesn't exist. At least log it.
        easyChatDir = Path.of(dirProperty);
        // ensure it exists
        Files.createDirectories(easyChatDir);
        logger.debug("EasyChatDM dir is {}", easyChatDir);
    }

    Map<String,List<String>> loadBundleDir(String bundleName)
    {
        // We only want the name of a subdirectory to look at,
        // not an absolute path, going up a directory, or a file.
        // If the directory does not exist (or is actually a fle),
        // we just return an empty map
        Path bundleDirFragment = Path.of(bundleName);
        if (bundleDirFragment.isAbsolute()) {
            throw new IllegalArgumentException("Bundle name must be relative to chatdmdir");
        }

        Path bundleDir = easyChatDir.resolve(bundleDirFragment);
        logger.debug("bundle dir is now {}", bundleDir);
        if (containsUpwardTraversal(bundleDir)) {
            throw new IllegalArgumentException("Bundle name cannot contain upward traversal: " + bundleName);
        }
        else if (!Files.isDirectory(bundleDir)) {
            logger.debug("bundle dir {} does not exist or is not a directory", bundleDir);
            return Collections.emptyMap();
        }

        // All good!

        try (Stream<Path> paths = Files.walk(bundleDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".txt"))
                    .collect(Collectors.toMap(
                            p -> p.getFileName().toString(),
                            p -> {
                                try {
                                    return getAllLines(bundleDir.relativize(p));
                                } catch (IOException e) {
                                    logger.warn("Failed to read file {}", p, e);
                                    return Collections.emptyList();
                                }
                            }
                    ));
        } catch (IOException e) {
            logger.error("Failed to read from bundle directory {}", bundleDir, e);
            return Collections.emptyMap();
        }

    }

    /**
     * Wrapper for {@link #getAllLines(Path)} that creates a path out of fileName.
     */
    List<String> getAllLines(String fileName) throws IOException {
        return getAllLines(Path.of(fileName));
    }

    /**
     * If the passed in file is actually a file, reads each line into a List. Each line is trimmed and if a line starts
     * with a # that line is treated as a comment and excluded from the list. If the file is a directory or does not
     * exist, an empty list will be returned.
     *
     * @param fileName the relative path to the file to load. The file must be in the chatdmdir.
     * @return an unmodifiable List<String> of the lines in the file, in order per the above.
     * @throws IOException if an error occurs.
     */
    List<String> getAllLines(Path fileName) throws IOException {

        logger.debug("Attempting to loading file {}", fileName);

        // Some security and validation:
        // (1) Keep requests in the DM dir, no looking outside the dir.
        // (2) We only want files, not directories.
        // (3) And we want them to actually exist.
        if (fileName.isAbsolute()) {
            throw new IllegalArgumentException(String.format("File name must be relative to chatdmdir", easyChatDir, fileName));
        }

        // Now we can try the full path.

        Path fullPath = easyChatDir.resolve(fileName);

        if (containsUpwardTraversal(fullPath)) {
            throw new IllegalArgumentException("File name cannot contain upward traversal: " + fileName);
        } else if (Files.isDirectory(fullPath)) {
            // isDirectory checks for directory existence as well.
            return Collections.emptyList();
        } else if (!Files.exists(fullPath)) {
            // finally, does the actual file exist?
            return Collections.emptyList();
        }
        logger.debug("Resolved file {}", fullPath);

        List<String> cleanedLines = Collections.emptyList();
        // Now we have a file, and one that exists
        try (Stream<String> s = Files.lines(fullPath)) {
            cleanedLines = s.map(String::trim).
                    filter(line -> !line.startsWith("#")).
                    toList();
        }

        return Collections.unmodifiableList(cleanedLines);
    }

    /**
     * Checks if a path contains any directory traversal elements.
     *
     * @param path The path to check
     * @return true if the path contains upward traversal elements
     */
    private static boolean containsUpwardTraversal(Path path) {
        // Generated by Claude
        // Iterate through each segment of the path
        for (Path segment : path) {
            if (segment.toString().equals("..")) {
                return true;
            }
        }
        return false;
    }

}