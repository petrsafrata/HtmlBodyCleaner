# HTML Body Cleaner 📖
![Java](https://img.shields.io/badge/Java-17%2B-green)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
![Open Source](https://img.shields.io/badge/Open%20Source-Yes-brightgreen)
![GitHub Packages](https://img.shields.io/badge/GitHub_Packages-Active-brightgreen)
![Build](https://github.com/petrsafrata/RepoName/actions/workflows/maven-release.yml/badge.svg)


A small, dependency-light Java library to sanitize and normalize HTML document bodies. 
It extracts the document body, sanitizes tags and attributes according to configurable rules, 
converts inline `style` attributes into a deduplicated CSS block, and reassembles the cleaned HTML. 
Designed for server-side use in Java web pipelines and batch processors.

---

## ✨ Features

- 🧩 Extracts and isolates content inside the `<body>` element.
- ⚙️ Configurable sanitization of allowed/disallowed tags and attributes.
- 🔁 Converts inline `style` attributes into a single generated CSS block with deduplication.
- 🛡️ Preserves token order and textual content while removing disallowed constructs.

---

## 📂 Project Structure

```
src/main/java/cz/jpmad/htmlbodycleaner/
├── cli
│   └── Main.java                       # Command-line interface for processing HTML files.
├── config
│   └── SanitizationConfig.java         # Configuration class for sanitization rules.
├── parser
│   ├── HtmlToken.java                  # Represents a token in the HTML document.
│   ├── HtmlTokenizer.java              # Tokenizer that converts HTML into a stream of tokens.
│   └── HtmlWriter.java                 # Utility to write tokens back into HTML format.
├── sanitizer
│   ├── BodyExtractor.java              # Extracts content from the <body> element.
│   ├── StyleExtractor.java             # Extracts and deduplicates inline styles into a CSS block.
│   └── TagSanitizer.java               # Sanitizes tags and attributes based on configuration.
└── HtmlBodyCleaner.java                # Main class that orchestrates the cleaning process.
```

---

## 🚀 Installation

This library is published to **GitHub Packages**.  
To use it in your project, you need to add the GitHub Packages repository and the dependency.

### Maven

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/petrsafrata/HtmlBodyCleaner</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>cz.jpmad</groupId>
        <artifactId>html-body-cleaner</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### **Gradle**

```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/petrsafrata/HtmlBodyCleaner")
    }
}

dependencies {
    implementation("cz.jpmad:html-body-cleaner:1.0.0")
}
```

## 🧠 Basic Usage

Simple example using the default configuration:

```java
import cz.jpmad.htmlbodycleaner.HtmlBodyCleaner;

public class Example {
    public static void main(String[] args) {
        final String rawHtml = "<html><body><div style=\"color:red\">Hello</div></body></html>";
        final HtmlBodyCleaner cleaner = new HtmlBodyCleaner(); // or new HtmlBodyCleaner(config)
        final String cleaned = cleaner.clean(rawHtml);
        System.out.println(cleaned);
    }
}
```

Usage with CLI interface:

```bash
java -jar html-body-cleaner-cli.jar --input input.html --output output.html
```


## 🧪 Testing

The library includes full unit tests covering:

- Allowed/disallowed tag handling
- Inline style extraction and CSS deduplication
- Edge cases (null/empty input, malformed HTML, nested disallowed tags)

You can run tests with:

```bash
mvn test
```

## 🤝 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests to improve the API, add features 
(e.g. CLI options, additional sanitization strategies, style extraction improvements), fix bugs, or expand the documentation.
Please read the [CONTRIBUTING.md](CONTRIBUTING.md) file for details.

## 📜 Licence

This project is open-source and released under the Apache License 2.0.
You are free to use, modify, distribute, and use it commercially under the terms of the Apache 2.0 license.
See the [LICENSE](LICENSE) file for full details.
```
Apache-2.0 – Copyright (c) 2025 Petr Šafrata
```