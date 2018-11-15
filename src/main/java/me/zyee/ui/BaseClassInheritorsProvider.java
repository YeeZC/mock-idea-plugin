package me.zyee.ui;

import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

/**
 * @author yee
 * @date 2018/11/2
 */
public abstract class BaseClassInheritorsProvider {
    private final PsiClass myBaseClass;
    private final GlobalSearchScope myScope;

    public BaseClassInheritorsProvider(PsiClass baseClass, GlobalSearchScope scope) {
        this.myBaseClass = baseClass;
        this.myScope = scope;
    }

    public PsiClass getBaseClass() {
        return this.myBaseClass;
    }

    public GlobalSearchScope getScope() {
        return this.myScope;
    }

    @NotNull
    protected abstract Query<PsiClass> searchForInheritors(PsiClass baseClass, GlobalSearchScope scope, boolean checkDeep);

    public abstract boolean isInheritor(PsiClass clazz, PsiClass baseClass, boolean checkDeep);

    public abstract String[] getNames();

    public Query<PsiClass> searchForInheritorsOfBaseClass() {
        return this.searchForInheritors(this.myBaseClass, this.myScope, true);
    }

    public boolean isInheritorOfBaseClass(PsiClass aClass) {
        return this.isInheritor(aClass, this.myBaseClass, true);
    }
}
