package me.zyee.ui;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SortedListModel;
import com.intellij.ui.components.JBList;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/2
 */
public class PsiElementList<T extends PsiElement> extends JBList<T> implements DataChangedProvider<T> {
    private final Comparator<T> comparator = (el0, el1) -> el0.getText().compareToIgnoreCase(el1.getText());

    public PsiElementList(boolean sort) {
        if (sort) {
            this.setModel(new ListModel());
        } else {
            this.setModel(new ListModelNotSort());
        }
        this.setCellRenderer(new DefaultPsiElementCellRenderer());
    }

    public PsiElementList() {
        this(true);
    }

    @Override
    public void setData(T[] elements) {
        ((DataChangedProvider) getModel()).setData(elements);
    }

    @Override
    public void setData(Collection<T> elements) {
        ((DataChangedProvider) getModel()).setData(elements);
    }

    private class ListModel extends SortedListModel<T> implements DataChangedProvider<T> {

        public ListModel() {
            super(comparator);
        }

        @Override
        public void setData(Collection<T> items) {
            this.clear();
            addAll(items);
            fireContentsChanged(PsiElementList.this, -1, -1);
        }

        @Override
        public void setData(T[] items) {
            this.clear();
            addAll(items);
            fireContentsChanged(PsiElementList.this, -1, -1);
        }
    }

    private class ListModelNotSort extends AbstractListModel<T> implements DataChangedProvider<T> {
        private List<T> items;

        public ListModelNotSort() {
            items = new ArrayList<>();
        }

        @Override
        public void setData(Collection<T> items) {
            this.items.clear();
            this.items.addAll(items);
            fireContentsChanged(PsiElementList.this, -1, -1);
        }

        @Override
        public void setData(T[] items) {
            this.items.clear();
            this.items.addAll(Arrays.asList(items));
            fireContentsChanged(PsiElementList.this, -1, -1);
        }

        @Override
        public int getSize() {
            return items.size();
        }

        @Override
        public T getElementAt(int index) {
            return items.get(index);
        }
    }
}
