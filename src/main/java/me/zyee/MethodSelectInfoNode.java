package me.zyee;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import me.zyee.config.Framework;
import me.zyee.config.MockSetting;
import me.zyee.format.CodeFormat;
import me.zyee.value.DefaultValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/11/2
 */
public class MethodSelectInfoNode implements CodeInfoNode {
    private String callBeanName;
    private PsiMethod method;
    private Map<Integer, SelectInfoNode> paramDepNode;
    private SelectInfoNode returnTypeDepNode;
    private Set<String> contains;
    private Set<PsiClass> staticMock;

    public MethodSelectInfoNode(String callBeanName) {
        this.callBeanName = callBeanName;
        this.paramDepNode = new HashMap<>();
        this.staticMock = new HashSet<>();
    }

    public Set<PsiClass> getStaticMock() {
        return staticMock;
    }

    public void setStaticMock(Set<PsiClass> staticMock) {
        this.staticMock = staticMock;
    }

    public void setCallBeanName(String callBeanName) {
        this.callBeanName = callBeanName;
    }

    public void setMethod(PsiMethod method) {
        this.method = method;
    }

    public void setContains(Set<String> contains) {
        this.contains = contains;
    }

    public void addParamNode(int order, SelectInfoNode node) {
        paramDepNode.put(order, node);
    }

    public void setReturnTypeDepNode(SelectInfoNode returnTypeDepNode) {
        this.returnTypeDepNode = returnTypeDepNode;
    }

    public Map<Integer, SelectInfoNode> getParamDepNode() {
        return paramDepNode;
    }

    public SelectInfoNode getReturnTypeDepNode() {
        return returnTypeDepNode;
    }

    @Override
    public String getCode() {
        if (PsiType.VOID.equals(method.getReturnType())
                && Framework.EASYMOCK.equals(MockSetting.getInstance().getFramework())) {
            return getVoidCode();
        } else {
            return getObjectCode();
        }
    }

    @Override
    public boolean isStatic() {
        return method.getModifierList().hasExplicitModifier(PsiModifier.STATIC);
    }

    @NotNull
    private String getObjectCode() {
        CodeFormat framework = MockSetting.getInstance().getCodeFormat();
        StringBuffer paramBuilder = new StringBuffer();
        StringBuffer returnTypeBuilder = new StringBuffer();
        StringBuffer buffer = new StringBuffer();
        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (isStatic()) {
            PsiClass parent = PsiTreeUtil.getParentOfType(method, PsiClass.class);
            staticMock.add(parent);
            buffer.append(framework.mockMethodStartFormat(parent.getName(), method.getName()));
        } else {
            buffer.append(framework.mockMethodStartFormat(callBeanName, method.getName()));
        }
        if (parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                PsiParameter parameter = parameters[i];
                SelectInfoNode node = paramDepNode.get(i);
                if (null != node) {
                    node.setStaticMockSet(staticMock);
                }
                String paramName = calculateBeanName(parameter.getName(), paramBuilder, node);
                buffer.append(paramName).append(",");
            }
        }
        buffer.setCharAt(buffer.length() - 1, ')');
        buffer.append(")");
        PsiElement type = method.getReturnTypeElement();
        if (null != type) {
            String mockBeanName = "mock" + type.getText();
            int index = mockBeanName.indexOf("<");
            if (index >= 0) {
                mockBeanName = mockBeanName.substring(0, index);
            }
            Object value = DefaultValue.value(method.getReturnType());
            mockBeanName = calculateBeanName(mockBeanName, returnTypeBuilder, returnTypeDepNode);
            if (null == returnTypeDepNode && value != null) {
                buffer.append(framework.mockMethodEndFormat(value.toString()));
            } else {
                buffer.append(framework.mockMethodEndFormat(mockBeanName));
            }
            paramBuilder.append(returnTypeBuilder).append(buffer);
        }
        return paramBuilder.toString();
    }

    @NotNull
    private String getVoidCode() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("%s.%s(", callBeanName, method.getName()));
        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                buffer.append("EasyMock.anyObject(), ");
            }
            buffer.delete(buffer.length() - 2, buffer.length());
        }
        buffer.append(")");
        buffer.append(";\n");
        buffer.append("EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {\n");
        buffer.append("@Override\n");
        buffer.append("public Object answer() throws Throwable {\n");
        buffer.append("return null;\n");
        buffer.append("}\n");
        buffer.append("}).anyTimes();\n");
        return buffer.toString();
    }

    private String calculateBeanName(String paramName, StringBuffer buffer, SelectInfoNode node) {
        if (null != node) {
            node.setContains(contains);
            String name = node.getMockBeanName();
            buffer.append(node.getCode());
            return name;
        } else {
            int id = 0;
            String tmp = paramName;
            while (contains.contains(tmp)) {
                tmp = paramName + id;
                id++;
            }
            contains.add(tmp);
            return tmp;
        }
    }
}
