package dev.leoluzh.drlsupport.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet

object DrlTokenTypes {
    @JvmField val KEYWORD = DrlTokenType("KEYWORD")
    @JvmField val IDENTIFIER = DrlTokenType("IDENTIFIER")
    @JvmField val STRING = DrlTokenType("STRING")
    @JvmField val NUMBER = DrlTokenType("NUMBER")
    @JvmField val LINE_COMMENT = DrlTokenType("LINE_COMMENT")
    @JvmField val BLOCK_COMMENT = DrlTokenType("BLOCK_COMMENT")
    @JvmField val OPERATOR = DrlTokenType("OPERATOR")
    @JvmField val ANNOTATION = DrlTokenType("ANNOTATION")
    @JvmField val LPAREN = DrlTokenType("LPAREN")
    @JvmField val RPAREN = DrlTokenType("RPAREN")
    @JvmField val LBRACE = DrlTokenType("LBRACE")
    @JvmField val RBRACE = DrlTokenType("RBRACE")
    @JvmField val LBRACKET = DrlTokenType("LBRACKET")
    @JvmField val RBRACKET = DrlTokenType("RBRACKET")
    @JvmField val COMMA = DrlTokenType("COMMA")
    @JvmField val SEMICOLON = DrlTokenType("SEMICOLON")
    @JvmField val DOT = DrlTokenType("DOT")
    @JvmField val COLON = DrlTokenType("COLON")

    @JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
    @JvmField val STRINGS = TokenSet.create(STRING)
    @JvmField val WHITESPACE = TokenSet.create(TokenType.WHITE_SPACE)
}
