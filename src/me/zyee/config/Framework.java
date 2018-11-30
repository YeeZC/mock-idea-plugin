package me.zyee.config;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import me.zyee.format.CodeFormat;
import me.zyee.ui.UIItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
            return "control.replay();\n" +
                    "EasyMock.verifyAll();";
        }

        @Override
        public String start(String... args) {
            return "IMocksControl control = EasyMock.createControl();\n";
        }

        @Override
        public List<PsiClass> importList(Project project) {
            List<PsiClass> list = new ArrayList<>();
            PsiClass easymock = PsiType.getTypeByName("org.easymock.EasyMock", project, GlobalSearchScope.allScope(project)).resolve();
            PsiClass control = PsiType.getTypeByName("org.easymock.IMocksControl", project, GlobalSearchScope.allScope(project)).resolve();
            return calculateImportClasses(list, easymock, control);
        }

        @Override
        public String getMavenConfig() {
            return "<dependency>\n" +
                    "    <groupId>junit</groupId>\n" +
                    "    <artifactId>junit</artifactId>\n" +
                    "    <version>4.12</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>org.easymock</groupId>\n" +
                    "    <artifactId>easymock</artifactId>\n" +
                    "    <version>3.4</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>";
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
            return String.format("Mockito.when(%s.%s( ", beanName, methodName);
        }

        @Override
        public String mockMethodEndFormat(String beanName) {
            return String.format(".thenReturn(%s);\n", beanName);
        }

        @Override
        public String mockObjectHeadFormat(String className, String beanName) {
            return String.format("%s %s = Mockito.mock(%s.class);\n", className, beanName, className);
        }

        @Override
        public String replay() {
            return "";
        }

        @Override
        public String start(String... args) {
            return "";
        }

        @Override
        public List<PsiClass> importList(Project project) {
            List<PsiClass> list = new ArrayList<>();
            PsiClass mockito = PsiType.getTypeByName("org.mockito.Mockito", project, GlobalSearchScope.allScope(project)).resolve();
            if (null != mockito) {
                list.add(mockito);
            }
            return Collections.unmodifiableList(list);

        }

        @Override
        public String getMavenConfig() {
            return "<dependency>\n" +
                    "    <groupId>junit</groupId>\n" +
                    "    <artifactId>junit</artifactId>\n" +
                    "    <version>4.12</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>org.mockito</groupId>\n" +
                    "    <artifactId>mockito-core</artifactId>\n" +
                    "    <version>2.8.9</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>";
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
            return EASYMOCK.mockMethodStartFormat(beanName, methodName);
        }

        @Override
        public String mockMethodEndFormat(String beanName) {
            return EASYMOCK.mockMethodEndFormat(beanName);
        }

        @Override
        public String mockObjectHeadFormat(String className, String beanName) {
            return String.format("%s %s = PowerMock.createMock(%s.class);\n", className, beanName, className);
        }

        @Override
        public String replay() {
            return "PowerMock.replayAll();\n\n" +
                    "// do test\n" +
                    "PowerMock.verifyAll();" +
                    "\n";
        }

        @Override
        public String start(String... args) {
            if (null != args && args.length > 0) {
                return String.format("PowerMock.mockStatic(%s.class);\n", args[0]);
            }
            return "";
        }

        @Override
        public List<PsiClass> importList(Project project) {
            List<PsiClass> list = new ArrayList<>();
            PsiClass runner = PsiType.getTypeByName("org.powermock.modules.junit4.PowerMockRunner", project, GlobalSearchScope.allScope(project)).resolve();
            PsiClass powerMock = PsiType.getTypeByName("org.powermock.api.easymock.PowerMock", project, GlobalSearchScope.allScope(project)).resolve();
            PsiClass easyMock = PsiType.getTypeByName("org.easymock.EasyMock", project, GlobalSearchScope.allScope(project)).resolve();

            if (null != runner) {
                list.add(runner);
            }

            return calculateImportClasses(list, powerMock, easyMock);
        }

        @Override
        public List<PsiClass> annotationClasses(Project project) {
            return powerMockAnnotationClasses(project);
        }

        @Override
        public List<PsiAnnotation> psiAnnotations(Project project, Collection<PsiClass> others) {
            return powerMockPsiAnnotations(project, others);
        }

        @Override
        public String getMavenConfig() {
            return "<dependency>\n" +
                    "    <groupId>junit</groupId>\n" +
                    "    <artifactId>junit</artifactId>\n" +
                    "    <version>4.12</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>org.easymock</groupId>\n" +
                    "    <artifactId>easymock</artifactId>\n" +
                    "    <version>3.4</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>org.powermock</groupId>\n" +
                    "    <artifactId>powermock-module-junit4</artifactId>\n" +
                    "    <version>1.7.4</version>\n" +
                    "    <scope>test</scope>\n" +
                    "<dependency>\n" +
                    "    <groupId>org.powermock</groupId>\n" +
                    "    <artifactId>powermock-api-easymock</artifactId>\n" +
                    "    <version>1.7.4</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>";
        }

        @Override
        public void updateStaticMock(Project project, Set<PsiClass> set, PsiClass annotation, PsiModifierList modifierList) {
            powerMockUpdateStaticMock(project, set, annotation, modifierList);
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
            return MOCKITO.mockMethodStartFormat(beanName, methodName);
        }

        @Override
        public String mockMethodEndFormat(String beanName) {
            return MOCKITO.mockMethodEndFormat(beanName);
        }

        @Override
        public String mockObjectHeadFormat(String className, String beanName) {
            return String.format("%s %s = PowerMockito.mock(%s.class);\n", className, beanName, className);
        }

        @Override
        public String replay() {
            return "";
        }

        @Override
        public String start(String... args) {
            if (null != args && args.length > 0) {
                return String.format("PowerMockito.mockStatic(%s.class);\n", args[0]);
            }
            return "";
        }

        @Override
        public List<PsiClass> importList(Project project) {
            List<PsiClass> list = new ArrayList<>();
            PsiClass runner = PsiType.getTypeByName("org.powermock.modules.junit4.PowerMockRunner", project, GlobalSearchScope.allScope(project)).resolve();
            PsiClass powerMock = PsiType.getTypeByName("org.powermock.api.mockito.PowerMockito", project, GlobalSearchScope.allScope(project)).resolve();
            PsiClass easyMock = PsiType.getTypeByName("org.mockito.Mockito", project, GlobalSearchScope.allScope(project)).resolve();
            if (null != runner) {
                list.add(runner);
            }
            return calculateImportClasses(list, powerMock, easyMock);
        }

        @Override
        public String getMavenConfig() {
            return "<dependency>\n" +
                    "    <groupId>junit</groupId>\n" +
                    "    <artifactId>junit</artifactId>\n" +
                    "    <version>4.12</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>org.mockito</groupId>\n" +
                    "    <artifactId>mockito-core</artifactId>\n" +
                    "    <version>2.8.9</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>org.powermock</groupId>\n" +
                    "    <artifactId>powermock-module-junit4</artifactId>\n" +
                    "    <version>1.7.4</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>org.powermock</groupId>\n" +
                    "    <artifactId>powermock-api-mockito2</artifactId>\n" +
                    "    <version>1.7.4</version>\n" +
                    "    <scope>test</scope>\n" +
                    "</dependency>";
        }

        @Override
        public List<PsiClass> annotationClasses(Project project) {
            return powerMockAnnotationClasses(project);
        }

        @Override
        public List<PsiAnnotation> psiAnnotations(Project project, Collection<PsiClass> others) {
            return powerMockPsiAnnotations(project, others);
        }

        @Override
        public void updateStaticMock(Project project, Set<PsiClass> set, PsiClass annotation, PsiModifierList modifierList) {
            powerMockUpdateStaticMock(project, set, annotation, modifierList);
        }
    };

    @NotNull
    private static List<PsiClass> calculateImportClasses(List<PsiClass> list, PsiClass easymock, PsiClass control) {
        if (null != easymock) {
            list.add(easymock);
        }
        if (null != control) {
            list.add(control);
        }
        return Collections.unmodifiableList(list);
    }

    @NotNull
    private static List<PsiAnnotation> powerMockPsiAnnotations(Project project, Collection<PsiClass> others) {
        List<PsiAnnotation> annotations = new ArrayList<>();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiAnnotation runWith = factory.createAnnotationFromText(String.format("@org.junit.runner.RunWith(PowerMockRunner.class)"), null);
        if (!others.isEmpty()) {
            StringBuilder buffer = new StringBuilder("@org.powermock.core.classloader.annotations.PrepareForTest( ");
            StringBuilder multi = new StringBuilder();
            if (others.size() > 1) {
                multi.append("value={");
            }
            for (PsiClass other : others) {
                multi.append(other.getName()).append(".class, ");
            }

            multi.setLength(multi.length() - 2);
            if (others.size() > 1) {
                multi.append("}");
            }
            multi.append(")");
            buffer.append(multi);
            PsiAnnotation prepare = factory.createAnnotationFromText(buffer.toString(), null);
            annotations.add(prepare);
        }
        annotations.add(runWith);
        return annotations;
    }

    @NotNull
    private static List<PsiClass> powerMockAnnotationClasses(Project project) {
        List<PsiClass> list = new ArrayList<>();
        PsiClass runWith = PsiType.getTypeByName("org.junit.runner.RunWith", project, GlobalSearchScope.allScope(project)).resolve();
        PsiClass prepare = PsiType.getTypeByName("org.powermock.core.classloader.annotations.PrepareForTest", project, GlobalSearchScope.allScope(project)).resolve();
        if (null != prepare) {
            list.add(prepare);
        }
        if (null != runWith) {
            list.add(runWith);
        }
        return list;
    }

    private static void powerMockUpdateStaticMock(Project project, Set<PsiClass> set, @NotNull PsiClass anClass, @Nullable PsiModifierList modifierList) {
        if (modifierList == null) {
            return;
        }
        if ("org.powermock.core.classloader.annotations.PrepareForTest".equals(anClass.getQualifiedName())) {
            PsiAnnotation annotation = modifierList.findAnnotation(anClass.getQualifiedName());
            if (null != annotation) {
                String content = annotation.getText();
                content = content.replace(".class", "");
                content = content.substring(content.indexOf("(") + 1, content.lastIndexOf(")"));
                String[] classes = content.split(",");
                for (String aClass : classes) {
                    PsiClass[] psiClass = PsiShortNamesCache.getInstance(project).getClassesByName(aClass, GlobalSearchScope.projectScope(project));
                    set.add(psiClass[0]);
                }
            }

        }

    }

    private String framework;

    Framework(String framework) {
        this.framework = framework;
    }

    @Override
    public String getDisplay() {
        return framework;
    }

    public abstract CodeFormat getCodeFormat(boolean staticMock);

    @Override
    public List<PsiClass> annotationClasses(Project project) {
        return Collections.emptyList();
    }

    @Override
    public void updateStaticMock(Project project, Set<PsiClass> set, PsiClass annotation, PsiModifierList modifierList) {

    }

    @Override
    public List<PsiAnnotation> psiAnnotations(Project project, Collection<PsiClass> others) {
        return Collections.emptyList();
    }
}
