# Contributing to HTML Body Cleaner

Thank you for your interest in contributing to **HTML Body Cleaner** 🚀  
This document describes the contribution workflow and expectations.  
For general information about features, usage, installation, or project structure, please see the `README.md`.

---

## 🎯 Contribution Principles

- Make small, focused and reviewable changes. Prefer multiple focused PRs over a single large PR.
- Keep backwards compatibility when possible. For breaking API changes, open an issue first and provide a migration plan.
- Write tests for behavior you change or add. Code without tests must have a strong justification.
- Value readability and maintainability: prefer explicit and simple solutions over clever one-liners.

---

## 🧭 Before You Start

### 1️⃣ Search Existing Issues

Before creating a new issue or PR:

- Check whether the problem or feature request already exists.
- If it does, join the discussion instead of duplicating it.

### 2️⃣ Discuss Larger Changes First

If you are planning:

- API changes
- CLI behavior changes
- Sanitization strategy changes

Please open an issue first to discuss the design.

---

## 🛠 Development Guidelines

### Code Style

- Target Java 17+. Follow the existing repository style; if unsure, match adjacent files.
- Use 4 spaces indentation and UTF-8 encoding.
- Prefer `final` for local variables and fields where appropriate.
- Keep methods short; extract helper methods for readability.
- Use clear, intention-revealing names for classes, methods and variables.
- Use SLF4J for logging:
    - `private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyClass.class);`
- Use constructor injection for Spring components (no field injection).
- Avoid introducing new dependencies without discussion.

### API Design Rules

- Name commands and service methods clearly; prefer verbs for actions (e.g., `clean`, `extractStyles`).
- Validate public inputs and fail fast with clear exception messages.
- For library API:
    - Prefer DTOs/immutable types across the API boundary.
    - Avoid returning internal mutable objects (defensive copies if needed).
    - Document nullability: prefer non-null returns; accept `null` only when documented.
- For breaking changes:
    - Mark old APIs with `@Deprecated` and provide migration guidance in the Javadoc and `README.md`.
    - Keep semantic versioning in mind; document breaking changes in the changelog.

---

## 🧪 Testing Requirements

All contributions must include appropriate tests.

Specifically:

- New features must include unit tests.
- Bug fixes must include regression tests.
- Tests must be deterministic (no uncontrolled timing behavior).

Pull requests without tests will not be accepted.

---

## 🔄 Pull Request Process

### 1️⃣ Fork and Create Branch

Create a feature branch from `master`:

```bash
git checkout -b feature/short-description
```

### 2️⃣ Commit Message Rules

Use clear, concise messages in present tense:

- ✅ Fix(parser): handle null <body> element in HtmlBodyCleaner
- ❌ Fix stuff in HtmlBodyCleaner

Keep commits logically grouped.

### 3️⃣ Pull Request Checklist

Before submitting:

- Project builds successfully
- All tests pass
- Tests added for new functionality
- Public APIs documented
- No unrelated formatting changes
- No unnecessary dependency introduced

In your PR description, explain:

- What problem is solved
- Why the change is needed
- Any design trade-offs

---

## 🐞 Reporting Bugs

When opening a bug report, please include:

- Java version
- Minimal reproduction steps and sample input HTML (smallest example to reproduce)
- Expected vs actual behavior
- Stack trace if available

Clear reports help resolve issues faster.

---

## 📜 License

By contributing, you agree that your contributions will be licensed under the same license as the project (Apache 2.0).

---

Thank you for helping improve HTML Body Cleaner! 🙌