package me.zyee.ui;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/**
 * @author yee
 * @date 2018/11/14
 */
public interface UIItem {
    String getDisplay();

    boolean isAvailable();

    class UIItemCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, ((UIItem) value).getDisplay(), index, isSelected, cellHasFocus);
        }
    }
}
