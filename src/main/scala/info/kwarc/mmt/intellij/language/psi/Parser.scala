package info.kwarc.mmt.intellij.language.psi

import com.intellij.lang.Language
import com.intellij.psi.tree.IElementType
import info.kwarc.mmt.intellij.language.MMTLanguage

class MMTElementType(debugName : String) extends IElementType(debugName,MMTLanguage.INSTANCE)

class MMTTokenType(debugName : String) extends IElementType(debugName,MMTLanguage.INSTANCE)