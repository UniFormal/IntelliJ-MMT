package info.kwarc.mmt.intellij.checking

import com.intellij.lang.annotation.{AnnotationHolder, ExternalAnnotator}
import com.intellij.notification.{Notification, NotificationType}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import info.kwarc.mmt.intellij.MMT
import info.kwarc.mmt.utils
import info.kwarc.mmt.utils.{File, Reflection, URI}

class ExtAnnotator extends ExternalAnnotator[Option[(MMT,Editor)],Option[(MMT,Editor)]] {

  override def apply(psifile: PsiFile, mmtO: Option[(MMT,Editor)], holder: AnnotationHolder): Unit = mmtO match {
    case Some((mmt, editor)) if mmt.errorViewer.doCheck =>
      val mmtjar = mmt.mmtjar
      object Jar {
        private val cls = mmtjar.reflection.getClass("info.kwarc.mmt.intellij.checking.Checker")
        private val checker = mmtjar.method("checker", Reflection.Reflected(cls), Nil)

        // private val checkerclass = mmtjar.classLoader.loadClass("info.kwarc.mmt.intellij.checking.Checker")
        // private val jarchecker = mmtjar.method("checker")
        def check(uri: URI, text: String,
                  clearFile: String => Unit,
                  note: (String, String) => Unit,
                  errorCont: (Int, Int, String, String, List[String]) => Unit
                 ) = {
          val cf = new Reflection.RFunction {
            def apply(v1: String): Unit = clearFile(v1)
          }
          val nt = new Reflection.RFunction {
            def apply(v1: String, v2: String): Unit = note(v1, v2)
          }
          val ec = new Reflection.RFunction {
            def apply(v1: Int, v2: Int, v3: String, v4: String, v5: List[String]): Unit =
              errorCont(v1, v2, v3, v4, v5)
          }
          try {
            checker.method("check", Reflection.unit, List(uri.toString, text, cf, nt, ec))
          } catch {
            case e : Exception =>
              utils.inotifyP(e.getStackTrace.mkString("\n"),title="MMT Error: " + e.getClass + ": " + e.getMessage,exp=5000,level=NotificationType.ERROR)
              ApplicationManager.getApplication.invokeLater { () => mmt.reset() }
            case e : Error =>
              utils.inotifyP(e.getStackTrace.mkString("\n"),title="MMT Error: " + e.getClass + ": " + e.getMessage,exp=5000,level=NotificationType.ERROR)
              ApplicationManager.getApplication.invokeLater { () => mmt.reset() }
          }
        }

      }
      var not: Option[Notification] = None
      val uri = URI(psifile.getVirtualFile.toString)
      val text = psifile.getText
      val clearFile: String => Unit = { f =>
        mmt.errorViewer.clearFile(File(f))
      }
      val note: (String, String) => Unit = {
        case (str, file) =>
          not match {
            case Some(n) => n.expire() //.dispose()
            case _ =>
          }
          if (str.startsWith("Done: ")) {
            not = None
            utils.inotifyP("Done: " + str.split('/').last, exp = 3000)
            mmt.logged("Sidekick...") {
              editor.getCaretModel.addCaretListener(mmt.sidekick)
              mmt.sidekick.setFile(psifile)
              mmt.sidekick.doDoc(str.drop(6))
            }
          } else try {
            not = Some(utils.inotifyP(str))
          } catch {
            case _: ArrayIndexOutOfBoundsException | _: IllegalArgumentException => // TODO why is this necessary??
          }
        /*
        not = Some(new Notification("MMT",File(file).name,str,NotificationType.INFORMATION))
        Notifications.Bus.notify(not.get)
        if (str == "Done.") {
          Thread.sleep(1000)
          not.get.expire()
          not = None
        }
        */
      }
      val error: (Int, Int, String, String, List[String]) => Unit = {
        case (start, length, file, main, extra) =>
          val tr = TextRange.from(psifile.getTextRange.getStartOffset + start, length)
          val int = psifile.getTextRange.intersection(tr)
          val region = if (int != null) int else psifile.getTextRange
          if (main.startsWith("Warning")) {
            holder.createWarningAnnotation(region, main)
          } else {
            holder.createErrorAnnotation(region, main)
            mmt.errorViewer.addError(main, extra, psifile, File(file), region)
          }
      }
      mmt.logged("Checking " + uri) {
        Jar.check(uri, text, clearFile, note, error)
      }
    case _ =>
  }

  override def collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): Option[(MMT,Editor)] = {
    Some((MMT.get(editor.getProject).getOrElse(return None),editor))
  }

  override def doAnnotate(collectedInfo: Option[(MMT,Editor)]): Option[(MMT,Editor)] = {
    collectedInfo
  }

}