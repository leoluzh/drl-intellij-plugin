---
name: java-build
description: Build, test, run, and package the DRL Support IntelliJ plugin (Kotlin/Java, Gradle). Use for any compile/build/test/run/verify/package request in this repo, or when hitting JDK/toolchain/Gradle version errors.
---

# Java/Kotlin build for this project

This is a Kotlin IntelliJ plugin (`org.jetbrains.intellij.platform` Gradle
plugin), targeting platform `2024.2`. Toolchain: **JDK 21** (Kotlin/Java
compile target and IntelliJ Platform Gradle Plugin 2.x both require it).

## Commands

Prefer the Makefile — it wraps the Gradle wrapper (`./gradlew`) and never
needs a system Gradle install:

- `make compile` — fast Kotlin-only compile, no tests/packaging
- `make build` — full `gradle build`
- `make test` — run the test suite
- `make run` — launch a sandbox IDE with the plugin installed (`runIde`)
- `make verify` — JetBrains Plugin Verifier
- `make plugin` — build the installable zip (`build/distributions/*.zip`)
- `make setup` — first-time: generate wrapper + build the `drools-lsp`
  server jar (needed for the LSP feature to do anything at runtime)
- `make devbox-build` / `devbox-test` / `devbox-run` — same, but inside
  `devbox shell` (JDK 21 + Gradle 9.7.1 provisioned automatically — use
  this if the host doesn't have a working JDK 21)

Direct Gradle equivalents: `./gradlew compileKotlin`, `./gradlew build`,
`./gradlew test`, `./gradlew runIde`, `./gradlew verifyPlugin`,
`./gradlew buildPlugin`.

## Known pitfalls (already hit and fixed once — don't re-diagnose)

- **No JDK 21 on PATH / `JAVA_HOME` points elsewhere**: pick an installed
  JDK 21 explicitly, e.g. `JAVA_HOME=/path/to/jdk-21 ./gradlew build`, or
  use `devbox shell` which provisions `jdk21@latest` (nixpkgs attribute is
  `jdk21`, **not** `openjdk21` — that name doesn't resolve).
- **`Cannot find a Java installation ... languageVersion=21` /
  `Toolchain download repositories have not been configured`**: means no
  matching JDK is installed and Gradle can't auto-provision one.
  `settings.gradle.kts` already applies the `foojay-resolver-convention`
  plugin, which lets Gradle auto-download the right JDK — if this error
  still appears, check network access to the Foojay Disco API.
- **`IntelliJ Platform Gradle Plugin requires Gradle 9.0.0 and higher`**:
  this repo pins Gradle 9.7.1 (see `gradle/wrapper/gradle-wrapper.properties`
  and `devbox.json`'s `gradle@9.7.1`). The IntelliJ Platform Gradle Plugin
  version in `build.gradle.kts` must stay **2.11.0 or lower** (2.12.0 bumped
  its minimum Gradle requirement to 9.0.0) unless Gradle itself is upgraded
  too — don't bump one without the other.
- **`sourceCompatibility='17' but IntelliJ Platform '2024.2' requires
  sourceCompatibility='21'`**: `build.gradle.kts` sets
  `sourceCompatibility`/`targetCompatibility`/`jvmTarget`/`jvmToolchain` to
  21 — keep all four in sync if you ever touch them.
- **`instrumentationTools()` deprecation warning**: not needed with IntelliJ
  Platform Gradle Plugin 2.x — don't re-add it.

## Project layout

- `src/main/kotlin` — plugin source (Kotlin only, no Java sources currently)
- `src/main/resources/META-INF` — `plugin.xml`
- `samples/*.drl` — sample rule files for manual testing
- `drools-lsp/` — cloned+built on demand by `make drools-lsp`, not checked in
