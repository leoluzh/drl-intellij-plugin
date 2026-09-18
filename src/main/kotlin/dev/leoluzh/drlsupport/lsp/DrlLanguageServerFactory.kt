package dev.leoluzh.drlsupport.lsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider

/**
 * Wires LSP4IJ to the Drools Language Server (org.drools.lsp.server.Main,
 * from https://github.com/kiegroup/drools-lsp — Apache-2.0). Registered as
 * an optional-dependency extension in META-INF/drl-lsp4ij.xml, so this
 * class is only ever touched when the user has the LSP4IJ plugin
 * installed; the base plugin (syntax highlighting, commenting, brace
 * matching) works fine without it.
 */
class DrlLanguageServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider =
        DrlLanguageServerConnectionProvider(project)
}
