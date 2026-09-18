# DRL Support — IntelliJ plugin for Drools

IntelliJ Platform plugin (IDEA Community/Ultimate) for editing `.drl`
(Drools Rule Language) files.

> **Status:** code complete and reviewed against the real LSP4IJ and
> kiegroup/drools-lsp APIs (sources read directly on GitHub), but **not
> compiled in this environment** — the sandbox where it was generated has
> no access to the Gradle Plugin Portal (`plugins.gradle.org`) or the
> JetBrains plugin repository, so `./gradlew build` couldn't be run here.
> Run `gradle build` (step 2 below) on your machine as a first step — that
> is the real test.

## How it works (two layers)

1. **Own layer, always active** — `DrlLanguage`/`DrlFileType` + a
   hand-written lexer (`lexer/DrlLexerAdapter.kt`) provide syntax
   highlighting, line/block commenting (`Ctrl+/`, `Ctrl+Shift+/`) and
   `(){}[]` brace matching. No external dependency.

2. **Smart layer, via [LSP4IJ](https://plugins.jetbrains.com/plugin/23257-lsp4ij)** —
   when the LSP4IJ plugin (Red Hat, free) is installed, this plugin
   registers the real **Drools Language Server**
   ([kiegroup/drools-lsp](https://github.com/kiegroup/drools-lsp), the same
   one behind the "DRL Editor" VS Code extension) as the LSP server for
   `*.drl` files. That brings keyword completion, Java types and
   fields/properties, diagnostics (missing `end`, unbalanced parentheses,
   unknown types with quick-fix...), hover, go-to-definition, find
   references, rename, outline and inlay hints — all implemented by
   Drools' real ANTLR4 parser, not by this plugin.

   This dependency is **optional** (`<depends optional="true" ...>`):
   without LSP4IJ installed, the plugin still works, just without the
   smart part.

## Prerequisites

- **To develop/run this plugin**: JDK 17+ and the Gradle Wrapper
  (included; it downloads Gradle and the IntelliJ Platform on its own —
  only needs network on the first run).
- **For the smart (LSP) part**: Java 17+ and Maven, to build the
  `drools-lsp-server` jar (see step 1 below). This is the same requirement
  listed in the VS Code extension's README.

## Step by step

### 1. Build the Drools Language Server jar

```bash
git clone https://github.com/kiegroup/drools-lsp.git
cd drools-lsp
mvn -pl drools-lsp-server -am clean package
```

This produces
`drools-lsp-server/target/drools-lsp-server-jar-with-dependencies.jar`
(a "fat" jar with all dependencies — what the `java -jar` command line
needs). Keep the full path to this file.

### 2. Run this plugin in a test IDE (sandbox)

This project doesn't ship the Gradle Wrapper committed (`gradlew`
downloads the right Gradle on its own, but that requires generating the
wrapper once with internet access). If you already have Gradle 8.x
installed, just run:

```bash
cd drl-intellij-plugin
gradle runIde
```

If you'd rather use the wrapper (recommended for reproducible builds /
CI), generate it once and then use `./gradlew`:

```bash
gradle wrapper --gradle-version 9.7.1
./gradlew runIde
```

`runIde` opens a "sandbox" instance of IntelliJ IDEA Community with the
plugin already installed. On the first run Gradle downloads the IntelliJ
platform and dependencies (LSP4IJ included) — it can take a few minutes.

This opens a "sandbox" instance of IntelliJ IDEA Community with the
plugin already installed. The first time, Gradle downloads the IntelliJ
platform and dependencies — it can take a few minutes.

Inside that sandbox instance:

1. Install **LSP4IJ** via `Settings/Preferences → Plugins → Marketplace`
   (search for "LSP4IJ").
2. Open `Settings/Preferences → Tools → Drools LSP` and paste, in "Server
   jar", the path to the
   `drools-lsp-server-jar-with-dependencies.jar` from step 1.
