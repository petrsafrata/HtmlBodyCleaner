package cz.jpmad.htmlbodycleaner.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HtmlTokenizerTest {

    private final HtmlTokenizer tokenizer = new HtmlTokenizer();

    @Test
    void shouldTokenizeSimpleTag() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<p>Hello</p>");
        assertEquals(3, tokens.size());
        assertInstanceOf(HtmlToken.OpenTag.class, tokens.get(0));
        assertEquals("p", ((HtmlToken.OpenTag) tokens.get(0)).name());
        assertInstanceOf(HtmlToken.Text.class, tokens.get(1));
        assertEquals("Hello", ((HtmlToken.Text) tokens.get(1)).content());
        assertInstanceOf(HtmlToken.CloseTag.class, tokens.get(2));
        assertEquals("p", ((HtmlToken.CloseTag) tokens.get(2)).name());
    }

    @Test
    void shouldTokenizeTagWithAttributes() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<a href=\"http://example.com\" title='Link'>click</a>");
        final HtmlToken.OpenTag openTag = (HtmlToken.OpenTag) tokens.get(0);
        assertEquals("a", openTag.name());
        assertEquals("http://example.com", openTag.attributes().get("href"));
        assertEquals("Link", openTag.attributes().get("title"));
        assertFalse(openTag.selfClosing());
    }

    @Test
    void shouldTokenizeSelfClosingTag() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<br/>");
        assertEquals(1, tokens.size());
        final HtmlToken.OpenTag tag = (HtmlToken.OpenTag) tokens.get(0);
        assertEquals("br", tag.name());
        assertTrue(tag.selfClosing());
    }

    @Test
    void shouldTokenizeComment() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<!-- this is a comment -->");
        assertEquals(1, tokens.size());
        assertInstanceOf(HtmlToken.Comment.class, tokens.get(0));
        assertEquals(" this is a comment ", ((HtmlToken.Comment) tokens.get(0)).content());
    }

    @Test
    void shouldTokenizeDoctype() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<!DOCTYPE html>");
        assertEquals(1, tokens.size());
        assertInstanceOf(HtmlToken.Doctype.class, tokens.get(0));
    }

    @Test
    void shouldTokenizeStyleAttribute() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<p style=\"color:red\">Text</p>");
        final HtmlToken.OpenTag tag = (HtmlToken.OpenTag) tokens.get(0);
        assertEquals("color:red", tag.attributes().get("style"));
    }

    @Test
    void shouldTokenizeBooleanAttribute() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<input disabled>");
        final HtmlToken.OpenTag tag = (HtmlToken.OpenTag) tokens.get(0);
        assertTrue(tag.attributes().containsKey("disabled"));
        assertEquals("", tag.attributes().get("disabled"));
    }

    @Test
    void shouldHandleEmptyInput() {
        final List<HtmlToken> tokens = tokenizer.tokenize("");
        assertTrue(tokens.isEmpty());
    }

    @Test
    void shouldTokenizeFullHtmlDocument() {
        final String html = "<!DOCTYPE html><html><head><title>Test</title></head><body><p>Hello</p></body></html>";
        final List<HtmlToken> tokens = tokenizer.tokenize(html);
        assertFalse(tokens.isEmpty());
        assertTrue(tokens.size() >= 10);
    }

    @Test
    void shouldTokenizeScriptAsRawBlock() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<script>alert('x');</script>");
        assertEquals(1, tokens.size());
        assertInstanceOf(HtmlToken.RawBlock.class, tokens.get(0));
        final HtmlToken.RawBlock block = (HtmlToken.RawBlock) tokens.get(0);
        assertEquals("script", block.name());
        assertEquals("alert('x');", block.rawContent());
    }

    @Test
    void shouldTokenizeStyleBlockAsRawBlock() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<style>.foo { color:red }</style>");
        assertEquals(1, tokens.size());
        assertInstanceOf(HtmlToken.RawBlock.class, tokens.get(0));
        assertEquals("style", ((HtmlToken.RawBlock) tokens.get(0)).name());
    }

    @Test
    void shouldTokenizeCustomElementWithHyphen() {
        final List<HtmlToken> tokens = tokenizer.tokenize("<custom-box data-x=\"1\">text</custom-box>");
        assertEquals(3, tokens.size());
        assertInstanceOf(HtmlToken.OpenTag.class, tokens.get(0));
        assertEquals("custom-box", ((HtmlToken.OpenTag) tokens.get(0)).name());
        assertInstanceOf(HtmlToken.CloseTag.class, tokens.get(2));
        assertEquals("custom-box", ((HtmlToken.CloseTag) tokens.get(2)).name());
    }
}
