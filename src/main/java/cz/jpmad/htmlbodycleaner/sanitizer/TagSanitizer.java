package cz.jpmad.htmlbodycleaner.sanitizer;

import cz.jpmad.htmlbodycleaner.config.SanitizeConfig;
import cz.jpmad.htmlbodycleaner.parser.HtmlToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sanitizes HTML tokens according to a provided {@link SanitizeConfig}.
 * <p>
 * Allowed tags are preserved with only permitted attributes retained.
 * Disallowed self-closing tags are removed entirely.
 * Disallowed paired tags are stripped while preserving their inner content.
 * Raw blocks (e.g. script/style) are kept or dropped based on the configuration.
 */
public final class TagSanitizer {

    private final SanitizeConfig config;

    /**
     * Creates a TagSanitizer using the given configuration.
     *
     * @param config configuration that determines allowed tags and attributes; must not be {@code null}
     * @throws NullPointerException if {@code config} is {@code null}
     */
    public TagSanitizer(final SanitizeConfig config) {
        this.config = config;
    }

    /**
     * Sanitizes the provided list of HTML tokens according to the configured rules.
     * <p>
     * Allowed tags are emitted with only allowed attributes. Disallowed paired tags are omitted
     * but their inner tokens are preserved (nested disallowed paired tags are tracked).
     * Disallowed self-closing tags and disallowed raw blocks are omitted entirely.
     *
     * @param tokens input list of tokens to sanitize; must not be {@code null}. Individual elements may be {@code null} and are preserved.
     * @return a new mutable list containing the sanitized tokens; never {@code null}
     * @throws NullPointerException if {@code tokens} is {@code null}
     */
    public List<HtmlToken> sanitize(final List<HtmlToken> tokens) {
        final List<HtmlToken> result = new ArrayList<>();
        // Stack to track depth of disallowed paired tags being stripped
        int disallowedDepth = 0;
        final List<String> disallowedStack = new ArrayList<>();

        for (final HtmlToken token : tokens) {
            if (token instanceof HtmlToken.OpenTag ot) {
                if (config.isTagAllowed(ot.name())) {
                    result.add(filterAttributes(ot));
                } else if (!ot.selfClosing() && !config.isSelfClosing(ot.name())) {
                    // Disallowed paired tag -> skip the tag itself, keep inner content
                    disallowedStack.add(ot.name());
                    disallowedDepth++;
                }
                // Disallowed self-closing tag -> drop completely
            } else if (token instanceof HtmlToken.CloseTag ct) {
                if (disallowedDepth > 0 && !disallowedStack.isEmpty()
                        && disallowedStack.get(disallowedStack.size() - 1).equals(ct.name())) {
                    disallowedStack.remove(disallowedStack.size() - 1);
                    disallowedDepth--;
                } else if (config.isTagAllowed(ct.name())) {
                    result.add(ct);
                }
            } else if (token instanceof HtmlToken.RawBlock rb) {
                // Allowed raw blocks (e.g. <style>) pass through, disallowed (e.g. <script>) are dropped entirely
                if (config.isTagAllowed(rb.name()) && disallowedDepth == 0) {
                    result.add(rb);
                }
            } else if (token instanceof HtmlToken.Text) {
                result.add(token);
            } else if (token instanceof HtmlToken.Comment) {
                result.add(token);
            } else if (token instanceof HtmlToken.Doctype) {
                result.add(token);
            }
        }

        return result;
    }

    /**
     * Filters attributes of an opening tag, keeping only attributes allowed by the configuration.
     * <p>
     * If no attributes are removed the original tag instance is returned; otherwise a new {@link HtmlToken.OpenTag}
     * with the filtered attribute map is returned.
     *
     * @param tag the opening tag to filter; must not be {@code null}
     * @return the original {@link HtmlToken.OpenTag} if unchanged, or a new instance with filtered attributes; never {@code null}
     * @throws NullPointerException if {@code tag} is {@code null}
     */
    private HtmlToken.OpenTag filterAttributes(final HtmlToken.OpenTag tag) {
        final Map<String, String> filtered = new LinkedHashMap<>();
        tag.attributes().forEach((key, value) -> {
            if (config.isAttributeAllowed(tag.name(), key)) {
                filtered.put(key, value);
            }
        });
        if (filtered.size() == tag.attributes().size()) {
            return tag; // No attributes removed
        }
        return tag.withAttributes(filtered);
    }
}
