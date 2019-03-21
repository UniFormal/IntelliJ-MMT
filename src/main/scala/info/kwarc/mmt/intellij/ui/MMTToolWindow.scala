package info.kwarc.mmt.intellij.ui

import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import javax.swing.JPanel

trait MMTToolWindow {
  val panel: JPanel
  val displayName: String

  def init(tw: ToolWindow): Unit = {
    val content = ContentFactory.SERVICE.getInstance().createContent(panel, displayName, false)
    // background {
    panel.setVisible(true)
    tw.getContentManager.addContent(content)
    // }
  }
}
