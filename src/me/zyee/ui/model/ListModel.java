package me.zyee.ui.model;

import com.intellij.psi.PsiMethod;
import com.intellij.ui.SortedListModel;
import me.zyee.ListPsiMethod;

import javax.swing.*;
import java.util.Comparator;

/**
 * @author yee
 * @date 2018/11/1
 */
public class ListModel extends SortedListModel<ListPsiMethod> {


    public ListModel(Comparator<ListPsiMethod> comparator) {
        super(comparator);
    }

    public void setMethods(PsiMethod[] methods) {
        this.clear();
        for (PsiMethod method : methods) {
            add(new ListPsiMethod(method));
        }
    }

    public void fireContentsChanged(JList list) {
        fireContentsChanged(list, -1, -1);
    }
}
