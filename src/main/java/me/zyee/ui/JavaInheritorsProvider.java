package me.zyee.ui;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

/**
 * @author yee
 * @date 2018/11/2
 */
public class JavaInheritorsProvider extends BaseClassInheritorsProvider {
    private final Project myProject;

    public JavaInheritorsProvider(Project project, PsiClass baseClass, GlobalSearchScope scope) {
        super(baseClass, scope);
        this.myProject = project;
    }

    @Override
    @NotNull
    protected Query<PsiClass> searchForInheritors(PsiClass baseClass, GlobalSearchScope searchScope, boolean checkDeep) {
        return ClassInheritorsSearch.search(baseClass, searchScope, checkDeep);
    }

    @Override
    public boolean isInheritor(PsiClass clazz, PsiClass baseClass, boolean checkDeep) {
        return clazz.isInheritor(baseClass, checkDeep);
    }

    @Override
    public String[] getNames() {
        return PsiShortNamesCache.getInstance(this.myProject).getAllClassNames();
    }
}
