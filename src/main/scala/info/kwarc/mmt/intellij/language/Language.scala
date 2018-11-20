package info.kwarc.mmt.intellij.language

import com.intellij.lang.Language

object MMTLanguage {
  final val INSTANCE = new MMTLanguage
}
class MMTLanguage extends Language("MMT")