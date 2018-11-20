package info.kwarc.mmt.intellij


// import java.util

import com.intellij.ide.util.projectWizard.{ModuleBuilder, ModuleWizardStep, WizardContext}
import com.intellij.openapi.module.{JavaModuleType, ModuleType}
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
// import com.intellij.openapi.util
import com.intellij.platform.{ProjectTemplate, ProjectTemplatesFactory}
import javax.swing.Icon
import org.jetbrains.plugins.scala.project.template.ScalaModuleBuilder

class MMTProjectTemplatesFactory extends ProjectTemplatesFactory {
  def getGroups = Array("MMT")

  override def getGroupIcon(group: String) = MMT.icon

  def createTemplates(group: String, context: WizardContext): Array[ProjectTemplate] = {
    Array(new MMTProjectTemplate)
  }

}

object MMTProjectTemplate {
  val getName: String = "MMT"
  val getDescription: String = "MMT MathHub Project"
  val getIcon: Icon = MMT.icon
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
  val getNodeIcon: Icon = MMT.icon
}

class MathHubModuleType extends ModuleType[MathHubModuleBuilder](MathHubModule.id) {
  override val getName = MathHubModule.getName
  override val getDescription: String = MathHubModule.getDescription
  override def getNodeIcon(isOpened: Boolean): Icon = MathHubModule.getNodeIcon

  override def createModuleBuilder = new MathHubModuleBuilder
}

class MathHubModuleBuilder extends ModuleBuilder {
  override def setupRootModel(modifiableRootModel: ModifiableRootModel): Unit = {
    // modifiableRootModel.addContentEntry("")
    // super.setupRootModel(modifiableRootModel)
  }

  override def getModuleType: ModuleType[_ <: ModuleBuilder] = new MathHubModuleType

  override def createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array[ModuleWizardStep] = {
    /*
    List(
      new ModuleWizardStep {
        override def updateDataModel(): Unit = ???
        override def getComponent: JComponent = ???
      }
    ).toArray
    */
    super.createWizardSteps(wizardContext,modulesProvider)
  }
  /*
  import com.intellij.openapi.util.Pair
  import ui.Implicit._
  override def getSourcePaths: java.util.List[Pair[String, String]] = List(new Pair(getContentEntryPath,""))
  */

}