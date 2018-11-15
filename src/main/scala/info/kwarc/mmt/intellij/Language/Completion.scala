package info.kwarc.mmt.intellij.Language

import com.intellij.codeInsight.completion._
import com.intellij.codeInsight.lookup.{LookupElement, LookupElementBuilder}
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.ProcessingContext
import info.kwarc.mmt.api.utils.MMTSystem

class Completion extends CompletionContributor {
  // extend(CompletionType.BASIC,psiElement().withLanguage(MMTLanguage.INSTANCE),Abbreviations)
  override def fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet): Unit = {
    if (parameters!=null && parameters.getOriginalPosition !=null &&
      parameters.getOriginalPosition.getLanguage==MMTLanguage.INSTANCE) {
      ProgressManager.checkCanceled()
      Abbreviations.addCompletionVariants(parameters,new ProcessingContext,result)
    }
  }
}


object Abbreviations extends CompletionProvider[CompletionParameters] {
  lazy val pairstrings = MMTSystem.getResourceAsString("latex/unicode-latex-map").split("\n")
  lazy val pairs = pairstrings.collect{ case s if s.nonEmpty =>
    val ls = s.split('|')
    if (ls.length != 2) {
      println(ls.mkString(", "))
      ???
    }
    ( /*"""\""" + */ ls.head.trim/*.drop(1)*/,ls.last.trim)
  }.filterNot(p => List("❚","❙","❘").contains(p._2))
  lazy val elements = {
    val a = LookupElementBuilder.create("❚").withLookupString("""jMD""").withTailText("Module Delimiter")
    val b = LookupElementBuilder.create("❙").withLookupString("""jDD""").withTailText("Declaration Delimiter")
    val c = LookupElementBuilder.create("❘").withLookupString("""jOD""").withTailText("Object Delimiter")
    val rest = pairs.map { case (l, s) => LookupElementBuilder.create(s).withLookupString(l).withTailText(l) }
    a :: b :: c :: rest.toList
  }
  override def addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet): Unit = {
    elements.foreach(result.addElement)
  }
}