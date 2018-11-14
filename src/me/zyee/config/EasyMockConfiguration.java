package me.zyee.config;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.panels.HorizontalLayout;
import me.zyee.ui.UIItemComboBox;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * @author yee
 * @date 2018/11/3
 */
public class EasyMockConfiguration implements SearchableConfigurable {
    private boolean modify = false;
    private boolean interfaceOnly = false;
    private boolean staticMock = true;
    private JBRadioButton interfaceOnlyButton = new JBRadioButton("Interfaces Only (EaseMock V2)");
    private JBRadioButton normal = new JBRadioButton("Interfaces & Classes (EaseMock V3+)");
    private JBCheckBox staticMockCheckBox = new JBCheckBox("Mock Static Method (Powered by PowerMock)");
    private UIItemComboBox<Framework> frameworkComboBox;
    private Framework selectedFramework;


    @NotNull
    @Override
    public String getId() {
        return "me.zyee.easymockgenerator.conf";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Mock Config";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel panel = new JPanel(new VerticalFlowLayout());
        JPanel frameworkPanel = new JPanel(new HorizontalLayout(5));
        frameworkPanel.add(new JBLabel("Mock Framework"));
        frameworkComboBox = new UIItemComboBox<>();
        frameworkComboBox.setData(Framework.values());

        frameworkPanel.add(frameworkComboBox);
        panel.add(frameworkPanel);
        panel.add(new JSeparator());
        panel.add(new JBLabel("EasyMock Config"));
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
            staticMockCheckBox.setEnabled(!interfaceOnly);
            modify = true;
        });
        staticMockCheckBox.setSelected(EasyMockSetting.getInstance().isStaticMock());
        staticMockCheckBox.addChangeListener(e -> {
            staticMock = staticMockCheckBox.isSelected();
            modify = true;
        });
        panel.add(new JSeparator());
        staticMockCheckBox.setEnabled(!interfaceOnly);
        panel.add(staticMockCheckBox);
        frameworkComboBox.addItemListener(e -> {
            selectedFramework = (Framework) frameworkComboBox.getSelectedItem();
            boolean isEasyMock = selectedFramework == Framework.EASYMOCK;
            interfaceOnlyButton.setEnabled(isEasyMock);
            normal.setEnabled(isEasyMock);
            if (!isEasyMock) {
                staticMockCheckBox.setEnabled(true);
            } else {
                staticMockCheckBox.setEnabled(!interfaceOnly);
            }
        });
        frameworkComboBox.setSelectedIndex(0);
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
        EasyMockSetting.getInstance().setStaticMock(staticMock);
    }

    @Override
    public void reset() {
        if (modify) {
            interfaceOnly = EasyMockSetting.getInstance().isInterfaceOnly();
            interfaceOnlyButton.setSelected(interfaceOnly);
            normal.setSelected(!interfaceOnly);
            staticMock = EasyMockSetting.getInstance().isStaticMock();
            staticMockCheckBox.setSelected(staticMock);
            modify = false;
        }
    }
}
