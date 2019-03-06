package info.kwarc.mmt.intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.structuralsearch.plugin.ui.TextFieldWithAutoCompletionWithBrowseButton;
import com.intellij.ui.EditorTextFieldWithBrowseButton;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;

public class GeneralizerToolWindow {
    private Project project;

    private JPanel panel;
    private TextFieldWithAutoCompletionWithBrowseButton partTheoryS;
    private TextFieldWithAutoCompletionWithBrowseButton generalizedTheoryOfPartG;
    private TextFieldWithAutoCompletionWithBrowseButton inputTheoryT;
    private JButton generalizeButton;

    public GeneralizerToolWindow(Project project) {
        this.project = project;
    }

    public JPanel getContent() {
        return panel;
    }

    public void createUIComponents() {
        // TODO Create own TheoryChooser akin to PackageChooserDialog
        inputTheoryT = new TextFieldWithAutoCompletionWithBrowseButton(project);
        partTheoryS = new TextFieldWithAutoCompletionWithBrowseButton(project);
        generalizedTheoryOfPartG = new TextFieldWithAutoCompletionWithBrowseButton(project);

        Collection<String> theories = Arrays.asList("theory1", "theory2");
        Collection<String> incomingMorphisms = Arrays.asList("G -> theory1", "G -> theory");

        inputTheoryT.setAutoCompletionItems(theories);
        partTheoryS.setAutoCompletionItems(theories);
        generalizedTheoryOfPartG.setAutoCompletionItems(incomingMorphisms);
    }
}
