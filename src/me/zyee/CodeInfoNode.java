package me.zyee;

import me.zyee.config.MockSetting;
import me.zyee.format.CodeFormat;

/**
 * @author yee
 * @date 2018/11/2
 */
public interface CodeInfoNode {
    String getCode();

    default String getPreview() {
        CodeFormat framework = MockSetting.getInstance().getCodeFormat();
        StringBuffer buffer = new StringBuffer(framework.start());
        buffer.append(getCode());
        buffer.append(framework.replay());
        return buffer.toString();
    }

    boolean isStatic();
}
