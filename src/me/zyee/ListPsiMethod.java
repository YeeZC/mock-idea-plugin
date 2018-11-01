package me.zyee;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmPrimitiveType;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author yee
 * @date 2018/11/1
 */
public class ListPsiMethod extends JCheckBox {
    private PsiMethod psiMethod;
    private String method;

    public ListPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
        StringBuffer buffer = new StringBuffer();
        String methodName = psiMethod.getName();
        PsiType returnType = psiMethod.getReturnType();
        buffer.append(psiMethod.getModifierList()).append(" ");
        if (null != returnType) {
            buffer.append(returnType);
        }
        buffer.append(" ").append(methodName).append("( ");
        for (JvmParameter parameter : psiMethod.getParameters()) {

            buffer.append(parameter.getType()).append(" ").append(parameter.getName()).append(",");
        }
        buffer.setCharAt(buffer.length() - 1, ')');
        buffer.append(" ");
        for (JvmReferenceType throwsType : psiMethod.getThrowsTypes()) {
            buffer.append(throwsType).append(",");
        }
        buffer.setLength(buffer.length() - 1);
        method = buffer.toString();
        method = method.replace("PsiType:", "").trim();
        method = method.replace("PsiModifierList:", "").trim();
    }

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return method;
    }

    @Override
    public String getName() {
        return psiMethod.getName();
    }

    @Override
    public String getText() {
        return method;
    }
}
