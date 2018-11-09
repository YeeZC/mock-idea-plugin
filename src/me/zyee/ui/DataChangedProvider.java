package me.zyee.ui;

import com.intellij.psi.PsiElement;

import java.util.Collection;

/**
 * @author yee
 * @date 2018/11/9
 */
interface DataChangedProvider<Element extends PsiElement> {
    void setData(Element[] elements);

    void setData(Collection<Element> elements);
}
