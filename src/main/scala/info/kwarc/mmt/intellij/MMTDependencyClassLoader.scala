package info.kwarc.mmt.intellij

import java.net.URLClassLoader

import info.kwarc.mmt.utils.File

object MMTDependencyClassLoader {
  def getFor(mmtSource: File): URLClassLoader = {
    val parentClassLoader = this.getClass.getClassLoader

    if (mmtSource.isFile) {
      new URLClassLoader(List(mmtSource.toURI.toURL).toArray, parentClassLoader)
    }
    else {
      // Treat as clone of MMT repo with compiled sources
      val unmanagedLibURLs = (mmtSource / "deploy")
        .children
        .filter(_.getExtension.contains("jar"))
        .map(_.toURI.toURL)

      val subprojectClassDirectoryURLs = (mmtSource / "src").subdirs.flatMap(subproject => {
        val targetDirectory = subproject / "target"
        if (!targetDirectory.exists()) {
          None
        }
        else {
          Some(targetDirectory / "scala-2.12" / "classes")
        }
      }).map(_.toURI.toURL)

      new URLClassLoader((unmanagedLibURLs ::: subprojectClassDirectoryURLs).toArray, parentClassLoader)
    }
  }
}
