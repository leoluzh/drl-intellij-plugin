package dev.leoluzh.drlsupport.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * Per-project configuration for launching the Drools Language Server
 * (https://github.com/kiegroup/drools-lsp). Every field here mirrors a
 * `drools.lsp.*` setting the server reads as a JVM system property (see
 * DrlLanguageServerConnectionProvider) — the same properties the reference
 * VS Code extension exposes.
 */
@State(name = "DrlLspSettings", storages = [Storage("drlLspSettings.xml")])
class DrlLspSettingsState : PersistentStateComponent<DrlLspSettingsState.State> {

    class State {
        var serverJarPath: String = ""
        var logLevel: String = "INFO"
        var lintMissingEnd: String = "warning"
        var lintMissingSeparator: String = "warning"
        var lintMissingSemicolon: String = "warning"
        var lintUnbalancedParens: String = "warning"
        var lintUnknownTypes: String = "warning"
        var lintMvelPropertyAccess: String = "off"
        var inlayHintsEnabled: Boolean = true
        var mavenPomPath: String = ""
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(project: Project): DrlLspSettingsState = project.service()
    }
}
