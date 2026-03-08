package cz.jpmad.htmlbodycleaner.parser;

import java.util.List;


public final class HtmlWriter {

    /**
     * Serializes a list of {@link HtmlToken} instances into an HTML string.
     * <p>
     * Preserves token order and reconstructs tokens using their canonical forms:
     * doctype as "<!content>", comments as "<!--content-->", text verbatim,
     * open/close tags and raw blocks with attributes (boolean attributes rendered without value,
     * attribute values quoted with double quotes).
     *
     * @param tokens list of tokens to serialize; must not be {@code null}. Null elements within the list are ignored.
     * @return the resulting HTML string; never {@code null}
     * @throws NullPointerException if {@code tokens} is {@code null}
     */
    public String write(final List<HtmlToken> tokens) {
        final StringBuilder sb = new StringBuilder();
        for (final HtmlToken token : tokens) {
            if (token instanceof HtmlToken.Doctype d) {
                sb.append("<!").append(d.content()).append(">");
            } else if (token instanceof HtmlToken.Comment c) {
                sb.append("<!--").append(c.content()).append("-->");
            } else if (token instanceof HtmlToken.Text t) {
                sb.append(t.content());
            } else if (token instanceof HtmlToken.CloseTag ct) {
                sb.append("</").append(ct.name()).append(">");
            } else if (token instanceof HtmlToken.RawBlock rb) {
                sb.append("<").append(rb.name());
                rb.attributes().forEach((key, value) -> {
                    if (value.isEmpty()) {
                        sb.append(" ").append(key);
                    } else {
                        sb.append(" ").append(key).append("=\"").append(value).append("\"");
                    }
                });
                sb.append(">").append(rb.rawContent()).append("</").append(rb.name()).append(">");
            } else if (token instanceof HtmlToken.OpenTag ot) {
                sb.append("<").append(ot.name());
                ot.attributes().forEach((key, value) -> {
                    if (value.isEmpty()) {
                        sb.append(" ").append(key);
                    } else {
                        sb.append(" ").append(key).append("=\"").append(value).append("\"");
                    }
                });
                if (ot.selfClosing()) {
                    sb.append("/>");
                } else {
                    sb.append(">");
                }
            }
        }
        return sb.toString();
    }
}
