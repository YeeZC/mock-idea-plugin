package me.zyee.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiMethod;
import me.zyee.MethodSelectInfoNode;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yee
 * @date 2018/11/2
 */
public class ParameterDlg extends DialogWrapper {

    private PsiMethod method;
    private Project project;
    private ParameterPanel parameterPanel;
    private String beanName;
    private MethodSelectInfoNode node;
    private Set<String> contains;

    protected ParameterDlg(String beanName, @Nullable Project project, PsiMethod method) {
        super(project, true);
        this.project = project;
        this.method = method;
        this.beanName = beanName;
    }

    public void setContains(Set<String> contains) {
        this.contains = contains;
    }

    public void showDialog() {
        this.init();
        show();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new VerticalFlowLayout());
        parameterPanel = new ParameterPanel(beanName, project, method, getDisposable());
        parameterPanel.setContains(contains);
        parameterPanel.loadData();
        panel.add(parameterPanel);
        return panel;
    }

    public MethodSelectInfoNode getNode() {
        node.setContains(new HashSet<>());
        return node;
    }

    @Override
    protected void doOKAction() {
        node = parameterPanel.getNode();
        super.doOKAction();
    }
}
