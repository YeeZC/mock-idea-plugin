package me.zyee.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import me.zyee.ui.dialog.TestCaseDlg;

/**
 * @author yee
 * @date 2018/11/7
 */
public class EasyMockTestCaseGenerator extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        PsiFile file = e.getData(LangDataKeys.PSI_FILE);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(file.getContext(), PsiClass.class);
        TestCaseDlg dlg = new TestCaseDlg(project, psiClass);
        dlg.show();
        PsiClass result = dlg.get();

    }
}
