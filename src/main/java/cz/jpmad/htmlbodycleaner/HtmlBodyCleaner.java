package cz.jpmad.htmlbodycleaner;

import cz.jpmad.htmlbodycleaner.config.SanitizeConfig;
import cz.jpmad.htmlbodycleaner.parser.HtmlToken;
import cz.jpmad.htmlbodycleaner.parser.HtmlTokenizer;
import cz.jpmad.htmlbodycleaner.parser.HtmlWriter;
import cz.jpmad.htmlbodycleaner.sanitizer.BodyExtractor;
import cz.jpmad.htmlbodycleaner.sanitizer.StyleExtractor;
import cz.jpmad.htmlbodycleaner.sanitizer.TagSanitizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade for cleaning HTML documents: tokenizes input, extracts the document body,
 * sanitizes tags, extracts inline styles into generated CSS classes, injects the
 * style block into the head, and reassembles the final HTML output.
 * <p>
 * Usage: construct with a {@link SanitizeConfig} (or use the default constructor)
 * and call {@link #clean(String)}.
 */
public final class HtmlBodyCleaner {

    private final SanitizeConfig config;
    private final HtmlTokenizer tokenizer;
    private final HtmlWriter writer;

    /**
     * Creates a cleaner with the provided sanitization configuration.
     *
     * @param config sanitization configuration; must not be {@code null}
     * @throws NullPointerException if {@code config} is {@code null}
     */
    public HtmlBodyCleaner(final SanitizeConfig config) {
        this.config = config;
        this.tokenizer = new HtmlTokenizer();
        this.writer = new HtmlWriter();
    }

    /**
     * Creates a cleaner with the default sanitization configuration.
     */
    public HtmlBodyCleaner() {
        this(SanitizeConfig.defaults());
    }

    /**
     * Cleans the given HTML string according to the configured rules.
     * <p>
     * The method performs tokenization, body extraction, tag sanitization, inline-style extraction
     * (converting styles into generated CSS classes), injection of the resulting style block into the head
     * (or before the body if no head), and final reassembly of tokens into a string.
     *
     * @param html the raw HTML input; may be {@code null} or blank — such values are returned unchanged
     * @return the sanitized HTML output; returns {@code null} if {@code html} was {@code null}
     */
    public String clean(final String html) {
        if (html == null || html.isBlank()) {
            return html;
        }

        // 1. Tokenize
        final List<HtmlToken> tokens = tokenizer.tokenize(html);

        // 2. Split into pre-body, body, post-body
        final BodyExtractor bodyExtractor = new BodyExtractor(tokens);
        final List<HtmlToken> preBody = bodyExtractor.getPreBody();
        List<HtmlToken> bodyTokens = bodyExtractor.getBodyTokens();
        final List<HtmlToken> postBody = bodyExtractor.getPostBody();

        // If there's no body section, return as-is
        if (bodyTokens.isEmpty() && postBody.isEmpty()) {
            return html;
        }

        // 3. Sanitize tags in body
        final TagSanitizer tagSanitizer = new TagSanitizer(config);
        bodyTokens = tagSanitizer.sanitize(bodyTokens);

        // 4. Extract inline styles and generate CSS classes
        final StyleExtractor styleExtractor = new StyleExtractor();
        bodyTokens = styleExtractor.extract(bodyTokens);

        // 5. Inject <style> block into the head section
        final List<HtmlToken> finalPreBody = injectStyleBlock(preBody, styleExtractor);

        // 6. Reassemble
        final List<HtmlToken> allTokens = new ArrayList<>();
        allTokens.addAll(finalPreBody);
        allTokens.addAll(bodyTokens);
        allTokens.addAll(postBody);

        return writer.write(allTokens);
    }

    /**
     * Injects the generated CSS style block into the head section (before {@code </head>}) or,
     * if no head closing tag exists, inserts the block before the {@code <body>} opening tag.
     *
     * @param preBody list of tokens preceding the body; must not be {@code null}
     * @param styleExtractor provider of the generated style block; must not be {@code null}
     * @return a list of tokens with the style block injected; may be the original {@code preBody} or a newly created list; never {@code null}
     * @throws NullPointerException if {@code preBody} or {@code styleExtractor} is {@code null}
     */
    private List<HtmlToken> injectStyleBlock(final List<HtmlToken> preBody,
                                              final StyleExtractor styleExtractor) {
        final String styleBlock = styleExtractor.buildStyleBlock();
        if (styleBlock.isEmpty()) {
            return preBody;
        }

        final List<HtmlToken> result = new ArrayList<>(preBody.size() + 2);
        boolean injected = false;

        for (final HtmlToken token : preBody) {
            // Insert before </head>
            if (!injected && token instanceof HtmlToken.CloseTag ct && "head".equals(ct.name())) {
                result.add(new HtmlToken.Text("\n"));
                result.add(new HtmlToken.Text(styleBlock));
                result.add(new HtmlToken.Text("\n"));
                injected = true;
            }
            result.add(token);
        }

        // If no </head> found, insert before <body> tag (last token in preBody)
        if (!injected) {
            final List<HtmlToken> resultWithStyle = new ArrayList<>();
            for (final HtmlToken token : preBody) {
                if (token instanceof HtmlToken.OpenTag ot && "body".equals(ot.name())) {
                    resultWithStyle.add(new HtmlToken.Text("\n"));
                    resultWithStyle.add(new HtmlToken.Text(styleBlock));
                    resultWithStyle.add(new HtmlToken.Text("\n"));
                }
                resultWithStyle.add(token);
            }
            return resultWithStyle;
        }

        return result;
    }
}
