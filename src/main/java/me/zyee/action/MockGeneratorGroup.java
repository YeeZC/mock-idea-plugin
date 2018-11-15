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
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import me.zyee.MethodSelectInfoNode;
import me.zyee.SelectInfoNode;
import me.zyee.config.MockSetting;
import me.zyee.format.CodeFormat;
import me.zyee.ui.dialog.GeneratorDlg;

import java.util.HashSet;
import java.util.List;
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
                    Set<PsiClass> staticMockSet = node.getStaticMockSet();
                    WriteCommandAction.writeCommandAction(project).run(() ->
                            CommandProcessor.getInstance().executeCommand(
                                    editor.getProject(),
                                    () -> replace(editor, code, staticMockSet, (PsiJavaFile) file),
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


    private void replace(Editor editor, String value, Set<PsiClass> staticMockSet, PsiJavaFile file) {
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
        CodeFormat codeFormat = MockSetting.getInstance().getCodeFormat();
        for (PsiClass psiClass : codeFormat.importList(editor.getProject())) {
            importClass(file, imported, psiClass);
        }
        importNode(file, imported, node);
        annotation(editor, staticMockSet, file, imported, codeFormat);
        CodeStyleManager.getInstance(editor.getProject()).reformat(file.getImportList());
        // format code
        CodeStyleManager.getInstance(editor.getProject()).reformat(method);
    }

    private void annotation(Editor editor, Set<PsiClass> staticMockSet, PsiJavaFile file, Set<PsiClass> imported, CodeFormat codeFormat) {
        if (!staticMockSet.isEmpty()) {
            PsiClass parent = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), PsiClass.class);
            List<PsiClass> annotations = codeFormat.annotationClasses(editor.getProject());
            if (null != parent && !annotations.isEmpty()) {
                for (PsiClass annotation : annotations) {
                    importClass(file, imported, annotation);
                }
                PsiDocComment docComment = parent.getDocComment();
                List<PsiAnnotation> psiAnnotations = codeFormat.psiAnnotations(editor.getProject(), staticMockSet);
                PsiModifierList modifierList = parent.getModifierList();
                if (null != docComment) {
                    for (PsiAnnotation psiAnnotation : psiAnnotations) {
                        if (null == modifierList || null == modifierList.findAnnotation(psiAnnotation.getQualifiedName())) {
                            parent.addAfter(psiAnnotation, docComment);
                        }
                    }
                } else {
                    for (PsiAnnotation psiAnnotation : psiAnnotations) {
                        if (null == modifierList || null == modifierList.findAnnotation(psiAnnotation.getQualifiedName())) {
                            parent.addBefore(psiAnnotation, parent);
                        }
                    }
                }
            }
        }
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
