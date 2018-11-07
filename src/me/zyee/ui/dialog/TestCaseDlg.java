package me.zyee.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import me.zyee.getter.PsiClassGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * @author yee
 * @date 2018/11/7
 */
public class TestCaseDlg extends DialogWrapper implements PsiClassGetter {
    private final PsiClass basePsiClass;

    public TestCaseDlg(@Nullable Project project, @NotNull PsiClass psiClass) {
        super(project, true);
        this.basePsiClass = psiClass;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return null;
    }

    @Override
    public PsiClass get() {
        return null;
    }
}
