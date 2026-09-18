# Security Policy

## Supported Versions

Only the latest released version of DRL Support receives security fixes.

| Version | Supported          |
| ------- | ------------------ |
| latest  | :white_check_mark: |
| < latest | :x:                |

## Reporting a Vulnerability

Please **do not** open a public issue for security vulnerabilities.

Instead, report it privately via
[GitHub's private vulnerability reporting](../../security/advisories/new)
for this repository, or by emailing leonardo.l.fernandes@gmail.com.

Include:

- A description of the vulnerability and its potential impact
- Steps to reproduce (a minimal `.drl` file or project setup, if relevant)
- Affected plugin version and IntelliJ IDEA version

You should get an initial response within a few days. If the issue is
confirmed, a fix will be prioritized and a new release published; you'll be
credited in the release notes unless you ask not to be.

## Scope

This plugin runs inside IntelliJ IDEA's sandbox and talks to a locally
configured Drools Language Server (LSP4IJ + `drools-lsp`). Reports specific
to `kiegroup/drools-lsp` itself should go to that project instead.
