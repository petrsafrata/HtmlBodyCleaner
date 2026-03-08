package cz.jpmad.htmlbodycleaner.sanitizer;

import cz.jpmad.htmlbodycleaner.config.SanitizeConfig;
import cz.jpmad.htmlbodycleaner.parser.HtmlToken;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TagSanitizerTest {

    private final SanitizeConfig config = SanitizeConfig.defaults();
    private final TagSanitizer sanitizer = new TagSanitizer(config);

    @Test
    void shouldKeepAllowedTag() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of("class", "test"), false),
                new HtmlToken.Text("Hello"),
                new HtmlToken.CloseTag("p")
        );
        final List<HtmlToken> result = sanitizer.sanitize(tokens);
        assertEquals(3, result.size());
        assertInstanceOf(HtmlToken.OpenTag.class, result.get(0));
        assertEquals("p", ((HtmlToken.OpenTag) result.get(0)).name());
    }

    @Test
    void shouldRemoveDisallowedSelfClosingTag() {
        final SanitizeConfig restrictive = SanitizeConfig.builder()
                .allowTags("p")
                .selfClosingTags("br", "custom")
                .build();
        final TagSanitizer san = new TagSanitizer(restrictive);

        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of(), false),
                new HtmlToken.OpenTag("custom", Map.of(), true),
                new HtmlToken.Text("Hello"),
                new HtmlToken.CloseTag("p")
        );
        final List<HtmlToken> result = san.sanitize(tokens);
        assertEquals(3, result.size());
        // custom tag should be removed
        assertInstanceOf(HtmlToken.OpenTag.class, result.get(0));
        assertInstanceOf(HtmlToken.Text.class, result.get(1));
        assertInstanceOf(HtmlToken.CloseTag.class, result.get(2));
    }

    @Test
    void shouldRemoveDisallowedPairedTagButKeepContent() {
        final SanitizeConfig restrictive = SanitizeConfig.builder()
                .allowTags("p")
                .build();
        final TagSanitizer san = new TagSanitizer(restrictive);

        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of(), false),
                new HtmlToken.OpenTag("font", Map.of("color", "red"), false),
                new HtmlToken.Text("Colored text"),
                new HtmlToken.CloseTag("font"),
                new HtmlToken.CloseTag("p")
        );
        final List<HtmlToken> result = san.sanitize(tokens);
        assertEquals(3, result.size());
        assertEquals("p", ((HtmlToken.OpenTag) result.get(0)).name());
        assertEquals("Colored text", ((HtmlToken.Text) result.get(1)).content());
        assertEquals("p", ((HtmlToken.CloseTag) result.get(2)).name());
    }

    @Test
    void shouldPreserveStyleAttributeForStyleExtractor() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of("style", "color:red", "class", "test"), false),
                new HtmlToken.Text("Text"),
                new HtmlToken.CloseTag("p")
        );
        final List<HtmlToken> result = sanitizer.sanitize(tokens);
        final HtmlToken.OpenTag tag = (HtmlToken.OpenTag) result.get(0);
        // style attribute is preserved by TagSanitizer; StyleExtractor handles its conversion to CSS classes
        assertTrue(tag.attributes().containsKey("style"));
        assertTrue(tag.attributes().containsKey("class"));
    }

    @Test
    void shouldRemoveDisallowedAttributes() {
        final SanitizeConfig restrictive = SanitizeConfig.builder()
                .allowTags("div")
                .allowAttributes("div", "class")
                .build();
        final TagSanitizer san = new TagSanitizer(restrictive);

        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("div", Map.of("class", "ok", "onclick", "evil()"), false),
                new HtmlToken.CloseTag("div")
        );
        final List<HtmlToken> result = san.sanitize(tokens);
        final HtmlToken.OpenTag tag = (HtmlToken.OpenTag) result.get(0);
        assertTrue(tag.attributes().containsKey("class"));
        assertFalse(tag.attributes().containsKey("onclick"));
    }
}
