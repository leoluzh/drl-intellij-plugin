package dev.leoluzh.drlsupport.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import dev.leoluzh.drlsupport.DrlLanguage
import dev.leoluzh.drlsupport.lexer.DrlLexerAdapter
import dev.leoluzh.drlsupport.lexer.DrlTokenTypes

class DrlParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer = DrlLexerAdapter()

    override fun createParser(project: Project?): PsiParser = DrlParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = DrlTokenTypes.COMMENTS

    override fun getStringLiteralElements(): TokenSet = DrlTokenTypes.STRINGS

    override fun getWhitespaceTokens(): TokenSet = DrlTokenTypes.WHITESPACE

    override fun createElement(node: ASTNode): PsiElement =
        throw AssertionError("Unexpected element: ${node.elementType} (the DRL parser only ever produces the root node)")

    override fun createFile(viewProvider: FileViewProvider): PsiFile = DrlFile(viewProvider)

    companion object {
        val FILE = IFileElementType(DrlLanguage)
    }
}
