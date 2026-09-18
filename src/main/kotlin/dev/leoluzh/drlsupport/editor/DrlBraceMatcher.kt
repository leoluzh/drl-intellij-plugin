package dev.leoluzh.drlsupport.editor

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import dev.leoluzh.drlsupport.lexer.DrlTokenTypes

class DrlBraceMatcher : PairedBraceMatcher {

    private val pairs = arrayOf(
        BracePair(DrlTokenTypes.LPAREN, DrlTokenTypes.RPAREN, false),
        BracePair(DrlTokenTypes.LBRACE, DrlTokenTypes.RBRACE, true),
        BracePair(DrlTokenTypes.LBRACKET, DrlTokenTypes.RBRACKET, false),
    )

    override fun getPairs(): Array<BracePair> = pairs

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int = openingBraceOffset
}
