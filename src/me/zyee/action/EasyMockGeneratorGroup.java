package me.zyee.action;

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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import me.zyee.MethodSelectInfoNode;
import me.zyee.SelectInfoNode;
import me.zyee.ui.GeneratorDlg;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yee
 * @date 2018/10/31
 */
public class EasyMockGeneratorGroup extends AnAction {
    private SelectInfoNode node;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = getEventProject(e);
        GeneratorDlg dialog = new GeneratorDlg("", project);
        dialog.showDialog();
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiFile file = e.getData(LangDataKeys.PSI_FILE);
        node = dialog.getNode();
        String code;
        if (null != node && null != (code = node.getCode())) {
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
        Document document = editor.getDocument();
        document.replaceString(caretOffset, caretOffset, value);
        int offset = caretOffset + value.length();
        editor.getCaretModel().moveToOffset(offset);
        PsiDocumentManager manager = PsiDocumentManager.getInstance(editor.getProject());
        manager.commitDocument(document);
        // import class
        if (file instanceof PsiJavaFile) {
            Set<PsiClass> imported = new HashSet<>();
            PsiImportStatement[] statements = ((PsiJavaFile) file).getImportList().getImportStatements();
            for (PsiImportStatement statement : statements) {
                PsiClass psiClass = PsiType.getTypeByName(statement.getQualifiedName(), editor.getProject(), GlobalSearchScope.allScope(editor.getProject())).resolve();
                imported.add(psiClass);
            }
            PsiClass easymock = PsiType.getTypeByName("org.easymock.EasyMock", editor.getProject(), GlobalSearchScope.allScope(editor.getProject())).resolve();
            importClass((PsiJavaFile) file, imported, easymock);
            if (!imported.contains(node.getPsiClass())) {
                ((PsiJavaFile) file).importClass(node.getPsiClass());
                imported.add(node.getPsiClass());
            }
            importClass((PsiJavaFile) file, imported, node.getPsiClass());
            for (MethodSelectInfoNode methodNode : node.getMethods().values()) {
                for (SelectInfoNode infoNode : methodNode.getParamDepNode().values()) {
                    PsiClass psiClass = infoNode.getPsiClass();
                    importClass((PsiJavaFile) file, imported, psiClass);
                }
            }
        }

        // format code
        CodeStyleManager.getInstance(editor.getProject()).reformat(file);
    }

    private void importClass(PsiJavaFile file, Set<PsiClass> contains, PsiClass importClass) {
        if (null != importClass && !contains.contains(importClass)) {
            file.importClass(importClass);
            contains.add(importClass);
        }
    }

}
