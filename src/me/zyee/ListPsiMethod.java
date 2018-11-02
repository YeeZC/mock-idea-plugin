package me.zyee;

import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

/**
 * @author yee
 * @date 2018/11/1
 */
public class ListPsiMethod {
    private PsiMethod psiMethod;
    private String display;

    public ListPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
        initMethodDisplayName();
    }

    private void initMethodDisplayName() {
        StringBuffer buffer = new StringBuffer();
        String methodName = psiMethod.getName();
        PsiElement returnType = psiMethod.getReturnTypeElement();
        if (null != returnType) {
            buffer.append(returnType.getText());
        }
        buffer.append(" ").append(methodName).append("( ");
        for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {

            buffer.append(parameter.getTypeElement().getText()).append(" ").append(parameter.getName()).append(",");
        }
        buffer.setCharAt(buffer.length() - 1, ')');
        buffer.append(" ");
        for (PsiClassType throwsType : psiMethod.getThrowsList().getReferencedTypes()) {
            buffer.append(throwsType.getName()).append(",");
        }
        buffer.setLength(buffer.length() - 1);
        display = buffer.toString();
    }

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    @Override
    public String toString() {
        return display;
    }

    public String getName() {
        return psiMethod.getName();
    }
}
