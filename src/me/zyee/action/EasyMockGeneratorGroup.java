package me.zyee.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
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
        PsiFile file = e.getData(LangDataKeys.PSI_FILE);
        new WriteCommandAction.Simple(project, file) {
            @Override
            protected void run() {
                Editor editor = e.getData(CommonDataKeys.EDITOR);
                CommandProcessor.getInstance().executeCommand(editor.getProject(), () -> replace(editor, dialog.getCode()), null, null, UndoConfirmationPolicy.DEFAULT, editor.getDocument());
            }
        }.execute();
    }

    private void replace(Editor editor, String code) {
        editor.getCaretModel().runForEachCaret(caret -> {
            EditorModificationUtil.deleteSelectedText(editor);
            int caretOffset = editor.getCaretModel().getOffset();
            int offset = insertLookupInDocument(caretOffset, editor.getDocument(), code);
            editor.getCaretModel().moveToOffset(offset);
            editor.getSelectionModel().removeSelection();
        });
    }

    private int insertLookupInDocument(int caretOffset, Document document, String lookupString) {
        int lookupStart = Math.min(caretOffset, Math.max(caretOffset, 0));
        document.replaceString(lookupStart, caretOffset, lookupString);
        return lookupStart + lookupString.length();
    }
}
