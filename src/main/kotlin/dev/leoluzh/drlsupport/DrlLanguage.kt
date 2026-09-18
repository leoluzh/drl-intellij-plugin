package dev.leoluzh.drlsupport

import com.intellij.lang.Language

/**
 * The DRL (Drools Rule Language) [Language] singleton. Registered as language id "DRL"
 * (see plugin.xml `<fileType language="DRL">` and `<lang.*>` extensions).
 */
object DrlLanguage : Language("DRL") {
    override fun getDisplayName(): String = DrlBundle.message("language.displayName")
    override fun isCaseSensitive(): Boolean = true
}
