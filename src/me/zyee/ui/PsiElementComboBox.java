package me.zyee.ui;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SortedComboBoxModel;

import java.util.Collection;
import java.util.Comparator;

/**
 * @author yee
 * @date 2018/11/9
 */
public class PsiElementComboBox<Element extends PsiElement> extends ComboBox<Element> implements DataChangedProvider<Element> {
    public PsiElementComboBox() {
        this.setModel(new SortedModel());
        this.setRenderer(new DefaultPsiElementCellRenderer());
    }

    @Override
    public void setData(Element[] elements) {
        ((DataChangedProvider) getModel()).setData(elements);
    }

    @Override
    public void setData(Collection<Element> elements) {
        ((DataChangedProvider) getModel()).setData(elements);
    }

    private class SortedModel extends SortedComboBoxModel<Element> implements DataChangedProvider<Element> {

        public SortedModel() {
            super(Comparator.comparing(Element::getText));
        }

        @Override
        public void setData(Element[] elements) {
            this.clear();
            this.addAll(elements);
            fireContentsChanged(PsiElementComboBox.this, -1, -1);
        }

        @Override
        public void setData(Collection<Element> elements) {
            this.clear();
            this.addAll(elements);
            fireContentsChanged(PsiElementComboBox.this, -1, -1);
        }
    }
}

