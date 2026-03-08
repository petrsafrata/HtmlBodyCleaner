package cz.jpmad.htmlbodycleaner;

import cz.jpmad.htmlbodycleaner.config.SanitizeConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlBodyCleanerTest {

    private final HtmlBodyCleaner cleaner = new HtmlBodyCleaner();

    @Test
    void shouldCleanFullHtmlDocument() {
        final String input = "<!DOCTYPE html><html><head><title>Test</title></head>"
                + "<body><p style=\"color:red\">Hello</p></body></html>";

        final String result = cleaner.clean(input);

        // Should contain generated style block in head
        assertTrue(result.contains("<style>"));
        assertTrue(result.contains(".sc-1 { color:red }"));
        // Should have class instead of style
        assertTrue(result.contains("class=\"sc-1\""));
        assertFalse(result.contains("style=\"color:red\""));
        // Should preserve structure
        assertTrue(result.contains("<!DOCTYPE html>"));
        assertTrue(result.contains("<title>Test</title>"));
        assertTrue(result.contains("</html>"));
    }

    @Test
    void shouldRemoveDisallowedTagsFromBody() {
        final SanitizeConfig config = SanitizeConfig.builder()
                .allowTags("html", "head", "body", "p")
                .selfClosingTags("br")
                .build();
        final HtmlBodyCleaner restrictiveCleaner = new HtmlBodyCleaner(config);

        final String input = "<html><head></head><body>"
                + "<p><font>Keep this text</font></p>"
                + "<script>alert('evil')</script>"
                + "</body></html>";

        final String result = restrictiveCleaner.clean(input);

        // <font> removed but text kept
        assertFalse(result.contains("<font>"));
        assertTrue(result.contains("Keep this text"));
        // <script> removed entirely including its content (security: raw content blocks are fully dropped)
        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("alert('evil')"));
    }

    @Test
    void shouldRemoveDisallowedSelfClosingTags() {
        final SanitizeConfig config = SanitizeConfig.builder()
                .allowTags("html", "head", "body", "p")
                .selfClosingTags("embed")
                .build();
        final HtmlBodyCleaner restrictiveCleaner = new HtmlBodyCleaner(config);

        final String input = "<html><head></head><body>"
                + "<p>Before</p><embed src=\"flash.swf\"/><p>After</p>"
                + "</body></html>";

        final String result = restrictiveCleaner.clean(input);

        assertFalse(result.contains("<embed"));
        assertTrue(result.contains("Before"));
        assertTrue(result.contains("After"));
    }

    @Test
    void shouldConvertInlineStylesToClasses() {
        final String input = "<html><head></head><body>"
                + "<p style=\"font-size:14px\">First</p>"
                + "<p style=\"font-size:14px\">Second</p>"
                + "<p style=\"color:blue\">Third</p>"
                + "</body></html>";

        final String result = cleaner.clean(input);

        // Deduplicated: first two paragraphs share the same class
        assertTrue(result.contains("<style>"));
        assertTrue(result.contains(".sc-1 { font-size:14px }"));
        assertTrue(result.contains(".sc-2 { color:blue }"));
        // Inline styles removed
        assertFalse(result.contains("style="));
    }

    @Test
    void shouldHandleNullInput() {
        assertNull(cleaner.clean(null));
    }

    @Test
    void shouldHandleBlankInput() {
        assertEquals("  ", cleaner.clean("  "));
    }

    @Test
    void shouldHandleNoBodyTag() {
        final String input = "<html><head><title>No body</title></head></html>";
        final String result = cleaner.clean(input);
        assertEquals(input, result);
    }

    @Test
    void shouldPreserveHeadSection() {
        final String input = "<html><head><title>Keep</title>"
                + "<meta charset=\"UTF-8\"></head>"
                + "<body><p>Content</p></body></html>";

        final String result = cleaner.clean(input);

        assertTrue(result.contains("<title>Keep</title>"));
        assertTrue(result.contains("<meta charset=\"UTF-8\">"));
    }

    @Test
    void shouldPreserveExistingClassWhenAddingGenerated() {
        final String input = "<html><head></head><body>"
                + "<p class=\"existing\" style=\"margin:0\">Text</p>"
                + "</body></html>";

        final String result = cleaner.clean(input);

        assertTrue(result.contains("class=\"existing sc-1\""));
        assertFalse(result.contains("style="));
    }

    @Test
    void shouldHandleNestedDisallowedTags() {
        final SanitizeConfig config = SanitizeConfig.builder()
                .allowTags("html", "head", "body", "p")
                .build();
        final HtmlBodyCleaner restrictiveCleaner = new HtmlBodyCleaner(config);

        final String input = "<html><head></head><body>"
                + "<div><span>Deep text</span></div>"
                + "</body></html>";

        final String result = restrictiveCleaner.clean(input);

        assertFalse(result.contains("<div>"));
        assertFalse(result.contains("<span>"));
        assertTrue(result.contains("Deep text"));
    }
}
