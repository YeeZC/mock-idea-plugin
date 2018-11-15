package me.zyee.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import me.zyee.format.CodeFormat;
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
                file = "$APP_CONFIG$/MockSetting.xml"
        )}
)
public class MockSetting implements PersistentStateComponent<Element> {
    private Boolean interfaceOnly = true;
    private Boolean staticMock = true;
    private Framework framework = Framework.EASYMOCK;

    public static MockSetting getInstance() {
        return ServiceManager.getService(MockSetting.class);
    }

    public Boolean isInterfaceOnly() {
        return interfaceOnly;
    }

    public void setInterfaceOnly(boolean interfaceOnly) {
        this.interfaceOnly = interfaceOnly;
    }

    public Boolean isStaticMock() {
        switch (framework) {
            case EASYMOCK:
                return !interfaceOnly && staticMock;
            case MOCKITO:
                return staticMock;
            default:
                return false;
        }
    }

    public void setStaticMock(Boolean staticMock) {
        this.staticMock = staticMock;
    }

    public Framework getFramework() {
        return framework;
    }

    public CodeFormat getCodeFormat() {
        return getFramework().getCodeFormat(isStaticMock());
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("MockSetting");
        element.setAttribute("interfaceOnly", isInterfaceOnly().toString());
        element.setAttribute("staticMock", isStaticMock().toString());
        element.setAttribute("framework", getFramework().name());
        return element;
    }

    @Override
    public void loadState(@NotNull Element element) {
        interfaceOnly = Boolean.valueOf(element.getAttributeValue("interfaceOnly", "true"));
        staticMock = Boolean.valueOf(element.getAttributeValue("staticMock", "true"));
        framework = Framework.valueOf(element.getAttributeValue("framework", "EASYMOCK"));
    }
}
