package info.kwarc.mmt.intellij

import java.awt.FlowLayout
import java.net.URLClassLoader

import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.projectWizard.{ModuleBuilder, ModuleWizardStep, WizardContext}
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.ui.{TextBrowseFolderListener, TextFieldWithBrowseButton}
import com.intellij.openapi.vfs.VirtualFile
import javax.swing._
import com.intellij.platform.{ProjectTemplate, ProjectTemplatesFactory}
import info.kwarc.mmt.utils.{File, Reflection}

class MMTProjectTemplatesFactory extends ProjectTemplatesFactory {
  def getGroups = Array("MMT")

  override def getGroupIcon(group: String) = MMT.projectIcon

  def createTemplates(group: String, context: WizardContext): Array[ProjectTemplate] = {
    Array(new MMTProjectTemplate)
  }

}

object MMTProjectTemplate {
  val getName: String = "MMT"
  val getDescription: String = "MMT MathHub Project"
  val getIcon: Icon = MMT.projectIcon
}


class MMTProjectTemplate extends ProjectTemplate {
  val getName: String = MMTProjectTemplate.getName
  val getDescription: String = MMTProjectTemplate.getDescription
  val getIcon: Icon = MMTProjectTemplate.getIcon

  def createModuleBuilder() = new MathHubModuleBuilder()

  def validateSettings() = null
}

object MathHubModule {
  val id = "mmt-mh"
  val getName = "MMT MathHub Module"
  val getDescription: String = "A content folder for MMT archives"
  val getNodeIcon: Icon = MMT.nodeIcon
}

class MathHubModuleType extends ModuleType[MathHubModuleBuilder](MathHubModule.id) {
  override val getName = MathHubModule.getName
  override val getDescription: String = MathHubModule.getDescription
  override def getNodeIcon(isOpened: Boolean): Icon = MathHubModule.getNodeIcon

  override def createModuleBuilder = new MathHubModuleBuilder
}

class MathHubModuleBuilder extends ModuleBuilder {
  private var valid : Option[String] = None
  override def setupRootModel(modifiableRootModel: ModifiableRootModel): Unit = {
    assert(valid.isDefined)
    PropertiesComponent.getInstance(modifiableRootModel.getProject).setValue(MMTDataKeys.mmtjar,valid.get)
    modifiableRootModel.getProject.save()
  }

  override def getModuleType: ModuleType[_ <: ModuleBuilder] = new MathHubModuleType

  override def createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array[ModuleWizardStep] = {
    List(
      new ModuleWizardStep {
        val panel = new JPanel()
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS))
        val ipanel = new JPanel(new FlowLayout(FlowLayout.LEFT))
        panel.add(ipanel)
        ipanel.add(new JLabel("Select mmt.jar:"))
        val filefield = new TextFieldWithBrowseButton()
        ipanel.add(filefield)
        val isok = new JLabel("")
        ipanel.add(isok)
        val link = new JTextPane()
        link.setContentType("text/html")
        link.setText("""(Download latest version at <a href="https://github.com/UniFormal/MMT/releases/latest">https://github.com/UniFormal/MMT/releases/latest</a>)""")
        panel.add(link)
        panel.updateUI()

        val invalid_string = "Please select a valid mmt.jar (requires " + MMT.requiredVersion + " or higher)"

        override def validate() = valid.isDefined || {
          throw new com.intellij.openapi.options.ConfigurationException(invalid_string)
        }

        object Descriptor extends FileChooserDescriptor(true,false,true,true,false,false) {
          override def isFileSelectable(file: VirtualFile): Boolean = file.getName == "mmt.jar"
        }

        filefield.addBrowseFolderListener(new TextBrowseFolderListener(Descriptor) {
          override def onFileChosen(chosenFile: VirtualFile): Unit = {
            super.onFileChosen(chosenFile)
            val file = File(chosenFile.getCanonicalPath)
            val loader = new URLClassLoader(Array(file.toURI.toURL))
            try {
              val ref = new Reflection(loader)
              val shcls = ref.getClass("info.kwarc.mmt.api.frontend.Shell")
              val sh = shcls.getInstance(Nil)
              val ctrlcls = ref.getClass("info.kwarc.mmt.api.frontend.Controller")
              val ctrl = sh.method("controller",Reflection.Reflected(ctrlcls),Nil)
              val v = Version(ctrl.method("getVersion",Reflection.string,Nil))
/*
              val ctrlcls = loader.loadClass("info.kwarc.mmt.api.frontend.Controller")
              val ctrl = ctrlcls.getConstructor().newInstance()
              val v = Version(ctrlcls.getMethod("getVersion").invoke(ctrl).asInstanceOf[String])
*/
              if (MMT.requiredVersion <= v) {
                isok.setText("Version: " + v)
                valid = Some(chosenFile.getCanonicalPath)
              } else {
                isok.setText(invalid_string)
                valid = None
              }
            } catch {
              case _ : ClassNotFoundException =>
                isok.setText(invalid_string)
                valid = None
            }
          }
        })
        override def updateDataModel(): Unit = { }
        override def getComponent: JComponent = panel
      }
    ).toArray
  }

}