package cz.jpmad.htmlbodycleaner.parser;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HtmlWriterTest {

    private final HtmlWriter writer = new HtmlWriter();

    @Test
    void shouldWriteSimpleTag() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("p", Map.of(), false),
                new HtmlToken.Text("Hello"),
                new HtmlToken.CloseTag("p")
        );
        assertEquals("<p>Hello</p>", writer.write(tokens));
    }

    @Test
    void shouldWriteTagWithAttributes() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("a", Map.of("href", "http://example.com"), false),
                new HtmlToken.Text("click"),
                new HtmlToken.CloseTag("a")
        );
        assertEquals("<a href=\"http://example.com\">click</a>", writer.write(tokens));
    }

    @Test
    void shouldWriteSelfClosingTag() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("br", Map.of(), true)
        );
        assertEquals("<br/>", writer.write(tokens));
    }

    @Test
    void shouldWriteComment() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.Comment(" comment ")
        );
        assertEquals("<!-- comment -->", writer.write(tokens));
    }

    @Test
    void shouldWriteDoctype() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.Doctype("DOCTYPE html")
        );
        assertEquals("<!DOCTYPE html>", writer.write(tokens));
    }

    @Test
    void shouldWriteBooleanAttribute() {
        final List<HtmlToken> tokens = List.of(
                new HtmlToken.OpenTag("input", Map.of("disabled", ""), false)
        );
        assertEquals("<input disabled>", writer.write(tokens));
    }
}
