package me.zyee.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import me.zyee.MethodSelectInfoNode;
import me.zyee.SelectInfoNode;
import me.zyee.ui.dialog.GeneratorDlg;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yee
 * @date 2018/10/31
 */
public class MockGeneratorGroup extends AnAction {
    private SelectInfoNode node;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = getEventProject(e);

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (null != editor) {
            Document document = editor.getDocument();
            if (!document.isWritable()) {
                Messages.showErrorDialog("Attempt to modify read-only document", "ReadOnly File");
                return;
            }

            PsiFile file = e.getData(LangDataKeys.PSI_FILE);
            if (file instanceof PsiJavaFile) {
                PsiCodeBlock codeBlock = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), PsiCodeBlock.class);
                if (null == codeBlock) {
                    Messages.showErrorDialog("Generate code out of code block is not supported", "Code Block");
                    return;
                }
                GeneratorDlg dialog = new GeneratorDlg("", project);
                dialog.showDialog();
                node = dialog.getNode();
                String code;
                if (null != node && null != (code = node.getPreview())) {
                    WriteAction.run(() ->
                            CommandProcessor.getInstance().executeCommand(
                                    editor.getProject(),
                                    () -> replace(editor, code, (PsiJavaFile) file),
                                    null,
                                    null,
                                    UndoConfirmationPolicy.DEFAULT,
                                    editor.getDocument()));
                }
            } else {
                Messages.showErrorDialog("Only Java Files are supported", "Java File");
            }
        }
    }


    private void replace(Editor editor, String value, PsiJavaFile file) {
        EditorModificationUtil.deleteSelectedText(editor);
        int caretOffset = editor.getCaretModel().getOffset();
        Document document = editor.getDocument();
        document.replaceString(caretOffset, caretOffset, value);
        int offset = caretOffset + value.length();
        PsiElement element = file.findElementAt(caretOffset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class, false);
        editor.getCaretModel().moveToOffset(offset);
        PsiDocumentManager manager = PsiDocumentManager.getInstance(editor.getProject());
        manager.commitDocument(document);
        // import class
        Set<PsiClass> imported = new HashSet<>();
        PsiImportStatement[] statements = file.getImportList().getImportStatements();
        for (PsiImportStatement statement : statements) {
            PsiClass psiClass = PsiType.getTypeByName(statement.getQualifiedName(), editor.getProject(), GlobalSearchScope.allScope(editor.getProject())).resolve();
            imported.add(psiClass);
        }
        PsiClass easymock = PsiType.getTypeByName("org.easymock.EasyMock", editor.getProject(), GlobalSearchScope.allScope(editor.getProject())).resolve();
        PsiClass control = PsiType.getTypeByName("org.easymock.IMocksControl", editor.getProject(), GlobalSearchScope.allScope(editor.getProject())).resolve();
        importClass(file, imported, easymock);
        importClass(file, imported, control);
        importNode(file, imported, node);
        PsiClass testCodeClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
//        testCodeClass.add()
        PsiClass runWith = PsiType.getTypeByName("org.junit.runner.RunWith", editor.getProject(), GlobalSearchScope.allScope(editor.getProject())).resolve();
        PsiClass powerMockRunner = PsiType.getTypeByName("org.powermock.modules.junit4.PowerMockRunner", editor.getProject(), GlobalSearchScope.allScope(editor.getProject())).resolve();
        if (null != powerMockRunner) {

        }
        CodeStyleManager.getInstance(editor.getProject()).reformat(file.getImportList());

        // format code
        CodeStyleManager.getInstance(editor.getProject()).reformat(method);
    }

    private void importClass(PsiJavaFile file, Set<PsiClass> contains, PsiClass importClass) {
        if (null != importClass && !contains.contains(importClass)) {
            file.importClass(importClass);
            contains.add(importClass);
        }
    }

    private void importNode(PsiJavaFile file, Set<PsiClass> contains, SelectInfoNode node) {
        if (null != node) {
            PsiClass psiClass = node.getPsiClass();
            importClass(file, contains, psiClass);
            for (MethodSelectInfoNode value : node.getMethods().values()) {
                for (SelectInfoNode selectInfoNode : value.getParamDepNode().values()) {
                    importNode(file, contains, selectInfoNode);
                }
                importNode(file, contains, value.getReturnTypeDepNode());
            }
        }
    }
}
