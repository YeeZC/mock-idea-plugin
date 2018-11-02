package me.zyee.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.ui.JBUI;
import me.zyee.SelectedInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/1
 */
public class CodeDialog extends DialogWrapper {
    private PsiClass psiClass;
    private SelectedInfo info;
    private CodePanel codePanel;

    protected CodeDialog(@Nullable Project project, PsiClass psiClass) {
        super(project, false);
        this.psiClass = psiClass;
        info = new SelectedInfo();
        info.setPsiClass(psiClass);
        setSize(510, 310);
        this.init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new VerticalFlowLayout());
        codePanel = new CodePanel(psiClass.getQualifiedName()) {
            @Override
            protected PsiClass getPsiClass() {
                return psiClass;
            }
        };
        codePanel.setSize(JBUI.size(510, 300));
        codePanel.loadClass();
        panel.add(codePanel);
        return panel;
    }

    @Override
    protected void doOKAction() {
        List<PsiMethod> methods = ((CodePanel) getContentPanel()).list.getSelectedValuesList();
        info.setMethods(methods);
        super.doOKAction();
    }


    public SelectedInfo getInfo() {
        return info;
    }
}
