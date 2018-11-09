package me.zyee.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;
import me.zyee.ui.dialog.TestCaseDlg;
import org.jetbrains.annotations.NotNull;

/**
 * @author yee
 * @date 2018/11/7
 */
public class EasyMockTestCaseGenerator extends AnAction {
    private JavaDirectoryService directoryService;

    public EasyMockTestCaseGenerator() {
        this.directoryService = JavaDirectoryService.getInstance();
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile file = e.getData(LangDataKeys.PSI_FILE);
        if (null != file && file instanceof PsiJavaFile) {
            PsiDirectory directory = file.getParent();
            Editor editor = e.getData(CommonDataKeys.EDITOR);
            PsiClass psiClass = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), PsiClass.class);
            if (null == psiClass) {
                return;
            }
            TestCaseDlg dlg = new TestCaseDlg(e.getData(LangDataKeys.MODULE), calPsiClass(psiClass), directoryService.getPackage(directory));
            dlg.show();
            PsiClass result = dlg.get();
        }
    }

    @NotNull
    private PsiClass calPsiClass(@NotNull PsiClass psiClass) {
        if (psiClass instanceof PsiAnonymousClass) {
            psiClass = calPsiClass(psiClass.getSuperClass());
        }
        return psiClass;
    }
}
