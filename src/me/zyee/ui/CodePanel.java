package me.zyee.ui;

import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.JBUI;
import me.zyee.SelectedInfo;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/1
 */
public abstract class CodePanel extends JPanel {
    protected PsiElementList<PsiMethod> list;
    private JTextArea textPane;

    public CodePanel(String label) {
        setLayout(new VerticalFlowLayout());
        list = new PsiElementList<>();
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            List<PsiMethod> methods = list.getSelectedValuesList();
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

    public JTextArea getTextPane() {
        return textPane;
    }

    public void loadClass() {
        loadClass(getPsiClass());
    }

    public void loadClass(PsiClass psiClass) {
        PsiMethod[] methods = psiClass.getMethods();
        list.addElements(methods);
        SelectedInfo info = new SelectedInfo();
        info.setPsiClass(psiClass);
        textPane.setText(info.toString());
    }

    protected abstract PsiClass getPsiClass();

    public PsiElementList getList() {
        return list;
    }
}
