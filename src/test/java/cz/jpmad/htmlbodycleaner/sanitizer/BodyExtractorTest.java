package cz.jpmad.htmlbodycleaner.sanitizer;

import cz.jpmad.htmlbodycleaner.parser.HtmlToken;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BodyExtractorTest {

    @Test
    void shouldSplitDocumentIntoSections() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.Doctype("DOCTYPE html"),
                new HtmlToken.OpenTag("html", Map.of(), false),
                new HtmlToken.OpenTag("head", Map.of(), false),
                new HtmlToken.CloseTag("head"),
                new HtmlToken.OpenTag("body", Map.of(), false),
                new HtmlToken.OpenTag("p", Map.of(), false),
                new HtmlToken.Text("Hello"),
                new HtmlToken.CloseTag("p"),
                new HtmlToken.CloseTag("body"),
                new HtmlToken.CloseTag("html")
        );

        final BodyExtractor extractor = new BodyExtractor(tokens);

        // preBody: DOCTYPE, html, head, /head, body
        assertEquals(5, extractor.getPreBody().size());
        // body content: p, text, /p
        assertEquals(3, extractor.getBodyTokens().size());
        // postBody: /body, /html
        assertEquals(2, extractor.getPostBody().size());
    }

    @Test
    void shouldHandleDocumentWithoutBody() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of(), false),
                new HtmlToken.Text("No body"),
                new HtmlToken.CloseTag("p")
        );

        final BodyExtractor extractor = new BodyExtractor(tokens);

        assertEquals(3, extractor.getPreBody().size());
        assertTrue(extractor.getBodyTokens().isEmpty());
        assertTrue(extractor.getPostBody().isEmpty());
    }
}
