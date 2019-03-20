package info.kwarc.mmt.intellij.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.structuralsearch.plugin.ui.TextFieldWithAutoCompletionWithBrowseButton;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.treeStructure.Tree;
import info.kwarc.mmt.intellij.language.MMTFile$;
import info.kwarc.mmt.intellij.ui.generalizer.ScalaGeneralizerToolWindowHelper;
import info.kwarc.mmt.intellij.ui.generalizer.TreeUtils;
import scala.runtime.BoxedUnit;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Arrays;
import java.util.Collection;

public class GeneralizerToolWindow {
    private final Project project;

    private JPanel panel;
    private TextFieldWithAutoCompletionWithBrowseButton partTheoryS;
    private TextFieldWithAutoCompletionWithBrowseButton generalizedTheoryOfPartG;
    private TextFieldWithAutoCompletionWithBrowseButton inputTheoryT;
    private JButton generalizeButton;
    private Tree errorTree;
    private DefaultTreeModel errorTreeModel;
    private DefaultMutableTreeNode errorTreeRootNode;
    private EditorTextField generalizedCode;
    private TextFieldWithAutoCompletionWithBrowseButton generalizationMorphism;
    private JButton insertGeneralizedCodeButton;

    private final ScalaGeneralizerToolWindowHelper scalaHelper;

    private class ScrollableMultilineEditorTextField extends EditorTextField {
        public ScrollableMultilineEditorTextField(Document document, Project project, FileType fileType, boolean isViewer) {
            super(document, project, fileType, isViewer, false);
        }

        @Override
        protected EditorEx createEditor() {
            EditorEx editor = super.createEditor();
            editor.setVerticalScrollbarVisible(true);
            editor.setHorizontalScrollbarVisible(true);
            editor.setCaretEnabled(true);

            return editor;
        }
    }

    public GeneralizerToolWindow(final Project project, final ScalaGeneralizerToolWindowHelper scalaHelper) {
        this.project = project;
        this.scalaHelper = scalaHelper;
        generalizeButton.addActionListener(e -> {
            scalaHelper.generalize(
                    generalizedTheoryOfPartG.getText(),
                    partTheoryS.getText(),
                    generalizationMorphism.getText(),
                    inputTheoryT.getText()
            );
        });
        insertGeneralizedCodeButton.addActionListener(e -> {
            insertGeneralizedCodeInCurrentEditorAtCursor();
        });
    }

    private void insertGeneralizedCodeInCurrentEditorAtCursor() {
        FileEditorManager manager = FileEditorManager.getInstance(project);
        final Editor selectedEditor = manager.getSelectedTextEditor();
        assert selectedEditor != null;
        final int cursorOffset = selectedEditor.getCaretModel().getOffset();
        final Document selectedDocument = selectedEditor.getDocument();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            selectedDocument.insertString(cursorOffset, generalizedCode.getText());
        });
    }

    public JPanel getContent() {
        return panel;
    }

    public void createUIComponents() {
        // TODO Create own TheoryChooser akin to PackageChooserDialog
        inputTheoryT = new TextFieldWithAutoCompletionWithBrowseButton(project);
        partTheoryS = new TextFieldWithAutoCompletionWithBrowseButton(project);
        generalizedTheoryOfPartG = new TextFieldWithAutoCompletionWithBrowseButton(project);
        generalizationMorphism = new TextFieldWithAutoCompletionWithBrowseButton(project);

        // Default values for faster debugging :-)
        inputTheoryT.setText("http://cds.omdoc.org/theorysplittest/generalization/metricAndNormedSpaces?NormedVectorspaceThms");
        partTheoryS.setText("http://cds.omdoc.org/theorysplittest/generalization/metricAndNormedSpaces?NormedVectorspace");
        generalizedTheoryOfPartG.setText("http://cds.omdoc.org/theorysplittest/generalization/metricAndNormedSpaces?MetricSpace");
        generalizationMorphism.setText("http://cds.omdoc.org/theorysplittest/generalization/metricAndNormedSpaces?NormedAsMetricSpace");

        Collection<String> theories = Arrays.asList("theory1", "theory2");
        Collection<String> incomingMorphisms = Arrays.asList("G -> theory1", "G -> theory");

        inputTheoryT.setAutoCompletionItems(theories);
        partTheoryS.setAutoCompletionItems(theories);
        generalizedTheoryOfPartG.setAutoCompletionItems(incomingMorphisms);

        errorTreeRootNode = new DefaultMutableTreeNode("Generalization Errors");

        errorTreeModel = new DefaultTreeModel(errorTreeRootNode);
        errorTree = new Tree(errorTreeModel);

        TreeUtils.addDoubleClickListenerToTree(errorTree, path -> {
            System.out.println(path.toString());

            return BoxedUnit.UNIT;
        });

        generalizedCode = new ScrollableMultilineEditorTextField(
                EditorFactory.getInstance().createDocument(""),
                project,
                MMTFile$.MODULE$,  // file type
                true       // readonly
        );
    }

    public DefaultMutableTreeNode getErrorTreeRootNode() {
        return errorTreeRootNode;
    }

    public void setGeneralizedCode(String code) {
        generalizedCode.setText(code);
        insertGeneralizedCodeButton.setEnabled(true);
    }

    public void refreshErrorTree() {
        errorTreeModel.reload();
        errorTree.invalidate();
    }
}
