package me.zyee.ui;

import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import me.zyee.ListPsiMethod;
import me.zyee.SelectedInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/1
 */
public abstract class CodePanel extends JPanel {
    protected JBList<ListPsiMethod> list;
    private JTextArea textPane;

    public CodePanel(String label, ListModel<ListPsiMethod> listModel) {
        setLayout(new VerticalFlowLayout());
        list = new JBList<>(listModel);
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            List<ListPsiMethod> methods = list.getSelectedValuesList();
            SelectedInfo info = new SelectedInfo();
            info.setPsiClass(getPsiClass());
            info.setMethods(methods);
            textPane.setText(info.toString());

        }));

        JScrollPane listScrollPane = ScrollPaneFactory.createScrollPane(list);
        listScrollPane.setPreferredSize(JBUI.size(500, 100));
        add(new JLabel(label));
        add(listScrollPane);
        textPane = new JTextArea();
        textPane.setEditable(false);
        textPane.setText("Generate Code");
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textPane);
        textScrollPane.setPreferredSize(JBUI.size(500, 100));
        add(new JLabel("Result:"));
        add(textScrollPane);
    }

    public void addMouseClickListener(MouseClickListener mouseListener) {
        list.addMouseListener(mouseListener);
    }

    public int getIndexFromPoint(Point point) {
        return list.locationToIndex(point);
    }

    public JTextArea getTextPane() {
        return textPane;
    }

    public void setTextPane(JTextArea textPane) {
        this.textPane = textPane;
    }


    public JBList<ListPsiMethod> getList() {
        return list;
    }

    public void setList(JBList<ListPsiMethod> list) {
        this.list = list;
    }

    public void loadClass() {
        loadClass(getPsiClass());
    }

    public void loadClass(PsiClass psiClass) {
        PsiMethod[] methods = psiClass.getMethods();
        if (list.getModel() instanceof me.zyee.ListModel) {
            ((me.zyee.ListModel) list.getModel()).setMethods(methods);
            ((me.zyee.ListModel) list.getModel()).fireContentsChanged(list);
        }
    }

    protected abstract PsiClass getPsiClass();

    public interface MouseClickListener extends MouseListener {
        @Override
        default public void mouseClicked(MouseEvent e) {

        }

        @Override
        default public void mouseReleased(MouseEvent e) {

        }

        @Override
        default public void mouseEntered(MouseEvent e) {

        }

        @Override
        default public void mouseExited(MouseEvent e) {

        }
    }
}
