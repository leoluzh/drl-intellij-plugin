package dev.leoluzh.drlsupport.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import dev.leoluzh.drlsupport.DrlFileType
import dev.leoluzh.drlsupport.DrlLanguage

class DrlFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, DrlLanguage) {
    override fun getFileType(): FileType = DrlFileType
    override fun toString(): String = "DRL File"
}
