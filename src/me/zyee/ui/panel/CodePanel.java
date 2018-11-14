package me.zyee.ui.panel;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.util.ui.JBUI;
import me.zyee.DataProcessors;
import me.zyee.MethodSelectInfoNode;
import me.zyee.SelectInfoNode;
import me.zyee.config.Framework;
import me.zyee.config.MockSetting;
import me.zyee.ui.PsiElementList;
import me.zyee.ui.dialog.ParameterDlg;

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
    protected PsiElementList<PsiField> fieldList;
    private JTextArea textPane;
    private SelectInfoNode node;
    private TabbedPaneWrapper tabbedPane;
    private JScrollPane fieldsPanel;
    private JScrollPane listScrollPane;

    CodePanel(Project project, Disposable disposable) {
        setLayout(new VerticalFlowLayout());
        tabbedPane = new TabbedPaneWrapper(disposable);
        list = new PsiElementList<>();
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            List<PsiMethod> methods = list.getSelectedValuesList();
            node.getContains().clear();
            node.calculateMethodSelected(methods);
            textPane.setText(node.getPreview());
        }));

        listScrollPane = ScrollPaneFactory.createScrollPane(list);
        listScrollPane.setPreferredSize(JBUI.size(500, 100));
        tabbedPane.addTab("Methods", listScrollPane);
        fieldList = new PsiElementList<>();
        fieldList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        fieldList.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
//            List<PsiField> methods = fieldList.getSelectedValuesList();
//            node.getContains().clear();
//            node.calculateMethodSelected(methods);
//            textPane.setText(node.getPreview());
        }));
        fieldsPanel = ScrollPaneFactory.createScrollPane(fieldList);
        if (MockSetting.getInstance().getFramework() != Framework.EASYMOCK) {
            tabbedPane.addTab("Fields", fieldsPanel);
        }
        add(tabbedPane.getComponent());
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
                        getTextPane().setText(node.getPreview());
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
        PsiField[] fields = psiClass.getFields();
        if (!MockSetting.getInstance().isStaticMock()) {
            list.setData(DataProcessors.AccessList.of(methods).execute(element -> {
                PsiModifierList list = element.getModifierList();
                return !list.hasModifierProperty("static");
            }));
            fieldList.setData(DataProcessors.AccessList.of(fields).execute(element -> {
                PsiModifierList list = element.getModifierList();
                return !list.hasModifierProperty("static");
            }));
        } else {
            list.setData(methods);
            fieldList.setData(fields);
        }

        textPane.setText(node.getPreview());
    }

    protected abstract PsiClass getPsiClass();

    public SelectInfoNode getNode() {
        return node;
    }

}
