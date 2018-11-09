package me.zyee.ui.dialog;

import com.intellij.concurrency.AsyncFuture;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import me.zyee.DataProcessors;
import me.zyee.getter.PsiClassGetter;
import me.zyee.ui.PsiElementComboBox;
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
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/11/7
 */
public class TestCaseDlg extends DialogWrapper implements PsiClassGetter {
    private final PsiClass basePsiClass;
    private static final int FUTURE_WAIT_TIMEOUT = 5;
    private PsiElementList<PsiField> mockFields;
    private PsiElementList<PsiMethod> testMethods;
    private final Module module;
    private PsiElementComboBox<PsiClass> inheritor;
    private TextFieldWithBrowseButton packageField;
    private PsiPackage selectedPackage;

    public TestCaseDlg(@Nullable Module module, @NotNull PsiClass psiClass, PsiPackage defaultPackage) {
        super(module.getProject(), true);
        this.basePsiClass = psiClass;
        this.module = module;
        this.selectedPackage = defaultPackage;
        this.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        List<PsiClass> classes = new ArrayList<>();
        classes.add(basePsiClass);
        AsyncFuture<Boolean> future = ClassInheritorsSearch.search(basePsiClass).forEachAsync(DataProcessors.uniqueCheckProcessor(classes, element -> !(element instanceof PsiAnonymousClass)));
        mockFields = new PsiElementList<>();
        mockFields.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        inheritor = new PsiElementComboBox<>();
        inheritor.addItemListener(e -> SwingUtilities.invokeLater(() -> {
            PsiClass selected = (PsiClass) inheritor.getSelectedItem();
            if (null != selected) {
                SwingUtilities.invokeLater(() -> {
                            mockFields.setElement(selected.getFields());
                            testMethods.setElement(basePsiClass.getMethods());
                        }
                );
            }
        }));

        testMethods = new PsiElementList<>();
        testMethods.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel panel = new JPanel(new VerticalFlowLayout());
        panel.add(new JBLabel("InheritorClasses:"));
        panel.add(inheritor);
        packageField = new TextFieldWithBrowseButton(new JBTextField(), e -> {
            PackageChooserDialog dialog = new PackageChooserDialog("Select Package", module);
            dialog.show();
            if (dialog.isOK()) {
                selectedPackage = dialog.getSelectedPackage();
                packageField.setText(selectedPackage.getQualifiedName());
            }
        });
        packageField.setText(selectedPackage.getQualifiedName());
        panel.add(new JBLabel("Packages:"));
        panel.add(packageField);
        panel.add(new JBLabel("MockFields:"));
        JScrollPane fieldsPane = ScrollPaneFactory.createScrollPane(mockFields);
        fieldsPane.setPreferredSize(JBUI.size(500, 100));
        panel.add(fieldsPane);
        panel.add(new JBLabel("TestMethods:"));
        JScrollPane methodsPane = ScrollPaneFactory.createScrollPane(testMethods);
        methodsPane.setPreferredSize(JBUI.size(500, 100));
        panel.add(methodsPane);
        try {
            if (future.get(FUTURE_WAIT_TIMEOUT, TimeUnit.SECONDS)) {
                inheritor.setData(classes);
                inheritor.setSelectedIndex(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return panel;
    }

    @Override
    public PsiClass get() {
        return null;
    }
}
