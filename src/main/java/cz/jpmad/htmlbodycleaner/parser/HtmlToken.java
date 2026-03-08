package cz.jpmad.htmlbodycleaner.parser;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a single token produced by the HTML tokenizer.
 */
public sealed interface HtmlToken {

    /**
     * An opening tag, e.g. {@code <div class="x">} or self-closing {@code <br/>}.
     */
    record OpenTag(String name, Map<String, String> attributes, boolean selfClosing) implements HtmlToken {
        public OpenTag {
            name = name.toLowerCase();
            attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        }

        /**
         * Returns a copy of this tag with the given attributes.
         */
        public OpenTag withAttributes(final Map<String, String> newAttributes) {
            return new OpenTag(name, newAttributes, selfClosing);
        }
    }

    /**
     * A closing tag, e.g. {@code </div>}.
     */
    record CloseTag(String name) implements HtmlToken {
        public CloseTag {
            name = name.toLowerCase();
        }
    }

    /**
     * Raw text content between tags.
     */
    record Text(String content) implements HtmlToken {
    }

    /**
     * An HTML comment, e.g. {@code <!-- comment -->}.
     */
    record Comment(String content) implements HtmlToken {
    }

    /**
     * A DOCTYPE declaration, e.g. {@code <!DOCTYPE html>}.
     */
    record Doctype(String content) implements HtmlToken {
    }

    /**
     * A raw-content block tag such as {@code <script>} or {@code <style>},
     * where the inner content is stored verbatim (not further tokenized).
     *
     * @param name       tag name in lower-case (e.g. {@code "script"}, {@code "style"})
     * @param attributes attributes of the opening tag
     * @param rawContent the raw text content between the opening and closing tag
     */
    record RawBlock(String name, Map<String, String> attributes, String rawContent) implements HtmlToken {
        public RawBlock {
            name = name.toLowerCase();
            attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        }
    }
}
