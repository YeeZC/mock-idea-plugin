package me.zyee.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yee
 * @date 2018/11/3
 */
@State(
        name = "MockSetting",
        storages = {@Storage(
                file = "$APP_CONFIG$/EasyMockSetting.xml"
        )}
)
public class EasyMockSetting implements PersistentStateComponent<Element> {
    private Boolean interfaceOnly = true;
    private Boolean staticMock = true;

    public static EasyMockSetting getInstance() {
        return ServiceManager.getService(EasyMockSetting.class);
    }

    public Boolean isInterfaceOnly() {
        return interfaceOnly;
    }

    public void setInterfaceOnly(boolean interfaceOnly) {
        this.interfaceOnly = interfaceOnly;
    }

    public Boolean isStaticMock() {
        return !interfaceOnly && staticMock;
    }

    public void setStaticMock(Boolean staticMock) {
        this.staticMock = staticMock;
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("MockSetting");
        element.setAttribute("interfaceOnly", isInterfaceOnly().toString());
        element.setAttribute("staticMock", isStaticMock().toString());
        return element;
    }

    @Override
    public void loadState(@NotNull Element element) {
        interfaceOnly = Boolean.valueOf(element.getAttributeValue("interfaceOnly", "true"));
        interfaceOnly = Boolean.valueOf(element.getAttributeValue("staticMock", "true"));
    }
}
