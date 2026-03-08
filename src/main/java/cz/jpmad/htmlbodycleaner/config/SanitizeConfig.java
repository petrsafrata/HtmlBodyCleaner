package cz.jpmad.htmlbodycleaner.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Immutable configuration defining which HTML tags and attributes
 * are allowed during sanitization.
 */
public final class SanitizeConfig {

    private final Set<String> allowedTags;
    private final Map<String, Set<String>> allowedAttributes;
    private final Set<String> selfClosingTags;

    /**
     * Creates an immutable SanitizeConfig from the provided Builder.
     * <p>
     * Performs defensive copies and normalization:
     * - clones and wraps allowed tags and self-closing tags as unmodifiable sets,
     * - clones each attribute set and stores them in a map with lower-cased tag keys,
     *   wrapping the map and sets as unmodifiable to ensure immutability.
     *
     * @param builder source Builder; must not be {@code null}. Its collections and their keys/values must not contain {@code null}.
     * @throws NullPointerException if {@code builder} is {@code null} or if any required builder collection, key, or attribute set is {@code null}.
     */
    private SanitizeConfig(final Builder builder) {
        this.allowedTags = Collections.unmodifiableSet(new HashSet<>(builder.allowedTags));
        final Map<String, Set<String>> attrCopy = new HashMap<>();
        builder.allowedAttributes.forEach((tag, attrs) ->
                attrCopy.put(tag.toLowerCase(), Collections.unmodifiableSet(new HashSet<>(attrs))));
        this.allowedAttributes = Collections.unmodifiableMap(attrCopy);
        this.selfClosingTags = Collections.unmodifiableSet(new HashSet<>(builder.selfClosingTags));
    }

    /**
     * Checks whether the given HTML tag is allowed by this configuration.
     * <p>
     * The check is case-insensitive: the provided tag is converted to lower-case
     * and matched against the normalized allowed tag set.
     *
     * @param tagName tag name to check; must not be {@code null}
     * @return {@code true} if the tag is allowed, {@code false} otherwise
     * @throws NullPointerException if {@code tagName} is {@code null}
     */
    public boolean isTagAllowed(final String tagName) {
        return allowedTags.contains(tagName.toLowerCase());
    }

    /**
     * Checks whether the given attribute is allowed for the specified HTML tag.
     * <p>
     * The check is case-insensitive: both tag and attribute are converted to lower-case.
     * The {@code "style"} attribute is always allowed to pass through.
     * If no attribute set is defined for the tag, the attribute is allowed only if the tag itself is allowed.
     *
     * @param tagName HTML tag name to check; must not be {@code null}
     * @param attributeName attribute name to check; must not be {@code null}
     * @return {@code true} if the attribute is allowed for the tag, {@code false} otherwise
     * @throws NullPointerException if {@code tagName} or {@code attributeName} is {@code null} (method calls {@code toLowerCase()})
     */
    public boolean isAttributeAllowed(final String tagName, final String attributeName) {
        final String tag = tagName.toLowerCase();
        final String attr = attributeName.toLowerCase();
        // Allow style attribute to pass through TagSanitizer
        // StyleExtractor will handle its conversion to CSS classes later
        if ("style".equals(attr)) {
            return true;
        }
        final Set<String> attrs = allowedAttributes.get(tag);
        if (attrs == null) {
            // If no specific attribute set defined, allow all
            return allowedTags.contains(tag);
        }
        return attrs.contains(attr);
    }

    /**
     * Checks whether the given HTML tag is configured as self-closing.
     * <p>
     * The check is case-insensitive: {@code tagName} is converted to lower-case.
     *
     * @param tagName tag name to check; must not be {@code null}
     * @return {@code true} if the tag is marked as self-closing, {@code false} otherwise
     * @throws NullPointerException if {@code tagName} is {@code null} (calls {@code toLowerCase()})
     */
    public boolean isSelfClosing(final String tagName) {
        return selfClosingTags.contains(tagName.toLowerCase());
    }

    /**
     * Returns the set of allowed HTML tag names.
     * <p>
     * The returned set is unmodifiable and contains lower-cased tag names.
     *
     * @return unmodifiable set of allowed tag names; never {@code null}
     */
    public Set<String> getAllowedTags() {
        return allowedTags;
    }

    /**
     * Returns the mapping of tag names to allowed attribute names.
     * <p>
     * Keys are lower-cased tag names; each value is an unmodifiable set of lower-cased attributes.
     *
     * @return unmodifiable map from tag name to allowed attribute sets; never {@code null}
     */
    public Map<String, Set<String>> getAllowedAttributes() {
        return allowedAttributes;
    }

