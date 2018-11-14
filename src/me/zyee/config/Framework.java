package me.zyee.config;

import me.zyee.format.CodeFormat;
import me.zyee.ui.UIItem;

/**
 * @author yee
 * @date 2018/11/14
 */
public enum Framework implements UIItem, CodeFormat {
    //
    EASYMOCK("Easy Mock") {
        @Override
        public CodeFormat getCodeFormat(boolean staticMock) {
            return staticMock ? POWER_EASYMOCK : this;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public String mockMethodStartFormat(String beanName, String methodName) {
            return String.format("EasyMock.expect(%s.%s( ", beanName, methodName);
        }

        @Override
        public String mockMethodEndFormat(String beanName) {
            return String.format(".andReturn(%s).anyTimes();\n", beanName);
        }

        @Override
        public String mockObjectHeadFormat(String className, String beanName) {
            return String.format("%s %s = control.createMock(%s.class);\n", className, beanName, className);
        }

        @Override
        public String replay() {
            return "control.replay();\n";
        }

        @Override
        public String start() {
            if (!MockSetting.getInstance().isStaticMock()) {
                return "IMocksControl control = EasyMock.createControl();\n";
            } else {
                return "";
            }
        }
    }, MOCKITO("Mockito") {
        @Override
        public CodeFormat getCodeFormat(boolean staticMock) {
            return staticMock ? POWER_MOCKITO : this;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public String mockMethodStartFormat(String beanName, String methodName) {
            return null;
        }

        @Override
        public String mockMethodEndFormat(String beanName) {
            return null;
        }

        @Override
        public String mockObjectHeadFormat(String className, String beanName) {
            return null;
        }

        @Override
        public String replay() {
            return null;
        }

        @Override
        public String start() {
            return null;
        }
    }, POWER_EASYMOCK("PowerEasy") {
        @Override
        public CodeFormat getCodeFormat(boolean staticMock) {
            return staticMock ? this : EASYMOCK;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public String mockMethodStartFormat(String beanName, String methodName) {
            return null;
        }

        @Override
        public String mockMethodEndFormat(String beanName) {
            return null;
        }

        @Override
        public String mockObjectHeadFormat(String className, String beanName) {
            return null;
        }

        @Override
        public String replay() {
            return null;
        }

        @Override
        public String start() {
            return null;
        }
    }, POWER_MOCKITO("PowerMockito") {
        @Override
        public CodeFormat getCodeFormat(boolean staticMock) {
            return staticMock ? this : MOCKITO;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public String mockMethodStartFormat(String beanName, String methodName) {
            return null;
        }

        @Override
        public String mockMethodEndFormat(String beanName) {
            return null;
        }

        @Override
        public String mockObjectHeadFormat(String className, String beanName) {
            return null;
        }

        @Override
        public String replay() {
            return null;
        }

        @Override
        public String start() {
            return null;
        }
    };
    private String framework;

    Framework(String framework) {
        this.framework = framework;
    }

    @Override
    public String getDisplay() {
        return framework;
    }

    public abstract CodeFormat getCodeFormat(boolean staticMock);
}
