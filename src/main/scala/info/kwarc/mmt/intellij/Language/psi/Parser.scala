package info.kwarc.mmt.intellij.Language.psi

import com.intellij.lang.Language
import com.intellij.psi.tree.IElementType
import info.kwarc.mmt.intellij.Language.MMTLanguage

class MMTElementType(debugName : String) extends IElementType(debugName,MMTLanguage.INSTANCE)

class MMTTokenType(debugName : String) extends IElementType(debugName,MMTLanguage.INSTANCE)