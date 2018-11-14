package me.zyee.ui;


import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SortedComboBoxModel;
import me.zyee.DataProcessors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * @author yee
 * @date 2018/11/14
 */
public class UIItemComboBox<T extends UIItem> extends ComboBox<T> implements DataChangedProvider<T> {
    public UIItemComboBox() {
        this.setModel(new SortedModel());
        this.setRenderer(new UIItem.UIItemCellRenderer());
    }

    @Override
    public void setData(T[] elements) {
        ((SortedModel) getModel()).setData(elements);
    }

    @Override
    public void setData(Collection<T> collection) {
        ((SortedModel) getModel()).setData(collection);
    }

    private class SortedModel extends SortedComboBoxModel<T> implements DataChangedProvider<T> {

        public SortedModel() {
            super(Comparator.comparing(T::getDisplay));
        }

        @Override
        public void setData(T[] elements) {
            setData(Arrays.asList(elements));
        }

        @Override
        public void setData(Collection<T> elements) {
            this.clear();
            this.addAll(DataProcessors.AccessList.of(elements).execute(item -> item.isAvailable()));
            fireContentsChanged(UIItemComboBox.this, -1, -1);
        }
    }
}
