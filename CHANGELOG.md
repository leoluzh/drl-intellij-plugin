# Changelog

All notable changes to the DRL Support plugin are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- `devbox.json` for a reproducible dev environment (JDK 21 + Gradle 8.14.3).
- `devbox-*` targets in the Makefile wrapping `devbox run`.
- `.github` scaffold: CI workflow, issue templates, PR template, Dependabot,
  PR labeler, and label sync.

### Changed

- Bumped Java/Kotlin compile target from 17 to 21 to match platform 2024.2's
  requirement.
- Bumped `org.jetbrains.intellij.platform` Gradle plugin from 2.1.0 to 2.11.0.

## [0.1.0]

### Added

- Initial DRL Support plugin scaffold.

[Unreleased]: https://github.com/leoluzh/drl-intellij-plugin/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/leoluzh/drl-intellij-plugin/releases/tag/v0.1.0
