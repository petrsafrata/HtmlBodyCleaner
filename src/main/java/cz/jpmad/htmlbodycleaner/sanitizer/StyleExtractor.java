package cz.jpmad.htmlbodycleaner.sanitizer;

import cz.jpmad.htmlbodycleaner.parser.HtmlToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Extracts inline {@code style} attributes from HTML tokens, generates unique CSS class names,
 * and replaces inline styles with the generated class references.
 * <p>
 * Deduplicates identical style declarations so the same inline style maps to a single generated class.
 * Use {@link #extract(List)} to produce a token list with styles replaced, then
 * {@link #getGeneratedStyles()} or {@link #buildStyleBlock()} to obtain the resulting CSS.
 */
public final class StyleExtractor {

    private static final String CLASS_PREFIX = "sc-";
    private static final String STYLE_ATTR = "style";
    private static final String CLASS_ATTR = "class";

    /** Normalizes whitespace around colons: {@code color : red} → {@code color:red} */
    private static final Pattern COLON_SPACES = Pattern.compile("\\s*:\\s*");
    /** Normalizes whitespace around semicolons: {@code color:red ; font} → {@code color:red;font} */
    private static final Pattern SEMICOLON_SPACES = Pattern.compile("\\s*;\\s*");
    /** Collapses multiple whitespace characters into one */
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

    private final Map<String, String> styleToClass = new LinkedHashMap<>();
    private int classCounter = 0;

    /**
     * Normalizes a CSS style value for consistent deduplication.
     * <p>
     * Collapses multiple whitespace into one, removes spaces around {@code ':'} and {@code ';'},
     * and removes a trailing semicolon if present.
     *
     * @param raw the raw style string to normalize; must not be {@code null}
     * @return the normalized style string; never {@code null}
     * @throws NullPointerException if {@code raw} is {@code null}
     */
    private static String normalizeStyle(final String raw) {
        String s = raw.trim();
        s = MULTI_SPACE.matcher(s).replaceAll(" ");
        s = COLON_SPACES.matcher(s).replaceAll(":");
        s = SEMICOLON_SPACES.matcher(s).replaceAll(";");
        // Remove trailing semicolon
        if (s.endsWith(";")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * Processes the provided token list: removes {@code style} attributes, generates CSS classes,
     * and appends or merges generated class names into the {@code class} attribute.
     * <p>
     * Each token is preserved unless it is an {@link HtmlToken.OpenTag} containing a {@code style}
     * attribute, in which case a transformed tag is added to the result.
     *
     * @param tokens the token list to process; must not be {@code null}. Elements may be {@code null} and are preserved.
     * @return a new list of tokens with styles replaced by class references; never {@code null}
     * @throws NullPointerException if {@code tokens} is {@code null}
     */
    public List<HtmlToken> extract(final List<HtmlToken> tokens) {
        final List<HtmlToken> result = new ArrayList<>(tokens.size());

        for (final HtmlToken token : tokens) {
            if (token instanceof HtmlToken.OpenTag ot && ot.attributes().containsKey(STYLE_ATTR)) {
                result.add(replaceStyleWithClass(ot));
            } else {
                result.add(token);
            }
        }

        return result;
    }

    /**
     * Returns an unmodifiable view of the generated CSS rules mapping (style declaration → class name).
     *
     * @return an unmodifiable map of generated styles; never {@code null}. May be empty.
     */
    public Map<String, String> getGeneratedStyles() {
        return Collections.unmodifiableMap(styleToClass);
    }

    /**
     * Builds a {@code <style>} block string containing CSS rules for all generated classes.
     * <p>
     * Each entry is formatted as {@code .<className> { <style-declaration> }} on its own line.
     *
     * @return the {@code <style>} block string; empty string if no styles were generated; never {@code null}
     */
    public String buildStyleBlock() {
        if (styleToClass.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<style>\n");
        styleToClass.forEach((styleValue, className) ->
                sb.append(".").append(className).append(" { ").append(styleValue).append(" }\n"));
        sb.append("</style>");
        return sb.toString();
    }

    /**
     * Replaces the {@code style} attribute on the given open tag with a generated {@code class} attribute.
     * <p>
     * The style value is normalized and deduplicated: identical normalized styles reuse the same generated class.
     * If the tag already has a {@code class} attribute, the generated class is appended.
     *
     * @param tag the opening tag to transform; must not be {@code null}
     * @return a new {@link HtmlToken.OpenTag} instance with updated attributes; never {@code null}
     * @throws NullPointerException if {@code tag} is {@code null}
     */
    private HtmlToken.OpenTag replaceStyleWithClass(final HtmlToken.OpenTag tag) {
        final String rawValue = tag.attributes().get(STYLE_ATTR).trim();
        if (rawValue.isEmpty()) {
            final Map<String, String> newAttrs = new LinkedHashMap<>(tag.attributes());
            newAttrs.remove(STYLE_ATTR);
            return tag.withAttributes(newAttrs);
        }

        final String styleValue = normalizeStyle(rawValue);

        final String className = styleToClass.computeIfAbsent(styleValue, k -> {
            classCounter++;
            return CLASS_PREFIX + classCounter;
        });

        final Map<String, String> newAttrs = new LinkedHashMap<>(tag.attributes());
        newAttrs.remove(STYLE_ATTR);

        final String existingClass = newAttrs.get(CLASS_ATTR);
        if (existingClass != null && !existingClass.isBlank()) {
            newAttrs.put(CLASS_ATTR, existingClass + " " + className);
        } else {
            newAttrs.put(CLASS_ATTR, className);
        }

        return tag.withAttributes(newAttrs);
    }
}
