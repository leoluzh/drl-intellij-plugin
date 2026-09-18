package dev.leoluzh.drlsupport.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/**
 * Hand-written lexer for DRL files, used only to drive editor syntax
 * coloring, commenting and brace matching. It does not attempt to be a
 * correct/complete DRL grammar — completion, diagnostics and navigation
 * come from the real Drools parser running inside the Drools Language
 * Server (see the `lsp` package), which is what actually understands the
 * language.
 */
class DrlLexerAdapter : LexerBase() {

    private lateinit var buffer: CharSequence
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var currentToken: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        advanceInternal()
    }

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? = currentToken

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenStart = tokenEnd
        advanceInternal()
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd

    private fun advanceInternal() {
        if (tokenStart >= bufferEnd) {
            currentToken = null
            tokenEnd = tokenStart
            return
        }

        val c = buffer[tokenStart]

        when {
            isWhitespace(c) -> {
                var i = tokenStart
                while (i < bufferEnd && isWhitespace(buffer[i])) i++
                setToken(TokenType.WHITE_SPACE, i)
            }
            c == '/' && peek(1) == '/' -> {
                var i = tokenStart + 2
                while (i < bufferEnd && buffer[i] != '\n') i++
                setToken(DrlTokenTypes.LINE_COMMENT, i)
            }
            c == '/' && peek(1) == '*' -> {
                var i = tokenStart + 2
                while (i < bufferEnd - 1 && !(buffer[i] == '*' && buffer[i + 1] == '/')) i++
                i = if (i < bufferEnd - 1) i + 2 else bufferEnd
                setToken(DrlTokenTypes.BLOCK_COMMENT, i)
            }
            c == '"' -> setToken(DrlTokenTypes.STRING, scanString('"'))
            c == '\'' -> setToken(DrlTokenTypes.STRING, scanString('\''))
            c.isDigit() -> setToken(DrlTokenTypes.NUMBER, scanNumber())
            isIdentifierStart(c) -> {
                val end = scanIdentifierWithHyphens()
                val text = buffer.subSequence(tokenStart, end).toString()
                val type = if (DrlKeywords.SIMPLE.contains(text) || DrlKeywords.HYPHENATED.contains(text))
                    DrlTokenTypes.KEYWORD
                else
                    DrlTokenTypes.IDENTIFIER
                setToken(type, end)
            }
            c == '@' -> {
                var i = tokenStart + 1
                while (i < bufferEnd && (buffer[i].isLetterOrDigit() || buffer[i] == '_')) i++
                setToken(DrlTokenTypes.ANNOTATION, i)
            }
            c == '(' -> setToken(DrlTokenTypes.LPAREN, tokenStart + 1)
            c == ')' -> setToken(DrlTokenTypes.RPAREN, tokenStart + 1)
            c == '{' -> setToken(DrlTokenTypes.LBRACE, tokenStart + 1)
            c == '}' -> setToken(DrlTokenTypes.RBRACE, tokenStart + 1)
            c == '[' -> setToken(DrlTokenTypes.LBRACKET, tokenStart + 1)
            c == ']' -> setToken(DrlTokenTypes.RBRACKET, tokenStart + 1)
            c == ',' -> setToken(DrlTokenTypes.COMMA, tokenStart + 1)
            c == ';' -> setToken(DrlTokenTypes.SEMICOLON, tokenStart + 1)
            c == '.' -> setToken(DrlTokenTypes.DOT, tokenStart + 1)
            c == ':' -> setToken(DrlTokenTypes.COLON, if (peek(1) == ':') tokenStart + 2 else tokenStart + 1)
            isOperatorChar(c) -> setToken(DrlTokenTypes.OPERATOR, scanOperator())
            else -> setToken(TokenType.BAD_CHARACTER, tokenStart + 1)
        }
    }

    private fun setToken(type: IElementType, end: Int) {
        currentToken = type
        tokenEnd = end
    }

    private fun peek(offset: Int): Char? {
        val idx = tokenStart + offset
        return if (idx < bufferEnd) buffer[idx] else null
    }

    private fun isWhitespace(c: Char) = c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == ''

    private fun isIdentifierStart(c: Char) = c.isLetter() || c == '_' || c == '$'

    private fun isIdentifierPart(c: Char) = c.isLetterOrDigit() || c == '_' || c == '$'

    private fun isOperatorChar(c: Char) = c in "=+-*/%<>!&|^~?#"

    private fun scanString(quote: Char): Int {
        var i = tokenStart + 1
        while (i < bufferEnd) {
            val ch = buffer[i]
            if (ch == '\\' && i + 1 < bufferEnd) {
                i += 2
                continue
            }
            if (ch == quote) {
                i++
                break
            }
            if (ch == '\n') break // unterminated string literal: stop at end of line
            i++
        }
        return i
    }

    private fun scanNumber(): Int {
        var i = tokenStart
        while (i < bufferEnd && buffer[i].isDigit()) i++
        if (i < bufferEnd && buffer[i] == '.' && i + 1 < bufferEnd && buffer[i + 1].isDigit()) {
            i++
            while (i < bufferEnd && buffer[i].isDigit()) i++
        }
        if (i < bufferEnd && (buffer[i] == 'e' || buffer[i] == 'E')) {
            var j = i + 1
            if (j < bufferEnd && (buffer[j] == '+' || buffer[j] == '-')) j++
            if (j < bufferEnd && buffer[j].isDigit()) {
                i = j
                while (i < bufferEnd && buffer[i].isDigit()) i++
            }
        }
        if (i < bufferEnd && buffer[i] in "LlfFdD") i++
        return i
    }

    /**
     * Scans a plain identifier, then greedily tries to extend across
     * `-word` segments (for "no-loop", "agenda-group", ...), backing off to
     * the plain identifier if the extended text never matches a known
     * hyphenated keyword — so `x - y` (subtraction) is never swallowed into
     * one bogus token.
     */
    private fun scanIdentifierWithHyphens(): Int {
        var end = tokenStart + 1
        while (end < bufferEnd && isIdentifierPart(buffer[end])) end++

        var lastGoodEnd = end
        var cursor = end
        while (cursor < bufferEnd && buffer[cursor] == '-' &&
            cursor + 1 < bufferEnd && isIdentifierStart(buffer[cursor + 1])
        ) {
            var next = cursor + 1
            while (next < bufferEnd && isIdentifierPart(buffer[next])) next++
            cursor = next
            val candidate = buffer.subSequence(tokenStart, cursor).toString()
            if (DrlKeywords.HYPHENATED.contains(candidate)) {
                lastGoodEnd = cursor
            }
        }
        return lastGoodEnd
    }

    private fun scanOperator(): Int {
        val two = if (tokenStart + 1 < bufferEnd) "" + buffer[tokenStart] + buffer[tokenStart + 1] else ""
        return if (two in MULTI_CHAR_OPERATORS) tokenStart + 2 else tokenStart + 1
    }

    companion object {
        private val MULTI_CHAR_OPERATORS = setOf(
            "==", "!=", "<=", ">=", "&&", "||", "->", "+=", "-=", "*=", "/=", "++", "--"
        )
    }
}
