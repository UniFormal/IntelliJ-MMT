package info.kwarc.mmt.intellij.ui

import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import javax.swing.JPanel
import info.kwarc.mmt.intellij.util._

trait MMTToolWindow {
  val panel : JPanel
  val displayName : String
  def init(tw : ToolWindow) = {
    val content = ContentFactory.SERVICE.getInstance().createContent(panel,displayName,false)
    background {
      panel.setVisible(true)
      tw.getContentManager.addContent(content)
    }
  }
}