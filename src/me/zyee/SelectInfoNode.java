package me.zyee;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import me.zyee.config.MockSetting;
import me.zyee.format.CodeFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    private Set<PsiClass> staticMockSet;

    public SelectInfoNode(@NotNull PsiClass psiClass) {
        this.psiClass = psiClass;
        mockBeanName = "mock" + psiClass.getName();
        baseName = mockBeanName;
        methods = new HashMap<>();
        tempMethods = new HashMap<>();
        staticMockSet = new HashSet<>();
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

    public Map<PsiMethod, MethodSelectInfoNode> getMethods() {
        return methods;
    }

    public void addMethodSelectInfoNode(PsiMethod method, MethodSelectInfoNode node) {
        methods.put(method, node);
    }

    public void calculateMethodSelected(Collection<PsiMethod> methods) {
        for (PsiMethod method : methods) {
            if (!tempMethods.containsKey(method)) {
                MethodSelectInfoNode node = new MethodSelectInfoNode(mockBeanName);
                node.setContains(contains);
                node.setMethod(method);
                tempMethods.put(method, node);
            }
        }
        tempMethods.entrySet().removeIf(entry -> !methods.contains(entry.getKey()));
    }

    @Override
    public String getCode() {
        CodeFormat framework = MockSetting.getInstance().getCodeFormat();
        StringBuffer staticBuffer = new StringBuffer();
        String className = psiClass.getName();
        boolean staticMock = false;
        contains.add(getMockBeanName());
        String text = framework.mockObjectHeadFormat(className, mockBeanName);
        StringBuffer buffer = new StringBuffer(text);
        if (!methods.isEmpty()) {
            for (MethodSelectInfoNode value : methods.values()) {
                value.setContains(contains);
                value.setStaticMock(staticMockSet);
                value.setCallBeanName(mockBeanName);
                buffer.append(value.getCode());
                staticMock |= value.isStatic();
            }
        }
        if (!tempMethods.isEmpty()) {
            for (Map.Entry<PsiMethod, MethodSelectInfoNode> entry : tempMethods.entrySet()) {
                if (!methods.containsKey(entry.getKey())) {
                    MethodSelectInfoNode value = entry.getValue();
                    value.setStaticMock(staticMockSet);
                    value.setCallBeanName(mockBeanName);
                    value.setContains(contains);
                    buffer.append(value.getCode());
                    staticMock |= value.isStatic();
                }
            }
        }
        if (isStatic() || staticMock) {
            staticMockSet.add(psiClass);
            staticBuffer.append(framework.start(className));
        }
        staticBuffer.append(buffer);
        return staticBuffer.toString();
    }

    @Override
    public boolean isStatic() {
        PsiModifierList list = psiClass.getModifierList();
        return null != list && (list.hasModifierProperty(PsiModifier.STATIC) || list.hasModifierProperty(PsiModifier.FINAL));
    }

    public Set<String> getContains() {
        return contains;
    }

    public void setContains(Set<String> contains) {
        this.contains = contains;
    }

    public Set<PsiClass> getStaticMockSet() {
        return staticMockSet;
    }

    public void setStaticMockSet(Set<PsiClass> staticMockSet) {
        this.staticMockSet = staticMockSet;
    }
}
