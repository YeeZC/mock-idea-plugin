package me.zyee.ui.panel;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import me.zyee.getter.PsiClassGetter;

/**
 * @author yee
 * @date 2018/11/7
 */
public class PanelBuilder {
    public static CodePanel buildCodePanel(Project project, PsiClass psiClass, Disposable disposable) {
        return new CodePanel(project, disposable) {
            @Override
            protected PsiClass getPsiClass() {
                return psiClass;
            }
        };
    }

    public static CodePanel buildCodePanel(Project project, PsiClassGetter callable, Disposable disposable) {
        return new CodePanel(project, disposable) {
            @Override
            protected PsiClass getPsiClass() {
                return callable.get();
            }
        };
    }

    public static ParameterPanel buildParamPanel(String beanName, Project project, PsiMethod method, Disposable disposable) {
        return new ParameterPanel(beanName, project, method, disposable);
    }

}
