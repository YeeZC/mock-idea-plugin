package me.zyee.config;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.JBRadioButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author yee
 * @date 2018/11/3
 */
public class EasyMockConfiguration implements SearchableConfigurable {
    private boolean modify = false;
    private boolean interfaceOnly = true;
    private JBRadioButton interfaceOnlyButton = new JBRadioButton("Interfaces Only (EaseMock V2)");
    private JBRadioButton normal = new JBRadioButton("Interfaces & Classes (EaseMock V3+)");


    @NotNull
    @Override
    public String getId() {
        return "me.zyee.easymockgenerator.conf";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Easy Mock Config";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel panel = new JPanel(new VerticalFlowLayout());
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(interfaceOnlyButton);
        buttonGroup.add(normal);
        this.interfaceOnly = EasyMockSetting.getInstance().isInterfaceOnly();
        interfaceOnlyButton.setSelected(this.interfaceOnly);
        normal.setSelected(!this.interfaceOnly);
        panel.add(interfaceOnlyButton);
        panel.add(normal);
        interfaceOnlyButton.addChangeListener(e -> {
            this.interfaceOnly = interfaceOnlyButton.isSelected();
            modify = true;
        });
        modify = false;
        return panel;
    }

    @Override
    public boolean isModified() {
        return modify;
    }

    @Override
    public void apply() throws ConfigurationException {
        EasyMockSetting.getInstance().setInterfaceOnly(interfaceOnly);
    }

    @Override
    public void reset() {
        if (modify) {
            boolean interfaceOnly = EasyMockSetting.getInstance().isInterfaceOnly();
            interfaceOnlyButton.setSelected(interfaceOnly);
            normal.setSelected(!interfaceOnly);
            modify = false;
        }
    }
}
