package dev.leoluzh.drlsupport.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType

/**
 * Minimal, intentionally flat parser: it wraps the whole token stream in a
 * single root node instead of building a real DRL syntax tree. Editing
 * intelligence (completion, diagnostics, navigation, hover, rename) is
 * provided by the Drools Language Server through LSP4IJ (see the `lsp`
 * package), which has the real ANTLR4-based Drools grammar. This parser
 * exists only so the platform has a well-formed PSI file to hand out for
 * file-type registration, commenting and brace matching.
 */
class DrlParser : PsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        while (!builder.eof()) {
            builder.advanceLexer()
        }
        rootMarker.done(root)
        return builder.treeBuilt
    }
}
