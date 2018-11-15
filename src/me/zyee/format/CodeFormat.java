package me.zyee.format;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.List;

/**
 * @author yee
 * @date 2018/11/14
 */
public interface CodeFormat {
    String mockMethodStartFormat(String beanName, String methodName);

    String mockMethodEndFormat(String beanName);

    String mockObjectHeadFormat(String className, String beanName);


    String replay();

    String start(String... args);

    List<PsiClass> importList(Project project);

    String getMavenConfig();
}
