package info.kwarc.mmt.intellij.language

import java.nio.charset.Charset

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.{FileTypeConsumer, FileTypeFactory, LanguageFileType}
import com.intellij.openapi.vfs.VirtualFile
import info.kwarc.mmt.intellij.MMT
import javax.swing.Icon

object MMTFile extends LanguageFileType(MMTLanguage.INSTANCE) {
  override def getName: String = "MMT"

  override def getDefaultExtension: String = "mmt"

  override def getIcon: Icon = MMT.icon

  // override def getCharset(file: VirtualFile, content: Array[Byte]): String = Charset.defaultCharset().name()

  override def getDescription: String = "MMT Document"

  override def isReadOnly: Boolean = false
}
/*
object MMTFile {
  val INSTANCE = new MMTFile
}
*/
class MMTFileFactory extends FileTypeFactory {
  override def createFileTypes(consumer: FileTypeConsumer): Unit = {
    consumer.consume(MMTFile)
  }
}

/*
class MMTExperimentalFile extends MMTFile {
  override def getDefaultExtension: String = "mmtx"

  override def getDescription: String = "MMT Experimental File"

  override def getName: String = "MMT Exp File"
}

object MMTExperimentalFile {
  val INSTANCE = new MMTExperimentalFile
}


class MMTXFileFactory extends FileTypeFactory {
  override def createFileTypes(consumer: FileTypeConsumer): Unit = {
    consumer.consume(MMTExperimentalFile.INSTANCE)
  }
}
*/