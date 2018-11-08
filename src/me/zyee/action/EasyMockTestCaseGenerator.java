package me.zyee.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import me.zyee.ui.dialog.TestCaseDlg;
import org.jetbrains.annotations.NotNull;

/**
 * @author yee
 * @date 2018/11/7
 */
public class EasyMockTestCaseGenerator extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        PsiFile file = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), PsiClass.class);
        TestCaseDlg dlg = new TestCaseDlg(project, calPsiClass(psiClass));
        dlg.show();
        PsiClass result = dlg.get();

    }

    @NotNull
    private PsiClass calPsiClass(PsiClass psiClass) {
        if (psiClass instanceof PsiAnonymousClass) {
            psiClass = calPsiClass(psiClass.getSuperClass());
        }
        return psiClass;
    }
}
