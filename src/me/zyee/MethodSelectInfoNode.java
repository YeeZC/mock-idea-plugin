package me.zyee;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.HashMap;
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

    public MethodSelectInfoNode(String callBeanName) {
        this.callBeanName = callBeanName;
        this.paramDepNode = new HashMap<>();
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


    @Override
    public String getCode() {
        StringBuffer paramBuilder = new StringBuffer();
        StringBuffer returnTypeBuilder = new StringBuffer();
        StringBuffer buffer = new StringBuffer();
        PsiParameter[] parameters = method.getParameterList().getParameters();
        buffer.append(String.format("EasyMock.expect(%s.%s( ", callBeanName, method.getName()));
        if (parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                PsiParameter parameter = parameters[i];
                SelectInfoNode node = paramDepNode.get(i);
                String paramName = calculateBeanName(parameter.getName(), paramBuilder, node);
                buffer.append(paramName).append(",");
            }
        }
        buffer.setCharAt(buffer.length() - 1, ')');
        buffer.append(")");
        PsiElement type = method.getReturnTypeElement();
        if (null != type && !"void".equals(type.getText())) {
            String mockBeanName = "mock" + type.getText();
            int index = mockBeanName.indexOf("<");
            if (index >= 0) {
                mockBeanName = mockBeanName.substring(0, index);
            }
            mockBeanName = calculateBeanName(mockBeanName, returnTypeBuilder, returnTypeDepNode);
            buffer.append(String.format(".addReturn(%s).anyTimes()", mockBeanName));
        }
        buffer.append(";\n");
        paramBuilder.append(returnTypeBuilder).append(buffer);
        return paramBuilder.toString();
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
