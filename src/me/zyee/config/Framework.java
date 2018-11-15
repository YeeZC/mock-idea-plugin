package me.zyee.config;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import me.zyee.format.CodeFormat;
import me.zyee.ui.UIItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        public String start(String... args) {
            return "IMocksControl control = EasyMock.createControl();\n";
        }

        @Override
        public List<PsiClass> importList(Project project) {
            List<PsiClass> list = new ArrayList<>();
            PsiClass easymock = PsiType.getTypeByName("org.easymock.EasyMock", project, GlobalSearchScope.allScope(project)).resolve();
            PsiClass control = PsiType.getTypeByName("org.easymock.IMocksControl", project, GlobalSearchScope.allScope(project)).resolve();
            if (null != easymock) {
                list.add(easymock);
            }
            if (null != control) {
                list.add(control);
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
                    "    <artifactId>mockito-all</artifactId>\n" +
                    "    <version>2.0.2-beta</version>\n" +
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
            return "PowerMock.replayAll();\n";
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

            if (null != powerMock) {
                list.add(powerMock);
            }

            if (null != easyMock) {
                list.add(easyMock);
            }
            return Collections.unmodifiableList(list);
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
            if (null != powerMock) {
                list.add(powerMock);
            }
            if (null != easyMock) {
                list.add(easyMock);
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
                    "    <artifactId>mockito-all</artifactId>\n" +
                    "    <version>2.0.2-beta</version>\n" +
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
    };

    @NotNull
    private static List<PsiAnnotation> powerMockPsiAnnotations(Project project, Collection<PsiClass> others) {
        List<PsiAnnotation> annotations = new ArrayList<>();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiAnnotation runWith = factory.createAnnotationFromText(String.format("@org.junit.runner.RunWith(PowerMockRunner.class)"), null);
        if (!others.isEmpty()) {
            StringBuilder buffer = new StringBuilder("@org.powermock.core.classloader.annotations.PrepareForTest( ");
            for (PsiClass other : others) {
                buffer.append(other.getName()).append(".class, ");
            }
            buffer.setLength(buffer.length() - 2);
            buffer.append(")");
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
    public List<PsiAnnotation> psiAnnotations(Project project, Collection<PsiClass> others) {
        return Collections.emptyList();
    }
}
