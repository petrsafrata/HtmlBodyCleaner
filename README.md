# This is a template for java library projects ⚠️

This repository serves as a template for Java library projects.

It includes preconfigured files such as:

- `LICENSE`
- `pom.xml`
- `.gitignore`
- `VERSION`
- `update-version.bat`
- `README.md`
- `CONTRIBUTING.md`

The template is intended to provide a consistent project structure and basic configuration for new Java library projects.

---

## How to Use This Template

Before using this template in a real project, several modifications are required.

### 1. Update `pom.xml`

In `pom.xml`, modify all fields marked with: `<!--- Change .... --->`

### 2. Update Package Name

Rename the base package: `cz.jpmad.library_name` to match your project name and organization.

### 3. Update `CONTRIBUTING.md`

In `CONTRIBUTING.md`, fill in all sections marked with:

- `TODO`
- `<!Project name!>`

Replace them with the correct project-specific information.

---

### 4. Update `README.md`

In `README.md`, complete all sections marked with:

- `TODO`
- `<!Project name!>`

Replace them with the correct project-specific information.

---

---

---


# <!Project name!> 📖
![Java](https://img.shields.io/badge/Java-17%2B-green)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
![Open Source](https://img.shields.io/badge/Open%20Source-Yes-brightgreen)
![GitHub Packages](https://img.shields.io/badge/GitHub_Packages-Active-brightgreen)
![Build](https://github.com/petrsafrata/RepoName/actions/workflows/maven-release.yml/badge.svg)


TODO: Describe the project in a few sentences. What problem does it solve? Who is it for? Why is it useful?

---

## ✨ Features

TODO: List key features of the library. 

---

## 📂 Project Structure

```
src/main/java/cz/jpmad/library_name/
├── 
│
└── TODO: Add package structure and key classes/interfaces with brief descriptions.
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
        <!-- Change to real repo url -->
        <url>https://maven.pkg.github.com/petrsafrata/Template</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>cz.jpmad</groupId>
        <!-- Change to real project name -->
        <artifactId>java-library-template</artifactId>
        <version>0.0.0</version>
    </dependency>
</dependencies>
```

### **Gradle**

```
repositories {
    maven {
        <!-- Change to real repo url -->
        url = uri("https://maven.pkg.github.com/petrsafrata/Template")
    }
}

dependencies {
    <!-- Change to real project name -->
    implementation("cz.jpmad:java-library-template:0.0.0")
}
```

## 🧠 Basic Usage

Retry a function that returns a value:

```java
//TODO: Add example usage of the library. Show how to call a function with retries, how to configure retry attempts, delay, and listeners.
```

## 🧪 Testing

The library includes full unit tests covering:

TODO: List key test cases and scenarios covered by the tests (e.g., successful retries, max attempts reached, delay between retries, listener notifications).

You can run tests with:

```bash
mvn test
```

## 🤝 Contributing

Contributions are welcome!

Please read the [CONTRIBUTING.md](CONTRIBUTING.md) file for details.

## 📜 Licence

This project is open-source and released under the Apache License 2.0.
You are free to use, modify, distribute, and use it commercially under the terms of the Apache 2.0 license.
See the [LICENSE](LICENSE) file for full details.
```
Apache-2.0 – Copyright (c) 2025 Petr Šafrata
```