package me.zyee.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiClass;
import com.intellij.util.ui.JBUI;
import me.zyee.SelectInfoNode;
import me.zyee.ui.panel.CodePanel;
import me.zyee.ui.panel.PanelBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author yee
 * @date 2018/11/1
 */
public class CodeDlg extends DialogWrapper {
    private PsiClass psiClass;
    private SelectInfoNode info;
    private CodePanel codePanel;
    private Project project;

    public CodeDlg(@Nullable Project project, PsiClass psiClass) {
        super(project, false);
        this.psiClass = psiClass;
        this.project = project;
        setSize(510, 310);
        this.init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new VerticalFlowLayout());
        codePanel = PanelBuilder.buildCodePanel(project, psiClass, psiClass.getQualifiedName());
        codePanel.setSize(JBUI.size(510, 300));
        codePanel.loadClass();
        panel.add(codePanel);
        return panel;
    }

    @Override
    protected void doOKAction() {
        info = codePanel.getNode();
        super.doOKAction();
    }


    public SelectInfoNode getInfo() {
        return info;
    }
}
