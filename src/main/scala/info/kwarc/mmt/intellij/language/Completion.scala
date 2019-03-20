package info.kwarc.mmt.intellij.language

import com.intellij.codeInsight.completion._
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.ProcessingContext
import info.kwarc.mmt.intellij.MMT

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
  private lazy val mmtO = MMT.getMMT
  lazy val elements = mmtO match {
    case Some(mmt) =>
      val a = LookupElementBuilder.create("❚").withLookupString("""jMD""").withTailText("Module Delimiter")
      val b = LookupElementBuilder.create("❙").withLookupString("""jDD""").withTailText("Declaration Delimiter")
      val c = LookupElementBuilder.create("❘").withLookupString("""jOD""").withTailText("Object Delimiter")
      val rest = mmt.mmtjar.abbrevs.map { case (l, s) => LookupElementBuilder.create(s).withLookupString(l).withTailText(l) }
      a :: b :: c :: rest
    case _ => Nil
  }
  override def addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet): Unit = {
    elements.foreach(result.addElement)
  }
}