package info.kwarc.mmt.intellij.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.structuralsearch.plugin.ui.TextFieldWithAutoCompletionWithBrowseButton;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import info.kwarc.mmt.intellij.language.MMTFileType;
import info.kwarc.mmt.intellij.ui.generalizer.ScalaGeneralizerToolWindowHelper;
import info.kwarc.mmt.intellij.ui.generalizer.TreeUtils;
import scala.runtime.BoxedUnit;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

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

    public GeneralizerToolWindow(final Project project, final ScalaGeneralizerToolWindowHelper scalaHelper) {
        this.project = project;
        $$$setupUI$$$();
        generalizeButton.addActionListener(e -> scalaHelper.generalize(
                generalizedTheoryOfPartG.getText(),
                partTheoryS.getText(),
                generalizationMorphism.getText(),
                inputTheoryT.getText()
        ));
        insertGeneralizedCodeButton.addActionListener(e -> insertGeneralizedCodeInCurrentEditorAtCursor());
    }

    private void insertGeneralizedCodeInCurrentEditorAtCursor() {
        FileEditorManager manager = FileEditorManager.getInstance(project);
        final Editor selectedEditor = manager.getSelectedTextEditor();
        assert selectedEditor != null;
        final int cursorOffset = selectedEditor.getCaretModel().getOffset();
        final Document selectedDocument = selectedEditor.getDocument();

        WriteCommandAction.runWriteCommandAction(project, () -> selectedDocument.insertString(cursorOffset, generalizedCode.getText()));
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
        //
        /*
        inputTheoryT.setText("http://cds.omdoc.org/theorysplittest/generalization/metricAndNormedSpaces?NormedVectorspaceThms");
        partTheoryS.setText("http://cds.omdoc.org/theorysplittest/generalization/metricAndNormedSpaces?NormedVectorspace");
        generalizedTheoryOfPartG.setText("http://cds.omdoc.org/theorysplittest/generalization/metricAndNormedSpaces?MetricSpace");
        generalizationMorphism.setText("http://cds.omdoc.org/theorysplittest/generalization/metricAndNormedSpaces?NormedAsMetricSpace");
        */

        /*Collection<String> theories = Arrays.asList("theory1", "theory2");
        Collection<String> incomingMorphisms = Arrays.asList("G -> theory1", "G -> theory");

        inputTheoryT.setAutoCompletionItems(theories);
        partTheoryS.setAutoCompletionItems(theories);
        generalizedTheoryOfPartG.setAutoCompletionItems(incomingMorphisms);*/

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
                MMTFileType.INSTANCE,          // file type
                true                   // readonly
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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Input theory");
        panel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Part to be generalized (directly included theory or same as input theory)");
        panel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Generalization of that part (theory)");
        panel.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(150);
        panel1.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPane1.setLeftComponent(scrollPane1);
        scrollPane1.setViewportView(errorTree);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(panel2);
        panel2.add(generalizedCode, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        insertGeneralizedCodeButton = new JButton();
        insertGeneralizedCodeButton.setEnabled(false);
        insertGeneralizedCodeButton.setText("insert >>");
        insertGeneralizedCodeButton.setToolTipText("Insert generalized theory code in current editor at cursor position.");
        panel2.add(insertGeneralizedCodeButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel.add(separator1, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel.add(generalizedTheoryOfPartG, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(10, 17), null, 0, false));
        panel.add(inputTheoryT, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel.add(partTheoryS, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        generalizeButton = new JButton();
        generalizeButton.setText("Generalize");
        panel.add(generalizeButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Specialization morphism (view)");
        panel.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(generalizationMorphism, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(10, 17), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
