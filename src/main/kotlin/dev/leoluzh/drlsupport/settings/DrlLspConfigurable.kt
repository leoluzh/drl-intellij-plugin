package dev.leoluzh.drlsupport.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import dev.leoluzh.drlsupport.DrlBundle
import java.io.File
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * Settings/Preferences → Tools → Drools LSP. Everything here is plumbed
 * into the `java -jar ... -Ddrools.lsp.*=...` command line built by
 * DrlLanguageServerConnectionProvider each time LSP4IJ (re)starts the
 * server for this project.
 */
class DrlLspConfigurable(private val project: Project) : Configurable {

    private val severities = arrayOf("off", "hint", "info", "warning", "error")
    private val logLevels = arrayOf("SEVERE", "WARNING", "INFO", "FINE", "FINER", "FINEST")

    private val jarPathField = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(
            DrlBundle.message("settings.jarPath.chooserTitle"),
            DrlBundle.message("settings.jarPath.chooserDescription"),
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor("jar")
        )
    }
    private val logLevelBox = JComboBox(logLevels)
    private val lintMissingEndBox = JComboBox(severities)
    private val lintMissingSeparatorBox = JComboBox(severities)
    private val lintMissingSemicolonBox = JComboBox(severities)
    private val lintUnbalancedParensBox = JComboBox(severities)
    private val lintUnknownTypesBox = JComboBox(severities)
    private val lintMvelBox = JComboBox(severities)
    private val inlayHintsCheckBox = JCheckBox(DrlBundle.message("settings.inlayHints.checkbox"))
    private val mavenPomPathField = JTextField()

    override fun getDisplayName(): String = DrlBundle.message("settings.displayName")

    override fun createComponent(): JComponent {
        val panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(DrlBundle.message("settings.jarPath.label"), jarPathField)
            .addComponentToRightColumn(JLabel("<html>${DrlBundle.message("settings.jarPath.help")}</html>"))
            .addSeparator()
            .addLabeledComponent(DrlBundle.message("settings.logLevel.label"), logLevelBox)
            .addSeparator()
            .addComponent(JLabel(DrlBundle.message("settings.lint.group")))
            .addLabeledComponent("  " + DrlBundle.message("settings.lint.missingEnd"), lintMissingEndBox)
            .addLabeledComponent("  " + DrlBundle.message("settings.lint.missingSeparator"), lintMissingSeparatorBox)
            .addLabeledComponent("  " + DrlBundle.message("settings.lint.missingSemicolon"), lintMissingSemicolonBox)
            .addLabeledComponent("  " + DrlBundle.message("settings.lint.unbalancedParens"), lintUnbalancedParensBox)
            .addLabeledComponent("  " + DrlBundle.message("settings.lint.unknownTypes"), lintUnknownTypesBox)
            .addLabeledComponent("  " + DrlBundle.message("settings.lint.mvel"), lintMvelBox)
            .addSeparator()
            .addComponent(inlayHintsCheckBox)
            .addSeparator()
            .addLabeledComponent(DrlBundle.message("settings.mavenPomPath.label"), mavenPomPathField)
            .addComponentToRightColumn(
                JLabel(DrlBundle.message("settings.mavenPomPath.help", File.pathSeparator))
            )
            .addComponentFillVertically(JPanel(), 0)
            .panel
        reset()
        return panel
    }

    override fun isModified(): Boolean {
        val s = DrlLspSettingsState.getInstance(project).state
        return jarPathField.text.trim() != s.serverJarPath ||
            logLevelBox.selectedItem != s.logLevel ||
            lintMissingEndBox.selectedItem != s.lintMissingEnd ||
            lintMissingSeparatorBox.selectedItem != s.lintMissingSeparator ||
            lintMissingSemicolonBox.selectedItem != s.lintMissingSemicolon ||
            lintUnbalancedParensBox.selectedItem != s.lintUnbalancedParens ||
            lintUnknownTypesBox.selectedItem != s.lintUnknownTypes ||
            lintMvelBox.selectedItem != s.lintMvelPropertyAccess ||
            inlayHintsCheckBox.isSelected != s.inlayHintsEnabled ||
            mavenPomPathField.text.trim() != s.mavenPomPath
    }

    override fun apply() {
        val s = DrlLspSettingsState.getInstance(project).state
        s.serverJarPath = jarPathField.text.trim()
        s.logLevel = logLevelBox.selectedItem as String
        s.lintMissingEnd = lintMissingEndBox.selectedItem as String
        s.lintMissingSeparator = lintMissingSeparatorBox.selectedItem as String
        s.lintMissingSemicolon = lintMissingSemicolonBox.selectedItem as String
        s.lintUnbalancedParens = lintUnbalancedParensBox.selectedItem as String
        s.lintUnknownTypes = lintUnknownTypesBox.selectedItem as String
        s.lintMvelPropertyAccess = lintMvelBox.selectedItem as String
        s.inlayHintsEnabled = inlayHintsCheckBox.isSelected
        s.mavenPomPath = mavenPomPathField.text.trim()
    }

    override fun reset() {
        val s = DrlLspSettingsState.getInstance(project).state
        jarPathField.text = s.serverJarPath
        logLevelBox.selectedItem = s.logLevel
        lintMissingEndBox.selectedItem = s.lintMissingEnd
        lintMissingSeparatorBox.selectedItem = s.lintMissingSeparator
        lintMissingSemicolonBox.selectedItem = s.lintMissingSemicolon
        lintUnbalancedParensBox.selectedItem = s.lintUnbalancedParens
        lintUnknownTypesBox.selectedItem = s.lintUnknownTypes
        lintMvelBox.selectedItem = s.lintMvelPropertyAccess
        inlayHintsCheckBox.isSelected = s.inlayHintsEnabled
        mavenPomPathField.text = s.mavenPomPath
    }
}
