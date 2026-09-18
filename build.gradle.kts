import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.4.20"
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(
            providers.gradleProperty("platformType").get(),
            providers.gradleProperty("platformVersion").get()
        )

        // LSP4IJ (Red Hat) — generic LSP client that talks to the real Drools
        // Language Server (kiegroup/drools-lsp) for completion/diagnostics/etc.
        // Declared as optional in plugin.xml, but needed here at compile time
        // since DrlLanguageServerFactory implements its interfaces directly.
        // Check https://plugins.jetbrains.com/plugin/23257-lsp4ij for the
        // latest released version if this one has gone stale.
        plugin("com.redhat.devtools.lsp4ij", "0.21.0")

        pluginVerifier()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        id.set(providers.gradleProperty("pluginGroup"))
        name.set(providers.gradleProperty("pluginName"))
        version.set(providers.gradleProperty("pluginVersion"))

        ideaVersion {
            sinceBuild.set(providers.gradleProperty("pluginSinceBuild"))
            untilBuild.set(providers.gradleProperty("pluginUntilBuild"))
        }
    }

    signing {
        // Fill these via env vars if/when you publish to the Marketplace:
        // certificateChain.set(providers.environmentVariable("CERTIFICATE_CHAIN"))
        // privateKey.set(providers.environmentVariable("PRIVATE_KEY"))
        // password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        // token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }
}

kotlin {
    jvmToolchain(21)
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    runIde {
        // Uncomment to open a specific project/sample file on launch:
        // args = listOf(project.rootDir.resolve("samples").absolutePath)
    }
}
