package me.zyee.ui;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SortedListModel;
import com.intellij.ui.components.JBList;

import java.util.Collection;
import java.util.Comparator;

/**
 * @author yee
 * @date 2018/11/2
 */
public class PsiElementList<T extends PsiElement> extends JBList<T> {
    private final Comparator<T> comparator = (el0, el1) -> el0.getText().compareToIgnoreCase(el1.getText());
    private ListModel model;

    public PsiElementList() {
        model = new ListModel();
        this.setModel(model);
        this.setCellRenderer(new DefaultPsiElementCellRenderer());
    }

    public void addElements(T[] elements) {
        model.addAll(elements);
    }

    public void addElements(Collection<T> elements) {
        model.addAll(elements);
    }
    private class ListModel extends SortedListModel<T> {

        public ListModel() {
            super(comparator);
        }

        @Override
        public int[] addAll(T[] items) {
            this.clear();
            int[] result = super.addAll(items);
            fireContentsChanged(PsiElementList.this, -1, -1);
            return result;
        }

        @Override
        public int[] addAll(Collection<T> items) {
            this.clear();
            int[] result = super.addAll(items);
            fireContentsChanged(PsiElementList.this, -1, -1);
            return result;
        }
    }
}
