package info.kwarc.mmt

import java.awt.Color

import com.intellij.notification.{Notification, NotificationType, Notifications}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.{Balloon, JBPopupFactory}
import com.intellij.openapi.vfs.{LocalFileSystem, VirtualFile, VirtualFileManager}
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiFile
import com.intellij.ui.awt.RelativePoint
import info.kwarc.mmt.intellij.MMT
import org.jetbrains.plugins.scala.project.migration.api.MigrationReport.MessageType

import scala.concurrent.Future

/**
  * This package defines various MMT-independent high-level APIs.
  */
package object utils {
   /** converts a string to an integer, returns None on format exception */
   def stringToInt(s: String) = try {Some(s.toInt)} catch {case _: Exception => None}
   /** matches a string as an integer */
   object NumericString {
     def unapply(s: String) = stringToInt(s)
   }

   /** splits a string at a separator (returns Nil for the empty string) */
   def stringToList(s: String, sep: String = "\\s") = s.split(sep).toList match {
      case List("") => Nil
      case l => l
   }

   /** string index modulo string length */
   def moduloLength(s:String, i: Int) = {
     if (i >= 0) i else i+s.length
   }
   def charAt(s: String, i: Int) = {
     val iA = moduloLength(s, i)
     if (iA < s.length) Some(s(i)) else None
   }
   /** substring of a string given by begin and end, negative indices allowed */
   def substringFromTo(s: String, from: Int, to: Int) = {
     s.substring(moduloLength(s,from), moduloLength(s,to))
   }
   /** substring of a string given by begin and length, negative index allowed */
   def substringFrom(s: String, from: Int, length: Int) = substringFromTo(s, from, from+length)

   /** splits a string at whitespace, quoted segments may contain whitespace, \" for quote, ignores leading/trailing whitespace */
   def splitAtWhitespace(s: String): List[String] = {
      var segments : List[String] = Nil
      var current = ""
      var inquoted = false
      var todo = s
      def done {
        if (current.nonEmpty) {
          segments ::= current
          current = ""
        }
      }
      while (todo.nonEmpty) {
        if (todo.startsWith("\\\"")) {
          current += '"'
          todo = todo.substring(2)
        } else {
          val c = todo(0)
          todo  = todo.substring(1)
          if (inquoted) {
            if (c == '"') {
              done
              inquoted = false
            } else {
              current += c
            }
          } else {
            if (c == '"') {
              inquoted = true
            } else if (c.isWhitespace) {
              done
            } else {
              current += c
            }
          }
        }
      }
      done
      segments.reverse
   }

  /** a pair of an open and a closing bracket */
  case class Bracket(begin: String, end: String) {
    def wrapAround(s: String) = begin + s + end
    val beginL = begin.length
    val endL = end.length
  }

   /** turns a list into a string by inserting a separator */
   def listToString[A](l: Iterable[A], sep: String) = l.map(_.toString).mkString(sep)

   /** repeats a strings a number of times, optionally with a separator */
   def repeatString(s: String, n: Int, sep: String = "") = Range(0,n).map(_ => s).mkString(sep)

   /** inserts a separator element in between all elements of a list */
   def insertSep[A](l: List[A], sep: A): List[A] = if (l.isEmpty) Nil else l.flatMap(a => List(sep,a)).tail

   /** applies a list of pairs seen as a map */
   def listmap[A,B](l: Iterable[(A,B)], a: A): Option[B] = l.find(_._1 == a).map(_._2)
   
   /** like map, but the map function knows what previous values produced */
   def mapInContext[A,B](l: Iterable[A])(f: (List[(A,B)],A) => B) : List[B] = {
     var sofar: List[(A,B)] = Nil
     l foreach {a =>
       sofar = sofar ::: List((a, f(sofar, a)))
     }
     sofar.map(_._2)
   }

   /** disjointness of two lists (fast if first argument is empty) */
   def disjoint[A](l: Seq[A], m: Seq[A]) = l.forall(a => ! m.contains(a))

   /** variant of fold such that associate(List(a), unit)(comp) = a instead of comp(unit, a) */
   def associate[A](l: List[A], unit: A)(comp: (A,A) => A): A = l match {
     case Nil => unit
     case hd::tl => tl.fold(hd)(comp)
   }

   /** checks if a list has duplicates */
   def hasDuplicates[A](l: Iterable[A]): Boolean = {
     val seen = new scala.collection.mutable.HashSet[A]
     l.foreach {a =>
       if (seen contains a)
         return true
       else
         seen += a
     }
     false
   }

   /** slurps an entire stream into a string */
   def readFullStream(is: java.io.InputStream) = scala.io.Source.fromInputStream(is, "UTF-8").mkString

   /** a cast function that allows only casting into a subtype and returns None if the cast fails */
   def downcast[A, B<:A](cls: Class[B])(a: A): Option[B] = a match {
     case b: B@unchecked if cls.isInstance(b) => Some(b)
     case _ => None
   }

   /** returns a histogram of pf over lst, i.e. counts how often each return value occurs */
   def histogram[T1, T2](lst: Seq[T1], pf: PartialFunction[T1, T2]): Seq[(T2, Int)] = {
     lst.groupBy(pf.lift).collect({
       case (Some(e), l) => (e, l.size)
     }).toSeq
   }

  def resource(f : String) = getClass.getResource("/" + f)
  def writable[A](fun : => A) : A = // background {
    ApplicationManager.getApplication.runWriteAction[A]{() => fun }
  // }
  def readable[A](fun : => A) : A = ApplicationManager.getApplication.runReadAction[A]{() => fun}
  def background(f: => Unit) = Future { f }(scala.concurrent.ExecutionContext.global)// ApplicationManager.getApplication.invokeLater{() => f}// Future { f }
  def errorMsg(f: => Unit)(implicit project : Project) : Unit = errorMsg(f,())
  def errorMsg [A](f : => A, orElse : => A)(implicit project : Project) : A = try {
    f
  } catch {
    case e : Exception =>
      Messages.showErrorDialog(project,e.getMessage + "\n\n" + e.getStackTrace.toList.mkString("\n"),"MMT Error")
      orElse
  }
  def inotify(message : String, title : String = "MMT", exp : Int = 0) = {
    /*
    val not = new Notification("MMT",title,message,NotificationType.INFORMATION)
    Notifications.Bus.notify(not)
    not
    */
    val statusbar = WindowManager.getInstance().getStatusBar(MMT.getProject.get)
    val builder = JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(message,MMT.icon,Color.GRAY,null)
    val ball = if (exp == 0) builder.createBalloon() else builder.setFadeoutTime(exp).createBalloon()
    ball.show(RelativePoint.getCenterOf(statusbar.getComponent),Balloon.Position.atRight)
    ball
  }
  def inotifyP(message : String, title : String = "MMT", exp : Int = 0) = {

    val not = new Notification("MMT",title,message,NotificationType.INFORMATION)
    ApplicationManager.getApplication.invokeLater{ () => Notifications.Bus.notify(not) }
    not

  }
  def notifyWhile[A](message : String, title : String = "MMT")(fun : => A) : A = {
    val not = inotify(message,title)
    val ret = fun
    not.dispose()
    ret
  }
  def notifyWhileP[A](message : String, title : String = "MMT")(fun : => A) : A = {
    val not = inotifyP(message,title)
    val ret = fun
    ApplicationManager.getApplication.invokeLater{ () => not.expire() }
    inotifyP("Done.").expire()
    ret
  }

  lazy val vfm = VirtualFileManager.getInstance()
  lazy val fs = vfm.getFileSystem("file")

  def toVF(f : File): VirtualFile = { /*
    val vf = fs.findFileByPath(f.toFilePath.toString())
    if (vf != null) vf
    else {
      println(f.toFilePath)
      ???
    } */
    LocalFileSystem.getInstance().findFileByIoFile(f)
  }
  def toFile(f : PsiFile): File = File(f.getVirtualFile.getCanonicalPath)

  import java.util.Collections
  implicit def toJava[A](ls : List[A]) : java.util.List[A] = {
    val empty = new java.util.ArrayList[A]()
    Collections.addAll(empty,ls:_*)
    empty
  }
  implicit def toScala[A](ls : java.util.Collection[A]) : List[A] = ls.toArray().asInstanceOf[Array[A]].toList

  def getResource(path: String): java.io.InputStream = {
    if(!path.startsWith("/")){ return getResource("/" + path) }
    getClass.getResourceAsStream(path)
  }
  def getResourceAsString(path: String): String = Option(getResource(path)) match {
    case Some(r) => utils.readFullStream(r)
    case None => throw new Error(s"cannot find resource $path")
  }
}
