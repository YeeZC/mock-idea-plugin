package me.zyee.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author yee
 * @date 2018/11/2
 */
public class ParameterDlg extends DialogWrapper {

    private PsiMethod method;
    private Project project;
    private ParameterPanel parameterPanel;

    protected ParameterDlg(@Nullable Project project, PsiMethod method) {
        super(project, true);
        this.project = project;
        this.method = method;
        this.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new VerticalFlowLayout());
        parameterPanel = new ParameterPanel(project, method, getDisposable());
        parameterPanel.loadData();
        panel.add(parameterPanel);
        return panel;
    }

    public String getCode() {
        return parameterPanel.getTextPane().getText();
    }
}
