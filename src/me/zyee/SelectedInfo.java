package me.zyee;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

import java.util.List;

/**
 * @author yee
 * @date 2018/11/1
 */
public class SelectedInfo {
    private PsiClass psiClass;
    private List<PsiMethod> methods;

    public SelectedInfo() {
    }

    public void setPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public void setMethods(List<PsiMethod> methods) {
        this.methods = methods;
    }

    @Override
    public String toString() {
        String className = psiClass.getName();
        String beanName = className.toLowerCase().charAt(0) + className.substring(1);
        String text = String.format("%s %s = EasyMock.createMock(%s.class);\n", className, beanName, className);
        StringBuffer buffer = new StringBuffer(text);
        if (null != methods) {
            for (PsiMethod method : methods) {
                buffer.append(String.format("EasyMock.expect(%s.%s( ", beanName, method.getName()));
                for (JvmParameter parameter : method.getParameters()) {
                    buffer.append(parameter.getName()).append(",");
                }
                buffer.setCharAt(buffer.length() - 1, ')');
                buffer.append(")");
                PsiElement type = method.getReturnTypeElement();
                if (null != type && !"void".equals(type.getText())) {
                    buffer.append(String.format(".addReturn(%s)", type.getText()));
                }
                buffer.append(";\n");
            }
        }
        buffer.append(String.format("EasyMock.replay(%s);\n", beanName));
        return buffer.toString();
    }
}
