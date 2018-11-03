package me.zyee;

/**
 * @author yee
 * @date 2018/11/2
 */
public interface CodeInfoNode {
    String getCode();

    default String getPreview() {
        StringBuffer buffer = new StringBuffer("IMocksControl control = EasyMock.createControl();\n");
        buffer.append(getCode());
        buffer.append("control.replay();\n");
        return buffer.toString();
    }
}
