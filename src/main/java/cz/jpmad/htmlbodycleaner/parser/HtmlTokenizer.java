package cz.jpmad.htmlbodycleaner.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML tokenizer based on regular expressions.
 * <p>
 * First captures raw-content blocks (script, style) as a whole using {@link #RAW_BLOCK_PATTERN},
 * then processes the remaining HTML using {@link #TOKEN_PATTERN}.
 */
public final class HtmlTokenizer {

    /**
     * Pattern to capture entire raw-content blocks (script, style) including their content.
     * Applied first at each position to prevent the content from being tokenized as text.
     */
    private static final Pattern RAW_BLOCK_PATTERN = Pattern.compile(
            "<(?<rawTagName>script|style)(?<rawAttrs>[^>]*?)>(?<rawContent>[\\s\\S]*?)</(?:script|style)\\s*>",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Main tokenization pattern for comments, DOCTYPE, close and open tags, and text.
     * Tag names allow dashes (custom elements, e.g. {@code custom-box}).
     */
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(?<comment><!--(?<commentBody>[\\s\\S]*?)-->)"
                    + "|(?<doctype><![^>]*>)"
                    + "|(?<closetag></\\s*(?<closeTagName>[a-zA-Z][a-zA-Z0-9-]*)\\s*>)"
                    + "|(?<opentag><(?<openTagName>[a-zA-Z][a-zA-Z0-9-]*)(?<attrs>[^>]*?)(?<selfClose>/?)>)"
                    + "|(?<text>[^<]+)",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Pattern to parse attributes inside an opening tag.
     * Supports double-quoted, single-quoted, unquoted values and boolean attributes.
     */
    private static final Pattern ATTR_PATTERN = Pattern.compile(
            "(?<attrName>[a-zA-Z][a-zA-Z0-9-]*)"
                    + "(?:\\s*=\\s*(?:\"(?<dqVal>[^\"]*)\"|'(?<sqVal>[^']*)'|(?<uqVal>[^\\s/>]+)))?"
    );

    /**
     * Tokenizes an HTML string into a list of {@link HtmlToken}.
     * <p>
     * Scans left-to-right position by position:
     * <ol>
     *   <li>At each position tries {@link #RAW_BLOCK_PATTERN} first (script/style).</li>
     *   <li>Otherwise tries {@link #TOKEN_PATTERN} (comment, DOCTYPE, tag, text).</li>
     *   <li>If nothing matches, advances by one character.</li>
     * </ol>
     */
    public List<HtmlToken> tokenize(final String html) {
        final List<HtmlToken> tokens = new ArrayList<>();
        int pos = 0;
        final int len = html.length();

        while (pos < len) {
            final Matcher rawMatcher = RAW_BLOCK_PATTERN.matcher(html);
            rawMatcher.region(pos, len);
            if (rawMatcher.lookingAt()) {
                tokens.add(new HtmlToken.RawBlock(
                        rawMatcher.group("rawTagName"),
                        parseAttributes(rawMatcher.group("rawAttrs")),
                        rawMatcher.group("rawContent")));
                pos = rawMatcher.end();
                continue;
            }

            final Matcher matcher = TOKEN_PATTERN.matcher(html);
            matcher.region(pos, len);
            if (matcher.lookingAt()) {
                if (matcher.group("comment") != null) {
                    tokens.add(new HtmlToken.Comment(matcher.group("commentBody")));
                } else if (matcher.group("doctype") != null) {
                    final String raw = matcher.group("doctype");
                    tokens.add(new HtmlToken.Doctype(raw.substring(2, raw.length() - 1).trim()));
                } else if (matcher.group("closetag") != null) {
                    tokens.add(new HtmlToken.CloseTag(matcher.group("closeTagName")));
                } else if (matcher.group("opentag") != null) {
                    final boolean selfClosing = "/".equals(matcher.group("selfClose"));
                    tokens.add(new HtmlToken.OpenTag(
                            matcher.group("openTagName"),
                            parseAttributes(matcher.group("attrs")),
                            selfClosing));
                } else if (matcher.group("text") != null) {
                    tokens.add(new HtmlToken.Text(matcher.group("text")));
                }
                pos = matcher.end();
            } else {
                pos++;
            }
        }

        return tokens;
    }

    /**
     * Parses the attribute string from an opening tag.
     *
     * @param rawAttrs raw attributes string (e.g. {@code ' href="url" disabled'})
     * @return ordered map attribute name (lower-cased) → value
     */
    private Map<String, String> parseAttributes(final String rawAttrs) {
        final Map<String, String> attributes = new LinkedHashMap<>();
        if (rawAttrs == null || rawAttrs.isBlank()) {
            return attributes;
        }
        final Matcher attrMatcher = ATTR_PATTERN.matcher(rawAttrs);
        while (attrMatcher.find()) {
            final String name = attrMatcher.group("attrName").toLowerCase();
            final String dq = attrMatcher.group("dqVal");
            final String sq = attrMatcher.group("sqVal");
            final String uq = attrMatcher.group("uqVal");
            if (dq != null) {
                attributes.put(name, dq);
            } else if (sq != null) {
                attributes.put(name, sq);
            } else if (uq != null) {
                attributes.put(name, uq);
            } else {
                attributes.put(name, "");
            }
        }
        return attributes;
    }
}
