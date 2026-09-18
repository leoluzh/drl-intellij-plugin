# Makefile for the DRL Support IntelliJ plugin project.
# Run `make help` (or just `make`) to list every available target.

SHELL := /bin/bash

# Uses the Gradle wrapper once it exists (see `make wrapper`); falls back to
# a system-installed `gradle` until then.
GRADLE          := $(if $(wildcard ./gradlew),./gradlew,gradle)
GRADLE_VERSION  ?= 8.14.3

DROOLS_LSP_DIR  ?= drools-lsp
DROOLS_LSP_REPO ?= https://github.com/kiegroup/drools-lsp.git
DROOLS_LSP_JAR  := $(DROOLS_LSP_DIR)/drools-lsp-server/target/drools-lsp-server-jar-with-dependencies.jar

.DEFAULT_GOAL := help

# --- Help --------------------------------------------------------------------

.PHONY: help
help: ## Show this help
	@echo "DRL Support -- available targets:"
	@awk 'BEGIN {FS = ":.*##"} /^[a-zA-Z0-9_-]+:.*##/ { printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)
	@echo
	@echo "Current Gradle runner: $(GRADLE)"

# --- First-time setup ---------------------------------------------------------

.PHONY: setup
setup: wrapper drools-lsp ## One-shot first-time setup: generate the wrapper + build the LSP server jar
	@echo
	@echo "Setup complete."
	@echo "Server jar: $(abspath $(DROOLS_LSP_JAR))"
	@echo "Paste that path into Settings/Preferences -> Tools -> Drools LSP -> Server jar,"
	@echo "then run 'make run' to open the sandbox IDE."

# --- Gradle wrapper ------------------------------------------------------------

.PHONY: wrapper
wrapper: ## Generate ./gradlew, pinned to GRADLE_VERSION (needs a system 'gradle' once)
	gradle wrapper --gradle-version $(GRADLE_VERSION)

# --- Plugin build / run ---------------------------------------------------------

.PHONY: compile
compile: ## Compile Kotlin sources only (fast feedback loop, no tests/packaging)
	$(GRADLE) compileKotlin compileTestKotlin

.PHONY: build
build: ## Full build: compile + run checks (gradle build)
	$(GRADLE) build

.PHONY: test
test: ## Run the plugin's test suite
	$(GRADLE) test

.PHONY: run
run: ## Launch a sandbox IntelliJ IDEA with the plugin installed (gradle runIde)
	$(GRADLE) runIde

.PHONY: verify
verify: ## Run the JetBrains Plugin Verifier against the configured target IDEs
	$(GRADLE) verifyPlugin

.PHONY: plugin
plugin: ## Build the installable plugin zip (build/distributions/*.zip)
	$(GRADLE) buildPlugin
	@echo "Built: $$(ls build/distributions/*.zip 2>/dev/null)"

.PHONY: clean
clean: ## Remove Gradle build output (build/, .gradle/)
	$(GRADLE) clean

.PHONY: clean-all
clean-all: clean drools-lsp-clean ## clean + also remove the cloned drools-lsp checkout

# --- Devbox (isolated JDK 21 env, see devbox.json) -------------------------------

DEVBOX := devbox

.PHONY: devbox-shell
devbox-shell: ## Enter an isolated devbox shell with JDK 21 provisioned
	$(DEVBOX) shell

.PHONY: devbox-compile
devbox-compile: ## Compile inside devbox (JDK 21)
	$(DEVBOX) run compile

.PHONY: devbox-build
devbox-build: ## Full build inside devbox (JDK 21)
	$(DEVBOX) run build

.PHONY: devbox-test
devbox-test: ## Run tests inside devbox (JDK 21)
	$(DEVBOX) run test

.PHONY: devbox-run
devbox-run: ## Launch sandbox IDE inside devbox (JDK 21)
	$(DEVBOX) run run-ide

# --- Drools Language Server (kiegroup/drools-lsp) --------------------------------

.PHONY: drools-lsp
drools-lsp: $(DROOLS_LSP_JAR) ## Clone (if needed) and build the drools-lsp-server jar
	@echo "Server jar: $(abspath $(DROOLS_LSP_JAR))"

# Clones on demand (kept inside this recipe, not a separate directory rule,
# so it can't collide with the `drools-lsp` phony convenience target above).
$(DROOLS_LSP_JAR):
	@if [ ! -d "$(DROOLS_LSP_DIR)/.git" ]; then \
		git clone $(DROOLS_LSP_REPO) $(DROOLS_LSP_DIR); \
	fi
	cd $(DROOLS_LSP_DIR) && mvn -pl drools-lsp-server -am clean package
	@test -f $(DROOLS_LSP_JAR) || { echo "Build finished but jar not found at $(DROOLS_LSP_JAR)"; exit 1; }

.PHONY: drools-lsp-update
drools-lsp-update: ## Pull the latest drools-lsp and rebuild the server jar
	cd $(DROOLS_LSP_DIR) && git pull
	cd $(DROOLS_LSP_DIR) && mvn -pl drools-lsp-server -am clean package

.PHONY: drools-lsp-clean
drools-lsp-clean: ## Remove the cloned drools-lsp checkout
	rm -rf $(DROOLS_LSP_DIR)
