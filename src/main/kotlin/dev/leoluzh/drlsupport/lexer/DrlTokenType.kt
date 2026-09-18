package dev.leoluzh.drlsupport.lexer

import com.intellij.psi.tree.IElementType
import dev.leoluzh.drlsupport.DrlLanguage
import org.jetbrains.annotations.NonNls

class DrlTokenType(@NonNls debugName: String) : IElementType(debugName, DrlLanguage) {
    override fun toString(): String = "DrlTokenType." + super.toString()
}
