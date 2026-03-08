package cz.jpmad.htmlbodycleaner.cli;

import cz.jpmad.htmlbodycleaner.HtmlBodyCleaner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLI entry point for the HTML Body Cleaner.
 * <p>
 * Usage: {@code java Main <input.html> <output.html>}
 */

public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private Main() {
        // Utility class
    }

    /**
     * CLI entry point for the HTML Body Cleaner.
     * <p>
     * Reads the input HTML file from {@code args[0]}, sanitizes it via {@code HtmlBodyCleaner},
     * and writes the result to {@code args[1]}.
     *
     * @param args command-line arguments; must contain at least two non-null elements:
     *             {@code args[0]} = input path, {@code args[1]} = output path; {@code args} itself must not be {@code null}
     * @throws NullPointerException if {@code args} is {@code null} or if {@code args[0]} / {@code args[1]} is {@code null}
     * @throws java.nio.file.InvalidPathException if {@code args[0]} or {@code args[1]} do not form valid file system paths
     * @throws SecurityException if file system access or {@code System.exit} is denied by the security manager
     * @throws RuntimeException for other unexpected unchecked errors occurring during processing
     * <p>
     * Side effects: reads and writes files on disk, writes logs via SLF4J, and may terminate the JVM with {@code System.exit}.
     * Nullability: {@code null} values for {@code args} or required elements are not allowed.
     */
    public static void main(final String[] args) {
        if (args.length < 2) {
            log.info("Usage: java cz.jpmad.htmlbodycleaner.cli.Main <input.html> <output.html>");
            System.exit(1);
            return;
        }

        final Path inputPath = Path.of(args[0]);
        final Path outputPath = Path.of(args[1]);

        if (!Files.exists(inputPath)) {
            log.error("Error: Input file does not exist: {}", inputPath);
            System.exit(1);
            return;
        }

        try {
            final String inputHtml = Files.readString(inputPath, StandardCharsets.UTF_8);

            final HtmlBodyCleaner cleaner = new HtmlBodyCleaner();
            final String outputHtml = cleaner.clean(inputHtml);

            Files.writeString(outputPath, outputHtml, StandardCharsets.UTF_8);

            log.info("Sanitization complete. Output written to: {}", outputPath);
        } catch (final IOException e) {
            log.error("Error processing file: {}", e.getMessage());
            System.exit(1);
        }
    }
}
