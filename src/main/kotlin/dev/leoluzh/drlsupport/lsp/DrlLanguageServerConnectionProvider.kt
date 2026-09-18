package dev.leoluzh.drlsupport.lsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.CannotStartProcessException
import com.redhat.devtools.lsp4ij.server.JavaProcessCommandBuilder
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import dev.leoluzh.drlsupport.DrlBundle
import dev.leoluzh.drlsupport.settings.DrlLspSettingsState
import java.io.File

/**
 * Launches `org.drools.lsp.server.Main` (the Drools Language Server) as a
 * subprocess and talks to it over stdio — exactly like the reference VS
 * Code client (client/src/extension.ts in kiegroup/drools-lsp): plain
 * `java [-D...] -jar drools-lsp-server-jar-with-dependencies.jar`, no
 * extra flags, no socket mode. All `drools.lsp.*` settings are passed as
 * JVM system properties because the server currently ignores
 * `workspace/didChangeConfiguration` and only reads them at startup.
 */
class DrlLanguageServerConnectionProvider(private val project: Project) : ProcessStreamConnectionProvider() {

    init {
        setCommands(buildCommand())
    }

    override fun start() {
        val jarPath = DrlLspSettingsState.getInstance(project).state.serverJarPath.trim()
        if (jarPath.isEmpty() || !File(jarPath).isFile) {
            throw CannotStartProcessException(DrlBundle.message("lsp.error.jarNotConfigured"))
        }
        // Rebuild in case settings changed since this provider was constructed.
        setCommands(buildCommand())
        super.start()
    }

    private fun buildCommand(): List<String> {
        val settings = DrlLspSettingsState.getInstance(project).state
        val jarPath = settings.serverJarPath.trim()

        // JavaProcessCommandBuilder resolves the right `java` executable for the
        // project (and wires up LSP4IJ's own per-server debug-port support) —
        // we only need to splice our `-D...` properties in before `-jar`.
        val base = JavaProcessCommandBuilder(project, SERVER_ID)
            .setJar(jarPath.ifEmpty { "drools-lsp-server-jar-with-dependencies.jar" })
            .create()

        val jarIndex = base.indexOf("-jar").let { if (it < 0) base.size else it }

        val extraArgs = buildList {
            add("-Ddrools.lsp.logLevel=${settings.logLevel}")
            add("-Ddrools.lsp.lint.missingEnd=${settings.lintMissingEnd}")
            add("-Ddrools.lsp.lint.missingSeparator=${settings.lintMissingSeparator}")
            add("-Ddrools.lsp.lint.missingSemicolon=${settings.lintMissingSemicolon}")
            add("-Ddrools.lsp.lint.unbalancedParens=${settings.lintUnbalancedParens}")
            add("-Ddrools.lsp.lint.unknownTypes=${settings.lintUnknownTypes}")
            add("-Ddrools.lsp.lint.mvelPropertyAccess=${settings.lintMvelPropertyAccess}")
            add("-Ddrools.lsp.inlayHints.enabled=${settings.inlayHintsEnabled}")
            if (settings.mavenPomPath.isNotBlank()) {
                add("-Ddrools.lsp.maven.pomPath=${settings.mavenPomPath.trim()}")
            }
        }

        val commands = ArrayList<String>(base.size + extraArgs.size)
        commands.addAll(base.subList(0, jarIndex))
        commands.addAll(extraArgs)
        commands.addAll(base.subList(jarIndex, base.size))
        return commands
    }

    override fun toString(): String = "Drools Language Server: ${getCommands()}"

    companion object {
        /** Matches the `id` of the `<server>` extension in META-INF/drl-lsp4ij.xml. */
        const val SERVER_ID = "droolsLanguageServer"
    }
}