    /**
     * Returns a default, fully configured SanitizeConfig with common HTML tags and attributes.
     *
     * @return immutable default SanitizeConfig; never {@code null}
     */
    public static SanitizeConfig defaults() {
        return new Builder()
                .allowTags("html", "head", "title", "meta", "link", "style", "body",
                        "div", "span", "p", "a", "b", "i", "u", "em", "strong",
                        "h1", "h2", "h3", "h4", "h5", "h6",
                        "ul", "ol", "li", "dl", "dt", "dd",
                        "table", "thead", "tbody", "tfoot", "tr", "th", "td",
                        "br", "hr", "img",
                        "blockquote", "pre", "code",
                        "form", "input", "select", "option", "textarea", "button", "label",
                        "header", "footer", "nav", "main", "section", "article", "aside",
                        "figure", "figcaption", "details", "summary", "mark", "small", "sub", "sup")
                .allowAttributes("a", "href", "title", "target", "rel", "class", "id")
                .allowAttributes("img", "src", "alt", "width", "height", "class", "id")
                .allowAttributes("div", "class", "id")
                .allowAttributes("span", "class", "id")
                .allowAttributes("p", "class", "id")
                .allowAttributes("table", "class", "id")
                .allowAttributes("td", "colspan", "rowspan", "class", "id")
                .allowAttributes("th", "colspan", "rowspan", "class", "id")
                .allowAttributes("input", "type", "name", "value", "placeholder", "class", "id")
                .allowAttributes("form", "action", "method", "class", "id")
                .allowAttributes("button", "type", "class", "id")
                .allowAttributes("label", "for", "class", "id")
                .allowAttributes("select", "name", "class", "id")
                .allowAttributes("option", "value", "selected")
                .allowAttributes("textarea", "name", "rows", "cols", "placeholder", "class", "id")
                .allowAttributes("meta", "charset", "name", "content")
                .allowAttributes("link", "rel", "href", "type")
                .allowAttributes("ol", "class", "id", "type")
                .allowAttributes("ul", "class", "id")
                .allowAttributes("li", "class", "id")
                .allowAttributes("h1", "class", "id")
                .allowAttributes("h2", "class", "id")
                .allowAttributes("h3", "class", "id")
                .allowAttributes("h4", "class", "id")
                .allowAttributes("h5", "class", "id")
                .allowAttributes("h6", "class", "id")
                .allowAttributes("blockquote", "class", "id", "cite")
                .allowAttributes("pre", "class", "id")
                .allowAttributes("code", "class", "id")
                .selfClosingTags("br", "hr", "img", "input", "meta", "link")
                .build();
    }

    /**
     * Creates a new Builder instance for constructing a SanitizeConfig.
     *
     * @return new Builder; never {@code null}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Set<String> allowedTags = new HashSet<>();
        private final Map<String, Set<String>> allowedAttributes = new HashMap<>();
        private final Set<String> selfClosingTags = new HashSet<>();

        /**
         * Adds a single allowed tag to the builder.
         * <p>
         * The tag is normalized to lower-case before storage.
         *
         * @param tag tag name to allow; must not be {@code null}
         * @return this Builder for chaining
         * @throws NullPointerException if {@code tag} is {@code null} (method calls {@code toLowerCase()})
         */
        public Builder allowTag(final String tag) {
            allowedTags.add(tag.toLowerCase());
            return this;
        }

        /**
         * Adds multiple allowed tags to the builder.
         * <p>
         * Each provided tag is normalized to lower-case before storage.
         *
         * @param tags varargs of tag names to allow; must not be {@code null} and must not contain {@code null} elements
         * @return this Builder for chaining
         * @throws NullPointerException if {@code tags} is {@code null} or any element is {@code null}
         */
        public Builder allowTags(final String... tags) {
            for (final String tag : tags) {
                allowedTags.add(tag.toLowerCase());
            }
            return this;
        }

        /**
         * Adds allowed attributes for the given tag.
         * <p>
         * The tag and attribute names are normalized to lower-case before storage. If the tag has no prior
         * attribute set, a new set is created.
         *
         * @param tag HTML tag name the attributes apply to; must not be {@code null}
         * @param attributes varargs of attribute names to allow for the tag; must not be {@code null} and must not contain {@code null} elements
         * @return this Builder for chaining
         * @throws NullPointerException if {@code tag} is {@code null}, if {@code attributes} is {@code null}, or if any attribute is {@code null}
         */
        public Builder allowAttributes(final String tag, final String... attributes) {
            final Set<String> attrSet = allowedAttributes.computeIfAbsent(
                    tag.toLowerCase(), k -> new HashSet<>());
            for (final String attr : attributes) {
                attrSet.add(attr.toLowerCase());
            }
            return this;
        }

        /**
         * Marks the given tags as self-closing in the builder.
         * <p>
         * Each tag is normalized to lower-case before storage.
         *
         * @param tags varargs of tag names to mark as self-closing; must not be {@code null} and must not contain {@code null} elements
         * @return this Builder for chaining
         * @throws NullPointerException if {@code tags} is {@code null} or any element is {@code null}
         */
        public Builder selfClosingTags(final String... tags) {
            for (final String tag : tags) {
                selfClosingTags.add(tag.toLowerCase());
            }
            return this;
        }

        /**
         * Builds an immutable SanitizeConfig from this Builder.
         * <p>
         * Performs defensive copies and normalization; the resulting SanitizeConfig is immutable.
         *
         * @return new immutable SanitizeConfig; never {@code null}
         * @throws NullPointerException if any configured tag or attribute name is {@code null}
         */
        public SanitizeConfig build() {
            return new SanitizeConfig(this);
        }
    }
}
