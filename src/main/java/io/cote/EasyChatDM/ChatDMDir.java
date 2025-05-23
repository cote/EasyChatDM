package io.cote.EasyChatDM;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    // TK make Resource Template out of this?

    private static final Logger logger = LoggerFactory.getLogger(ChatDMDir.class);

    @Value("${easychatdm.dir:${user.dir}/.easychatdm}")
    private String dirProperty;

    private Path easyChatDir;

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

    @PostConstruct
    private void init() throws IOException {
        easyChatDir = Path.of(dirProperty);
        // ensure it exists
        Files.createDirectories(easyChatDir);
        logger.debug("EasyChatDM dir is {}", easyChatDir);
    }

    Map<String, List<String>> loadBundleDir(String bundleName) {

        Path bundleDirFragment = Path.of(bundleName);
        throwIfInvalidFile(bundleDirFragment);

        Path bundleDir = easyChatDir.resolve(bundleDirFragment);
        logger.debug("bundle dir is now {}", bundleDir);
        if (containsUpwardTraversal(bundleDir)) {
            throw new IllegalArgumentException("Bundle name cannot contain upward traversal: " + bundleName);
        } else if (!Files.isDirectory(bundleDir)) {
            logger.debug("bundle dir {} does not exist or is not a directory", bundleDir);
            return Collections.emptyMap();
        }

        // All good!
        try (Stream<Path> paths = Files.walk(bundleDir)) {
            return paths.filter(Files::isRegularFile).filter(
              p -> acceptedFileFormat(p.getFileName().toString())).collect(
              Collectors.toMap(p -> p.getFileName().toString(), p -> {
                  try {
                      // Now we need to relative paths again for the validation
                      // check to avoid passing in a absolute path.
                      String relativePath = p.getFileName().toString();
                      return getAllLines(Path.of(bundleName, relativePath));
                  } catch (IOException e) {
                      logger.warn("Failed to read file {}", p, e);
                      return Collections.emptyList();
                  }
              }));
        } catch (IOException e) {
            logger.error("Returning empty listing. Failed to read from bundle directory {}", bundleDir, e);
            return Collections.emptyMap();
        }

    }

    /**
     * Returns the filename without its extension. It will only remove the <i>last</i> part of the string that starts
     * with a period.
     *
     * @param filename the full filename (e.g. "foo.txt" or "club.salad.txt)
     * @return the base name without extension (e.g. "foo" or "club.salad" )
     */
    private String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot > 0) ? filename.substring(0, dot) : filename;
    }

    private boolean acceptedFileFormat(String filename) {
        return (filename.endsWith(".txt") || filename.endsWith(".yaml") || filename.endsWith(
          ".yml") || filename.endsWith(".st"));
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

        // Will check for absolute filename and upwards traversal
        // for basic security checks.
        throwIfInvalidFile(fileName);

        // Now we can try the full path.
        Path fullPath = easyChatDir.resolve(fileName);

        if (Files.isDirectory(fullPath)) {
            // isDirectory checks for directory existence as well.
            return Collections.emptyList();
        } else if (!Files.exists(fullPath)) {
            logger.debug("File {} requested does not exist", fullPath);
            // finally, does the actual file exist?
            return Collections.emptyList();
        }
        logger.debug("Resolved file {}", fullPath);

        List<String> cleanedLines = Collections.emptyList();
        // Now we have a file, and one that exists
        try (Stream<String> s = Files.lines(fullPath)) {
            cleanedLines = s.map(String::trim).filter(line -> !line.startsWith("#")).toList();
        }

        return Collections.unmodifiableList(cleanedLines);
    }

    /**
     * Convenience method for {@link #readFile(Path)}
     */
    String readFile(String fileName) throws IOException {
        return readFile(Path.of(fileName));
    }

    /**
     * Reads the file from the dmDir. Will throw {@linke IllegalArgumentException} if the file is outside of the DM Dir.
     * If the filename ends in <code>.st</code>, the file is assumed to be <a
     * href="https://github.com/antlr/stringtemplate4/blob/master/doc/introduction.md">a stringtemplate4 file</a> and
     * processed as such.
     *
     * @param fileName the file to read
     * @param args     a map of arguments to pass to the ST file. Can be null.
     * @return the contents of the file as a {@link String}.
     */
    String readSTFile(Path fileName,
                      Map<String, Object> args) throws IOException {

        throwIfInvalidFile(fileName);
        // all good
        Path fullPath = easyChatDir.resolve(fileName);
        logger.debug("Reading file {}", fullPath);

        // check if String Template.
        String template = Files.readString(fullPath);
        ST st = new ST(template);
        // add in any args passed in to use in the template
        if (args != null) {
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                st.add(entry.getKey(), entry.getValue());
                logger.debug("Added {} to ST {}", entry.getKey(), entry.getValue());
            }
        }

        logger.debug("Read file {} as ST {}", fullPath, st);
        return st.render();
    }

    String readFile(Path filename) throws IOException {
        return readSTFile(filename, Map.of());
    }

    /**
     * Convenience method for {@link #writeFile(Path, String)}}.
     *
     * @param fileName the file to write the contents to
     * @param content  the contents to write.
     * @throws IOException
     */
    void writeFile(String fileName,
                   String content) throws IOException {
        writeFile(Path.of(fileName), content);
    }

    /**
     * Writes a file in the chatdmdir. <code>fileName</code> can include directories.</code>
     *
     * @param fileName
     * @param content
     * @throws IOException
     */
    void writeFile(Path fileName,
                   String content) throws IOException {

        // Will check for absolute filename and upwards traversal
        // for basic security checks.
        throwIfInvalidFile(fileName);

        // Now we can try the full path.
        Path fullPath = easyChatDir.resolve(fileName);

        // make sure the directories exist.
        Files.createDirectories(fullPath.getParent());

        Files.writeString(fullPath, content, StandardCharsets.UTF_8);
        logger.debug("Wrote file {}", fullPath);
    }

    /**
     * Returns the top-level DM directory.
     *
     * @return the
     */
    Path getChatDMDir() {
        return easyChatDir;
    }

    private void throwIfInvalidFile(Path fileName) {
        // Some security and validation:
        // (1) Keep requests in the DM dir, no looking outside the dir.
        // (2) We only want files, not directories.
        if (fileName.isAbsolute()) {
            throw new IllegalArgumentException(
              String.format("File name must be relative to chatdmdir", easyChatDir, fileName));
        } else if (containsUpwardTraversal(fileName)) {
            throw new IllegalArgumentException("File name cannot contain upward traversal: " + fileName);
        }

    }

}