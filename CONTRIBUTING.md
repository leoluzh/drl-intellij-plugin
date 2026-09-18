# Contributing to DRL Support

Thanks for taking the time to contribute. This project is governed by our
[Code of Conduct](CODE_OF_CONDUCT.md); by participating you agree to uphold
it.

## Prerequisites

- JDK 21
- The bundled Gradle wrapper (`./gradlew`) — no system Gradle needed
- Maven (only if you need to build `drools-lsp` locally)

Don't want to install JDK 21 yourself? Use [Devbox](https://www.jetify.com/devbox)
(`devbox shell`) — see `devbox.json`, it provisions JDK 21 + Gradle 9.7.1 for
you.

## First-time setup

```sh
make setup    # generates ./gradlew + builds the drools-lsp server jar
```

See `make help` for every available target (build, test, run, verify, plugin
packaging, devbox variants, drools-lsp management).

## Development loop

```sh
make compile      # fast Kotlin compile only
make build        # full gradle build
make test         # run the test suite
make run          # launch a sandbox IDE with the plugin installed
```

Point the sandbox IDE's Settings -> Tools -> Drools LSP -> Server jar at the
path `make drools-lsp` printed, so language features have a server to talk
to.

If you're using devbox instead: `make devbox-build`, `make devbox-test`,
`make devbox-run`, etc. (same targets, run inside `devbox run`).

## Making changes

1. Fork/branch off `main`.
2. Keep changes focused — one logical change per PR.
3. Run `make build` (and `make test`) before opening a PR; CI runs the same
   `./gradlew build` + `./gradlew verifyPlugin`.
4. Update `CHANGELOG.md` under `[Unreleased]` for any user-facing change.
5. Fill out the PR template — link the issue it closes, if any.

## Commit messages

Keep the subject line short and imperative ("Fix X", "Add Y", not "Fixed" or
"Adding"). Explain *why* in the body when it isn't obvious from the diff.

## Reporting bugs / requesting features

Use the issue templates (Bug report / Feature request) — they ask for the
details maintainers need to act quickly (repro steps, plugin/IDE version,
logs).

## Testing against real DRL files

Sample `.drl` files live in `samples/`. Add new samples there when a bug
report or feature depends on a specific rule-file shape, so it's covered by
manual testing (and future automated tests).

## Code style

- Kotlin only in `src/main/kotlin` — no unrelated formatting-only diffs in a
  functional PR.
- No comments explaining *what* code does — only *why*, when it's genuinely
  non-obvious.
