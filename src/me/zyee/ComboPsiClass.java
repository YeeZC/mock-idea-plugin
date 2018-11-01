package me.zyee;

import com.intellij.ide.util.gotoByName.ChooseByNameItem;
import com.intellij.psi.PsiClass;

/**
 * @author yee
 * @date 2018/11/1
 */
public class ComboPsiClass {
    private PsiClass psiClass;
    private String string;

    public ComboPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
        this.string = psiClass.getName();
    }

    public ComboPsiClass(String string) {
        this.string = string;
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComboPsiClass that = (ComboPsiClass) o;

        if (psiClass != null ? !psiClass.equals(that.psiClass) : that.psiClass != null) return false;
        return string != null ? string.equals(that.string) : that.string == null;
    }

    @Override
    public int hashCode() {
        int result = psiClass != null ? psiClass.hashCode() : 0;
        result = 31 * result + (string != null ? string.hashCode() : 0);
        return result;
    }

}
