package me.zyee.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.util.ui.JBUI;
import me.zyee.SelectedInfo;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/1
 */
public class ParameterPanel extends JPanel {
    protected PsiElementList<PsiClass> list;
    protected PsiElementList<PsiMethod> returnType;
    private JTextArea textPane;
    private TabbedPaneWrapper tabbedPane;
    private Project project;
    private PsiClass returnTypeClass;
    private PsiMethod method;
    private Disposable disposable;

    public ParameterPanel(Project project, PsiMethod method, Disposable disposable) {
        this.project = project;
        this.method = method;
        this.disposable = disposable;
        setLayout(new VerticalFlowLayout());
        tabbedPane = new TabbedPaneWrapper(disposable);
        String className = method.getReturnType().getCanonicalText();
        returnTypeClass = JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.projectScope(project));
        list = new PsiElementList<>();
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane listScrollPane = ScrollPaneFactory.createScrollPane(list);
        listScrollPane.setPreferredSize(JBUI.size(500, 100));
        tabbedPane.addTab("Parameter", listScrollPane);

        returnType = new PsiElementList<>();
        returnType.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        returnType.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            List<PsiMethod> methods = returnType.getSelectedValuesList();
            SelectedInfo info = new SelectedInfo();
            info.setPsiClass(returnTypeClass);
            info.setMethods(methods);
            textPane.setText(info.toString());
        }));

        JScrollPane returnTypeScroll = ScrollPaneFactory.createScrollPane(returnType);
        returnTypeScroll.setPreferredSize(JBUI.size(500, 100));
        tabbedPane.addTab("ReturnType", returnTypeScroll);
        add(tabbedPane.getComponent());
        textPane = new JTextArea();
        textPane.setEditable(false);
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textPane);
        textScrollPane.setPreferredSize(JBUI.size(500, 100));
        add(new JLabel("Result:"));
        add(textScrollPane);

        (new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                int index = list.locationToIndex(event.getPoint());
                if (index >= 0) {
                    PsiClass psiClass = list.getModel().getElementAt(index);
                    if (null != psiClass && psiClass.isInterface()) {
                        CodeDialog codeDialog = new CodeDialog(project, psiClass);
                        codeDialog.show();
                        SelectedInfo info = codeDialog.getInfo();
                        if (info != null) {
                            textPane.append(info.toString());
                        }
                    }
                    return true;
                }
                return false;
            }
        }).installOn(this.list);

        (new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                int index = returnType.locationToIndex(event.getPoint());
                if (index >= 0) {
                    PsiMethod psiMethod = returnType.getModel().getElementAt(index);
                    ParameterDlg dlg = new ParameterDlg(project, psiMethod);
                    dlg.show();
                    String code = dlg.getCode();
                    textPane.append(code);
                    return true;
                }
                return false;
            }
        }).installOn(this.returnType);
    }


    public JTextArea getTextPane() {
        return textPane;
    }

    public void loadData() {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        List<PsiClass> classes = new ArrayList<>();
        for (PsiParameter parameter : parameters) {
            String className = parameter.getType().getCanonicalText();
            int index = className.indexOf("<");
            if (index > 0) {
                className = className.substring(0, index);
            }
            PsiClass psiClass = PsiType.getTypeByName(className, project, GlobalSearchScope.allScope(project)).resolve();
            if (null != psiClass) {
                classes.add(psiClass);
            }
        }
        list.addElements(classes);
        if (null != returnTypeClass) {
            returnType.addElements(returnTypeClass.getMethods());
        }
    }
}
