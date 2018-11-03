package me.zyee.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.util.ui.JBUI;
import me.zyee.CustomPsiClass;
import me.zyee.MethodSelectInfoNode;
import me.zyee.SelectInfoNode;
import me.zyee.config.EasyMockSetting;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/11/1
 */
public class ParameterPanel extends JPanel {
    protected PsiElementList<CustomPsiClass> parameterList;
    protected PsiElementList<PsiMethod> returnType;
    private JTextArea textPane;
    private TabbedPaneWrapper tabbedPane;
    private Project project;
    private PsiClass returnTypeClass;
    private PsiMethod method;
    private MethodSelectInfoNode node;
    private SelectInfoNode returnTypeNode;
    private Set<String> contains;
    private Map<String, PsiClass> typeMap = new HashMap<>();

    public ParameterPanel(String beanName, Project project, PsiMethod method, Disposable disposable) {
        this.project = project;
        this.method = method;
        setLayout(new VerticalFlowLayout());
        tabbedPane = new TabbedPaneWrapper(disposable);
        PsiClass parent = PsiTreeUtil.getParentOfType(method, PsiClass.class, false);
        for (PsiTypeParameter typeParameter : parent.getTypeParameterList().getTypeParameters()) {
            String paramType = typeParameter.getName();
            PsiClassType[] types = typeParameter.getExtendsListTypes();
            if (null != types && types.length > 0) {
                typeMap.put(paramType, types[0].resolve());
            }
        }
        PsiTypeElement type = method.getReturnTypeElement();
        if (null != type) {
            if (typeMap.get(type.getText()) == null) {
                String className = type.getType().getCanonicalText();
                returnTypeClass = JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.projectScope(project));
            } else {
                returnTypeClass = typeMap.get(type.getText());
            }
        }

        parameterList = new PsiElementList<>(false);
        parameterList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        parameterList.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            List<CustomPsiClass> psiClasses = parameterList.getSelectedValuesList();
            for (CustomPsiClass psiClass : psiClasses) {
                if (psiClass.isInterface()) {
                    SelectInfoNode info = new SelectInfoNode(psiClass);
                    info.setContains(new HashSet<>());
                    node.setContains(new HashSet<>());
                    node.addParamNode(psiClass.getOrder(), info);
                }
            }
            textPane.setText(node.getPreview());
        }));
        JScrollPane listScrollPane = ScrollPaneFactory.createScrollPane(parameterList);
        listScrollPane.setPreferredSize(JBUI.size(500, 100));
        tabbedPane.addTab("Parameter", listScrollPane);
        returnType = new PsiElementList<>();
        returnType.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        returnType.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            List<PsiMethod> methods = returnType.getSelectedValuesList();
            returnTypeNode.calculateMethodSelected(methods);
            returnTypeNode.setContains(new HashSet<>());
            node.setContains(new HashSet<>());
            node.setReturnTypeDepNode(returnTypeNode);
            textPane.setText(node.getPreview());
        }));
        node = new MethodSelectInfoNode(beanName);
        node.setMethod(method);


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
                int index = parameterList.locationToIndex(event.getPoint());
                if (index >= 0) {
                    CustomPsiClass psiClass = parameterList.getModel().getElementAt(index);
                    if (!EasyMockSetting.getInstance().isInterfaceOnly() || psiClass.isInterface()) {
                        CodeDialog codeDialog = new CodeDialog(project, psiClass);
                        codeDialog.show();
                        SelectInfoNode info = codeDialog.getInfo();
                        if (null != info) {
                            node.addParamNode(psiClass.getOrder(), info);
                            node.setContains(contains);
                            textPane.setText(node.getPreview());
                        }

                    }
                    return true;
                }
                return false;
            }
        }).installOn(this.parameterList);

        (new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                int index = returnType.locationToIndex(event.getPoint());
                if (index >= 0) {
                    PsiMethod psiMethod = returnType.getModel().getElementAt(index);
                    ParameterDlg dlg = new ParameterDlg(beanName, project, psiMethod);
                    dlg.setContains(contains);
                    dlg.showDialog();
                    MethodSelectInfoNode methodNode = dlg.getNode();
                    if (methodNode != null) {
                        returnTypeNode.addMethodSelectInfoNode(psiMethod, methodNode);
                        node.setReturnTypeDepNode(returnTypeNode);
                    }
                    textPane.setText(node.getPreview());
                    return true;
                }
                return false;
            }
        }).installOn(this.returnType);
    }

    public void setContains(Set<String> contains) {
        this.contains = contains;

    }

    public void loadData() {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        List<CustomPsiClass> classes = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            PsiParameter parameter = parameters[i];
            String type = parameter.getTypeElement().getText();
            PsiClass psiClass = null;
            if ((psiClass = typeMap.get(type)) == null) {
                String className = parameter.getType().getCanonicalText();
                int index = className.indexOf("<");
                if (index > 0) {
                    className = className.substring(0, index);
                }
                psiClass = PsiType.getTypeByName(className, project, GlobalSearchScope.allScope(project)).resolve();
            }
            if (null != psiClass) {
                classes.add(new CustomPsiClass(psiClass, i));
            }
        }
        parameterList.addElements(classes);
        if (null != returnTypeClass) {
            returnTypeNode = new SelectInfoNode(returnTypeClass);
            returnType.addElements(returnTypeClass.getMethods());
        }
    }

    public MethodSelectInfoNode getNode() {
        return node;
    }
}