3. Open `samples/sample.drl` (included in this repository) — or any
   project with `.drl` files — and check: highlighting should work
   immediately; completion (`Ctrl+Space` inside a `when`), hover and
   diagnostics should appear as soon as the server starts (watch
   `View → Tool Windows → LSP Consoles` if something doesn't show up).

### 3. Package to install in a "real" IDE

```bash
gradle buildPlugin   # or ./gradlew buildPlugin, once the wrapper is generated
```

Produces `build/distributions/drl-support-0.1.0.zip`. In any IntelliJ
Platform IDE: `Settings/Preferences → Plugins → ⚙️ → Install Plugin from
Disk...` and select this zip. Repeat steps 1 and 2 above (install LSP4IJ,
build the jar, point to the path in Settings) inside that IDE.

## Settings (Settings → Tools → Drools LSP)

| Field | Maps to | Default |
|---|---|---|
| Server jar | local path to the jar (required for the LSP part) | empty |
| Log level | `-Ddrools.lsp.logLevel` | `INFO` |
| Lint: missing `end`, separator, `;`, parentheses, unknown types, MVEL style | `-Ddrools.lsp.lint.*` | `warning` (MVEL: `off`) |
| Inlay hints | `-Ddrools.lsp.inlayHints.enabled` | enabled |
| Maven POM(s) | `-Ddrools.lsp.maven.pomPath` — used by the server to resolve the Java classpath (types used in rules) | empty → uses the project root's `pom.xml` |

These settings are per-project (stored in
`.idea/drlLspSettings.xml`) and are read again every time LSP4IJ restarts
the server.

## Internationalization (i18n)

All UI strings coming from our own Kotlin code (settings screen labels,
names on the colors screen, error message when the jar isn't configured,
etc.) go through `DrlBundle.message("key")`
(`DrlBundle.kt`, JetBrains' official `DynamicBundle` pattern). The
**default language is English**:
`src/main/resources/messages/DrlBundle.properties`. I also included
`DrlBundle_pt_BR.properties` with the same keys in Portuguese — Java's
default `ResourceBundle` picks this file automatically when the IDE runs
under the `pt_BR` locale (for example, with the corresponding Language
Pack installed); otherwise everyone gets English.

To add another language, just create
`messages/DrlBundle_<locale>.properties` with the same keys — no code
changes needed.

The `plugin.xml`/`drl-lsp4ij.xml` texts (plugin name and description, the
language server description) stay as English literals directly in the
XML, and don't go through `DrlBundle`: third-party extension points
generally don't resolve resource-bundle keys automatically the way
`<action>` does, so it wouldn't make a difference at runtime.

## Project structure

```
build.gradle.kts, settings.gradle.kts, gradle.properties  — Gradle build
src/main/resources/META-INF/
  plugin.xml            — main plugin declaration
  drl-lsp4ij.xml         — LSP4IJ extensions (only loads if LSP4IJ is installed)
src/main/resources/messages/
  DrlBundle.properties        — UI strings in English (default)
  DrlBundle_pt_BR.properties  — UI strings in Portuguese (pt_BR)
src/main/kotlin/dev/leoluzh/drlsupport/
  DrlLanguage.kt, DrlFileType.kt, DrlIcons.kt, DrlBundle.kt
  lexer/                — lexer + token table + keywords
  psi/                  — minimal (flat) parser + ParserDefinition + PsiFile
  highlighting/         — SyntaxHighlighter + ColorSettingsPage
  editor/                — Commenter + BraceMatcher
  settings/              — "Tools > Drools LSP" screen + persisted state
  lsp/                   — LanguageServerFactory + ProcessStreamConnectionProvider
samples/sample.drl       — sample file for testing
```

## Why not a custom DRL parser?

A full DRL parser could have been written here, but the KIE group
(maintainer of Drools/Kogito) already publishes and maintains one — with
the real ANTLR4 parser, completion engine (C3) and all validations — as a
reusable Language Server (Apache-2.0). Wrapping that server via LSP4IJ
gives a much more correct result (same source of truth used in VS Code)
with a fraction of the code, and keeps receiving upstream improvements
without needing to touch this plugin.

## Known limitations

- The Drools LSP server ignores `workspace/didChangeConfiguration`; that's
  why settings are passed as `-D...` on the command line and only take
  effect from the next server restart onward (LSP4IJ restarts
  automatically when you change the jar path; for the other options, use
  "Restart" on the server icon in `LSP Consoles` after changing them).
- `drools-lsp` is under active development (v1.0.x); check the upstream
  repository if something stops working after an update.
- The local (layer 1) highlighting is lexical, not semantic: it colors by
  token type, not by "is this a valid Java type" — that's the LSP's job.

## Licenses

- This plugin: its code is yours to use/distribute as you wish.
- [kiegroup/drools-lsp](https://github.com/kiegroup/drools-lsp): Apache
  License 2.0.
- [LSP4IJ](https://github.com/redhat-developer/lsp4ij): Eclipse Public
  License 2.0.
