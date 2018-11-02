package me.zyee.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.JBUI;
import me.zyee.MethodSelectInfoNode;
import me.zyee.SelectInfoNode;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/1
 */
public abstract class CodePanel extends JPanel {
    protected PsiElementList<PsiMethod> list;
    private JTextArea textPane;
    private SelectInfoNode node;

    public CodePanel(Project project, String label) {
        setLayout(new VerticalFlowLayout());
        list = new PsiElementList<>();
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            List<PsiMethod> methods = list.getSelectedValuesList();
            node.getContains().clear();
            node.calculateMethodSelected(methods);
            textPane.setText(node.getCode());
        }));

        JScrollPane listScrollPane = ScrollPaneFactory.createScrollPane(list);
        listScrollPane.setPreferredSize(JBUI.size(500, 100));
        add(new JLabel(label));
        add(listScrollPane);
        textPane = new JTextArea();
        textPane.setEditable(false);
        textPane.setText("Generate Code");
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textPane);
        textScrollPane.setPreferredSize(JBUI.size(500, 100));
        add(new JLabel("Result:"));
        add(textScrollPane);
        (new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                int index = list.locationToIndex(event.getPoint());
                if (index >= 0) {
                    PsiMethod psiMethod = list.getModel().getElementAt(index);
                    ParameterDlg dlg = new ParameterDlg("mock" + getPsiClass().getName(), project, psiMethod);
                    dlg.setContains(node.getContains());
                    dlg.showDialog();
                    MethodSelectInfoNode methodSelectInfoNode = dlg.getNode();
                    if (null != methodSelectInfoNode) {
                        node.addMethodSelectInfoNode(psiMethod, methodSelectInfoNode);
                        node.getContains().clear();
                        getTextPane().setText(node.getCode());
                    }
                    return true;
                }
                return false;
            }
        }).installOn(list);
    }

    public JTextArea getTextPane() {
        return textPane;
    }

    public void loadClass() {
        loadClass(getPsiClass());
    }

    private void loadClass(PsiClass psiClass) {
        node = new SelectInfoNode(psiClass);
        node.setContains(new HashSet<>());
        PsiMethod[] methods = psiClass.getMethods();
        list.addElements(methods);
        textPane.setText(node.getCode());
    }

    protected abstract PsiClass getPsiClass();

    public SelectInfoNode getNode() {
        return node;
    }
}
