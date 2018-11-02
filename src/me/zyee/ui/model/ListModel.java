package me.zyee.ui.model;

import com.intellij.psi.PsiMethod;
import com.intellij.ui.SortedListModel;

import javax.swing.JList;
import java.util.Comparator;

/**
 * @author yee
 * @date 2018/11/1
 */
public class ListModel extends SortedListModel<PsiMethod> {


    public ListModel(Comparator<PsiMethod> comparator) {
        super(comparator);
    }

    @Override
    public int[] addAll(PsiMethod[] items) {
        this.clear();
        return super.addAll(items);
    }

    public void fireContentsChanged(JList list) {
        fireContentsChanged(list, -1, -1);
    }
}
