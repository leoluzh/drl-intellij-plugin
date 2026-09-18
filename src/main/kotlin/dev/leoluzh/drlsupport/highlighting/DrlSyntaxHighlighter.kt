package dev.leoluzh.drlsupport.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import dev.leoluzh.drlsupport.lexer.DrlLexerAdapter
import dev.leoluzh.drlsupport.lexer.DrlTokenTypes

class DrlSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = DrlLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        val key = when (tokenType) {
            DrlTokenTypes.KEYWORD -> KEYWORD
            DrlTokenTypes.IDENTIFIER -> IDENTIFIER
            DrlTokenTypes.STRING -> STRING
            DrlTokenTypes.NUMBER -> NUMBER
            DrlTokenTypes.LINE_COMMENT, DrlTokenTypes.BLOCK_COMMENT -> COMMENT
            DrlTokenTypes.OPERATOR -> OPERATOR
            DrlTokenTypes.ANNOTATION -> ANNOTATION
            DrlTokenTypes.LPAREN, DrlTokenTypes.RPAREN -> PARENTHESES
            DrlTokenTypes.LBRACE, DrlTokenTypes.RBRACE -> BRACES
            DrlTokenTypes.LBRACKET, DrlTokenTypes.RBRACKET -> BRACKETS
            DrlTokenTypes.COMMA -> COMMA
            DrlTokenTypes.SEMICOLON -> SEMICOLON
            DrlTokenTypes.DOT, DrlTokenTypes.COLON -> DOT
            TokenType.BAD_CHARACTER -> BAD_CHARACTER
            else -> null
        }
        return if (key != null) arrayOf(key) else emptyArray()
    }

    companion object {
        val KEYWORD = createTextAttributesKey("DRL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val IDENTIFIER = createTextAttributesKey("DRL_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val STRING = createTextAttributesKey("DRL_STRING", DefaultLanguageHighlighterColors.STRING)
        val NUMBER = createTextAttributesKey("DRL_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val COMMENT = createTextAttributesKey("DRL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val OPERATOR = createTextAttributesKey("DRL_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val ANNOTATION = createTextAttributesKey("DRL_ANNOTATION", DefaultLanguageHighlighterColors.METADATA)
        val PARENTHESES = createTextAttributesKey("DRL_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        val BRACES = createTextAttributesKey("DRL_BRACES", DefaultLanguageHighlighterColors.BRACES)
        val BRACKETS = createTextAttributesKey("DRL_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        val COMMA = createTextAttributesKey("DRL_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val SEMICOLON = createTextAttributesKey("DRL_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
        val DOT = createTextAttributesKey("DRL_DOT", DefaultLanguageHighlighterColors.DOT)
        val BAD_CHARACTER = createTextAttributesKey("DRL_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
    }
}
