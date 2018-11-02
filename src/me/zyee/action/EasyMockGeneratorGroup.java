package me.zyee.action;

import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.injected.editor.DocumentWindow;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import me.zyee.ui.GeneratorDlg;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.java.generate.GenerateToStringWorker;
import org.jetbrains.java.generate.GenerationUtil;
import org.jetbrains.java.generate.velocity.VelocityFactory;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

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
        WriteAction.run(() -> CommandProcessor.getInstance().executeCommand(editor.getProject(), () -> EasyMockGeneratorGroup.this.replace(editor, dialog.getValue(), file), null, null, UndoConfirmationPolicy.DEFAULT, editor.getDocument()));
    }


    private void replace(Editor editor, String value, PsiFile file) {

        EditorModificationUtil.deleteSelectedText(editor);
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(caretOffset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class, false);
        Document document = editor.getDocument();
        document.replaceString(caretOffset, caretOffset, value);
        int offset =  caretOffset + value.length();
        editor.getCaretModel().moveToOffset(offset);
        PsiDocumentManager manager = PsiDocumentManager.getInstance(editor.getProject());
        manager.commitDocument(document);
        CodeStyleManager.getInstance(editor.getProject()).reformat(method);
    }

}
