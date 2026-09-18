package dev.leoluzh.drlsupport.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import dev.leoluzh.drlsupport.DrlBundle
import dev.leoluzh.drlsupport.DrlIcons
import javax.swing.Icon

/**
 * Lets the user customize DRL colors under Settings/Preferences → Editor →
 * Color Scheme → DRL (Drools). The demo text below is tokenized by the real
 * [DrlSyntaxHighlighter], so no manual `<TAG>` markup is needed.
 */
class DrlColorSettingsPage : ColorSettingsPage {

    override fun getIcon(): Icon = DrlIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = DrlSyntaxHighlighter()

    override fun getDemoText(): String = """
        package com.example.rules

        import com.example.model.Order;

        // Sample rule
        rule "Order overdue"
            no-loop true
            salience 10
            agenda-group "billing"
        when
            ${'$'}o : Order(dueDate < today, status == "OPEN")
        then
            ${'$'}o.setStatus("OVERDUE");
            update(${'$'}o);
        end
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = DrlBundle.message("colorSettings.displayName")

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor(DrlBundle.message("color.keyword"), DrlSyntaxHighlighter.KEYWORD),
            AttributesDescriptor(DrlBundle.message("color.identifier"), DrlSyntaxHighlighter.IDENTIFIER),
            AttributesDescriptor(DrlBundle.message("color.string"), DrlSyntaxHighlighter.STRING),
            AttributesDescriptor(DrlBundle.message("color.number"), DrlSyntaxHighlighter.NUMBER),
            AttributesDescriptor(DrlBundle.message("color.comment"), DrlSyntaxHighlighter.COMMENT),
            AttributesDescriptor(DrlBundle.message("color.operator"), DrlSyntaxHighlighter.OPERATOR),
            AttributesDescriptor(DrlBundle.message("color.annotation"), DrlSyntaxHighlighter.ANNOTATION),
            AttributesDescriptor(DrlBundle.message("color.parentheses"), DrlSyntaxHighlighter.PARENTHESES),
            AttributesDescriptor(DrlBundle.message("color.braces"), DrlSyntaxHighlighter.BRACES),
            AttributesDescriptor(DrlBundle.message("color.brackets"), DrlSyntaxHighlighter.BRACKETS),
            AttributesDescriptor(DrlBundle.message("color.comma"), DrlSyntaxHighlighter.COMMA),
            AttributesDescriptor(DrlBundle.message("color.semicolon"), DrlSyntaxHighlighter.SEMICOLON),
            AttributesDescriptor(DrlBundle.message("color.dot"), DrlSyntaxHighlighter.DOT),
        )
    }
}
