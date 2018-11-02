package me.zyee;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/11/2
 */
public class SelectInfoNode implements CodeInfoNode {
    private PsiClass psiClass;
    private Map<PsiMethod, MethodSelectInfoNode> methods;
    private Map<PsiMethod, MethodSelectInfoNode> tempMethods;
    private Set<String> contains;
    private String mockBeanName;
    private String baseName;

    public SelectInfoNode(PsiClass psiClass) {
        this.psiClass = psiClass;
        mockBeanName = "mock" + psiClass.getName();
        baseName = mockBeanName;
        methods = new HashMap<>();
        tempMethods = new HashMap<>();
    }

    public String getMockBeanName() {
        int id = 0;
        String tmp = baseName;
        while (contains.contains(tmp)) {
            tmp = baseName + id;
            id++;
        }
        mockBeanName = tmp;
        return mockBeanName;
    }

    public void setMockBeanName(String mockBeanName) {
        this.mockBeanName = mockBeanName;
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public void addMethodSelectInfoNode(PsiMethod method, MethodSelectInfoNode node) {
        methods.put(method, node);
    }

    public void calculateMethodSelected(Collection<PsiMethod> methods) {
        for (PsiMethod method : methods) {
            if (!tempMethods.containsKey(method)) {
                MethodSelectInfoNode node = new MethodSelectInfoNode(mockBeanName);
                node.setMethod(method);
                tempMethods.put(method, node);
            }
        }
        Iterator<Map.Entry<PsiMethod, MethodSelectInfoNode>> it = tempMethods.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PsiMethod, MethodSelectInfoNode> entry = it.next();
            if (!methods.contains(entry.getKey())) {
                it.remove();
            }
        }
    }

    @Override
    public String getCode() {
        String className = psiClass.getName();
        contains.add(getMockBeanName());
        String text = String.format("%s %s = EasyMock.createMock(%s.class);\n", className, mockBeanName, className);
        StringBuffer buffer = new StringBuffer(text);
        if (!methods.isEmpty()) {
            for (MethodSelectInfoNode value : methods.values()) {
                value.setContains(contains);
                value.setCallBeanName(mockBeanName);
                buffer.append(value.getCode());
            }
        }
        if (!tempMethods.isEmpty()) {
            Iterator<Map.Entry<PsiMethod, MethodSelectInfoNode>> it = tempMethods.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<PsiMethod, MethodSelectInfoNode> entry = it.next();
                if (!methods.containsKey(entry.getKey())) {
                    MethodSelectInfoNode value = entry.getValue();
                    value.setCallBeanName(mockBeanName);
                    value.setContains(contains);
                    buffer.append(value.getCode());
                }
            }
        }

        buffer.append(String.format("EasyMock.replay(%s);\n", mockBeanName));
        return buffer.toString();
    }

    public Set<String> getContains() {
        return contains;
    }

    public void setContains(Set<String> contains) {
        this.contains = contains;
    }
}
