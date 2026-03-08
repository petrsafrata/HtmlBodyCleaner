package cz.jpmad.htmlbodycleaner.sanitizer;

import cz.jpmad.htmlbodycleaner.parser.HtmlToken;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StyleExtractorTest {

    @Test
    void shouldExtractInlineStyleAndGenerateClass() {
        final StyleExtractor extractor = new StyleExtractor();
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of("style", "color:red"), false),
                new HtmlToken.Text("Text"),
                new HtmlToken.CloseTag("p")
        );

        final List<HtmlToken> result = extractor.extract(tokens);
        final HtmlToken.OpenTag tag = (HtmlToken.OpenTag) result.get(0);

        assertFalse(tag.attributes().containsKey("style"));
        assertTrue(tag.attributes().containsKey("class"));
        assertEquals("sc-1", tag.attributes().get("class"));

        final String styleBlock = extractor.buildStyleBlock();
        assertTrue(styleBlock.contains(".sc-1 { color:red }"));
    }

    @Test
    void shouldDeduplicateIdenticalStyles() {
        final StyleExtractor extractor = new StyleExtractor();
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of("style", "color:red"), false),
                new HtmlToken.Text("First"),
                new HtmlToken.CloseTag("p"),
                new HtmlToken.OpenTag("p", Map.of("style", "color:red"), false),
                new HtmlToken.Text("Second"),
                new HtmlToken.CloseTag("p")
        );

        final List<HtmlToken> result = extractor.extract(tokens);
        final HtmlToken.OpenTag tag1 = (HtmlToken.OpenTag) result.get(0);
        final HtmlToken.OpenTag tag2 = (HtmlToken.OpenTag) result.get(3);

        assertEquals(tag1.attributes().get("class"), tag2.attributes().get("class"));
        assertEquals(1, extractor.getGeneratedStyles().size());
    }

    @Test
    void shouldPreserveExistingClassAttribute() {
        final StyleExtractor extractor = new StyleExtractor();
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of("class", "existing", "style", "color:blue"), false),
                new HtmlToken.Text("Text"),
                new HtmlToken.CloseTag("p")
        );

        final List<HtmlToken> result = extractor.extract(tokens);
        final HtmlToken.OpenTag tag = (HtmlToken.OpenTag) result.get(0);

        assertTrue(tag.attributes().get("class").startsWith("existing "));
        assertTrue(tag.attributes().get("class").contains("sc-1"));
    }

    @Test
    void shouldHandleEmptyStyleAttribute() {
        final StyleExtractor extractor = new StyleExtractor();
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of("style", ""), false),
                new HtmlToken.Text("Text"),
                new HtmlToken.CloseTag("p")
        );

        final List<HtmlToken> result = extractor.extract(tokens);
        final HtmlToken.OpenTag tag = (HtmlToken.OpenTag) result.get(0);

        assertFalse(tag.attributes().containsKey("style"));
        assertFalse(tag.attributes().containsKey("class"));
    }

    @Test
    void shouldReturnEmptyStyleBlockWhenNoStyles() {
        final StyleExtractor extractor = new StyleExtractor();
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of(), false),
                new HtmlToken.Text("Text"),
                new HtmlToken.CloseTag("p")
        );

        extractor.extract(tokens);
        assertEquals("", extractor.buildStyleBlock());
    }
}
