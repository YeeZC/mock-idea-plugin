package me.zyee.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import me.zyee.getter.PsiClassGetter;
import me.zyee.ui.PsiElementList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/7
 */
public class TestCaseDlg extends DialogWrapper implements PsiClassGetter {
    private final PsiClass basePsiClass;
    private PsiElementList<PsiClass> inheritorClasses;
    private PsiElementList<PsiField> mockFields;
    private PsiElementList<PsiMethod> testMethods;

    public TestCaseDlg(@Nullable Project project, @NotNull PsiClass psiClass) {
        super(project, true);
        this.basePsiClass = psiClass;
        this.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        List<PsiClass> classes = new ArrayList<>();
        classes.add(basePsiClass);
        classes.addAll(ClassInheritorsSearch.search(basePsiClass).findAll());
        mockFields = new PsiElementList<>();
        mockFields.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        inheritorClasses = new PsiElementList<>();
        inheritorClasses.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inheritorClasses.setElement(classes);
        inheritorClasses.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            PsiClass selected = inheritorClasses.getSelectedValue();
            if (null != selected) {
                SwingUtilities.invokeLater(() -> {
                            mockFields.setElement(selected.getFields());
                            testMethods.setElement(basePsiClass.getMethods());
                        }
                );
            }
        }));
        inheritorClasses.setSelectedIndex(0);
        testMethods = new PsiElementList<>();
        testMethods.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel panel = new JPanel(new VerticalFlowLayout());
        panel.add(new JBLabel("InheritorClasses:"));
        JScrollPane classPane = ScrollPaneFactory.createScrollPane(inheritorClasses);
        classPane.setPreferredSize(JBUI.size(500, 100));
        panel.add(classPane);
        panel.add(new JBLabel("MockFields:"));
        JScrollPane fieldsPane = ScrollPaneFactory.createScrollPane(mockFields);
        fieldsPane.setPreferredSize(JBUI.size(500, 100));
        panel.add(fieldsPane);
        panel.add(new JBLabel("TestMethods:"));
        JScrollPane methodsPane = ScrollPaneFactory.createScrollPane(testMethods);
        methodsPane.setPreferredSize(JBUI.size(500, 100));
        panel.add(methodsPane);
        return panel;
    }

    @Override
    public PsiClass get() {
        return null;
    }
}
