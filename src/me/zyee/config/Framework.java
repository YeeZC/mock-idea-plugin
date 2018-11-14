package me.zyee.config;

import me.zyee.ui.UIItem;

/**
 * @author yee
 * @date 2018/11/14
 */
public enum Framework implements UIItem {
    EASYMOCK("Easy Mock"), MOCKITO("Mockito");
    private String framework;

    Framework(String framework) {
        this.framework = framework;
    }

    @Override
    public String getDisplay() {
        return framework;
    }
}
