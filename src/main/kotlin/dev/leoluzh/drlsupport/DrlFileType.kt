package dev.leoluzh.drlsupport

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object DrlFileType : LanguageFileType(DrlLanguage) {
    override fun getName(): String = "DRL File"
    override fun getDescription(): String = DrlBundle.message("filetype.description")
    override fun getDefaultExtension(): String = "drl"
    override fun getIcon(): Icon = DrlIcons.FILE
}
