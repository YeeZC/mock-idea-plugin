package me.zyee.action;

import com.intellij.application.options.editor.JavaAutoImportOptions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import me.zyee.ui.GeneratorDlg;

/**
 * @author yee
 * @date 2018/10/31
 */
public class EasyMockGeneratorGroup extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = getEventProject(e);
        GeneratorDlg dialog = new GeneratorDlg("", project);
        dialog.showDialog();
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiFile file = e.getData(LangDataKeys.PSI_FILE);
        String code = dialog.getNode().getCode();
        if (null != code && null != code) {
            WriteAction.run(() ->
                    CommandProcessor.getInstance().executeCommand(
                            editor.getProject(),
                            () -> replace(editor, code, file),
                            null,
                            null,
                            UndoConfirmationPolicy.DEFAULT,
                            editor.getDocument()));
        }
    }


    private void replace(Editor editor, String value, PsiFile file) {

        EditorModificationUtil.deleteSelectedText(editor);
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(caretOffset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class, false);
        Document document = editor.getDocument();
        document.replaceString(caretOffset, caretOffset, value);
        int offset = caretOffset + value.length();
        editor.getCaretModel().moveToOffset(offset);
        PsiDocumentManager manager = PsiDocumentManager.getInstance(editor.getProject());
        manager.commitDocument(document);
        CodeStyleManager.getInstance(editor.getProject()).reformat(method);
        JavaAutoImportOptions options = new JavaAutoImportOptions(editor.getProject());
        options.apply();
    }

}
