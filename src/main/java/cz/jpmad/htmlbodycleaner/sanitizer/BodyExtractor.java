package cz.jpmad.htmlbodycleaner.sanitizer;

import cz.jpmad.htmlbodycleaner.parser.HtmlToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits a token stream into three sections: before body, body content, and after body.
 * This allows the sanitizer to operate only on the body section while preserving the rest.
 */
public final class BodyExtractor {

    private final List<HtmlToken> preBody;
    private final List<HtmlToken> bodyTokens;
    private final List<HtmlToken> postBody;

    /**
     * Creates a new BodyExtractor and immediately extracts sections from the given token stream.
     *
     * @param tokens list of tokens to split; must not be {@code null}. Elements may be {@code null} and are preserved.
     * @throws NullPointerException if {@code tokens} is {@code null}
     */
    public BodyExtractor(final List<HtmlToken> tokens) {
        this.preBody = new ArrayList<>();
        this.bodyTokens = new ArrayList<>();
        this.postBody = new ArrayList<>();
        extract(tokens);
    }

    /**
     * Scans the provided token stream and fills internal lists: preBody, bodyTokens, and postBody.
     * <p>
     * The method advances through tokens in order, switching sections when an opening or closing {@code body} tag is encountered.
     *
     * @param tokens list to scan; must not be {@code null}. Elements may be {@code null}.
     * @throws NullPointerException if {@code tokens} is {@code null}
     */
    private void extract(final List<HtmlToken> tokens) {
        enum Section { PRE_BODY, BODY, POST_BODY }

        Section section = Section.PRE_BODY;

        for (final HtmlToken token : tokens) {
            switch (section) {
                case PRE_BODY -> {
                    if (isOpenBodyTag(token)) {
                        // The <body> tag itself goes into preBody (keep it as structural)
                        preBody.add(token);
                        section = Section.BODY;
                    } else {
                        preBody.add(token);
                    }
                }
                case BODY -> {
                    if (isCloseBodyTag(token)) {
                        postBody.add(token);
                        section = Section.POST_BODY;
                    } else {
                        bodyTokens.add(token);
                    }
                }
                case POST_BODY -> postBody.add(token);
            }
        }
    }

    /**
     * Returns {@code true} if the given token is an opening HTML {@code body} tag.
     * <p>
     * The check is exact on the tag name (compares to {@code "body"}).
     *
     * @param token token to test; may be {@code null}
     * @return {@code true} when {@code token} is an {@link HtmlToken.OpenTag} whose {@code name()} equals {@code "body"}; otherwise {@code false}
     */
    private static boolean isOpenBodyTag(final HtmlToken token) {
        return token instanceof HtmlToken.OpenTag ot && "body".equals(ot.name());
    }

    /**
     * Returns {@code true} if the given token is a closing HTML {@code body} tag.
     * <p>
     * The check is exact on the tag name (compares to {@code "body"}).
     *
     * @param token token to test; may be {@code null}
     * @return {@code true} when {@code token} is an {@link HtmlToken.CloseTag} whose {@code name()} equals {@code "body"}; otherwise {@code false}
     */
    private static boolean isCloseBodyTag(final HtmlToken token) {
        return token instanceof HtmlToken.CloseTag ct && "body".equals(ct.name());
    }

    /**
     * Returns the tokens found before the document's body (the opening {@code <body>} tag itself is included here).
     *
     * @return mutable list of pre-body tokens; never {@code null}
     */
    public List<HtmlToken> getPreBody() {
        return preBody;
    }

    /**
     * Returns the tokens that belong to the document body (between the opening and closing {@code <body>} tags).
     *
     * @return mutable list of body tokens; never {@code null}
     */
    public List<HtmlToken> getBodyTokens() {
        return bodyTokens;
    }

    /**
     * Returns the tokens found after the document's closing {@code </body>} tag.
     *
     * @return mutable list of post-body tokens; never {@code null}
     */
    public List<HtmlToken> getPostBody() {
        return postBody;
    }
}
