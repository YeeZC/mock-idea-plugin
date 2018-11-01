package me.zyee.ui;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.projectView.BaseProjectTreeBuilder;
import com.intellij.ide.projectView.impl.AbstractProjectTreeStructure;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectTreeBuilder;
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.ide.util.ClassFilter;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePanel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.CheckBoxListListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SortedListModel;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import cucumber.api.java.hu.De;
import me.zyee.ListModel;
import me.zyee.ListPsiMethod;
import me.zyee.SelectedInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/11/1
 */
public class GeneratorDlg extends DialogWrapper implements TreeClassChooser {
    @NotNull
    private final Project myProject;
    private final GlobalSearchScope myScope;
    @NotNull
    private final Filter<PsiClass> myClassFilter;
    private final Class<PsiClass> myElementClass;
    @Nullable
    private final PsiClass myBaseClass;
    private final boolean myIsShowMembers;
    private final boolean myIsShowLibraryContents;
    private Tree myTree;
    private PsiClass mySelectedClass;
    private BaseProjectTreeBuilder myBuilder;
    private TabbedPaneWrapper myTabbedPane;
    private ChooseByNamePanel myGotoByNamePanel;
    private PsiClass myInitialClass;
    private JTextArea textPane;

    private ListModel listModel;
    private JList<ListPsiMethod> methodJList;
    private static final Comparator<ListPsiMethod> c = (var0, var1) -> var0.getName().compareToIgnoreCase(var1.getName());

    private String code;

    public GeneratorDlg(String title, Project project) {
        this(title, project, PsiClass.class, null);
    }

    public GeneratorDlg(String title, Project project, Class<PsiClass> elementClass, @Nullable PsiClass initialClass) {
        this(title, project, GlobalSearchScope.projectScope(project), elementClass, (Filter) null, initialClass);
    }

    public GeneratorDlg(String title, @NotNull Project project, GlobalSearchScope scope, @NotNull Class<PsiClass> elementClass, @Nullable Filter<PsiClass> classFilter, @Nullable PsiClass initialClass) {
        this(title, project, scope, elementClass, classFilter, null, initialClass, false, true);
    }

    public GeneratorDlg(String title, @NotNull Project project, GlobalSearchScope scope, @NotNull Class<PsiClass> elementClass, @Nullable Filter<PsiClass> classFilter, @Nullable PsiClass baseClass, @Nullable PsiClass initialClass, boolean isShowMembers, boolean isShowLibraryContents) {
        super(project, true);
        this.mySelectedClass = null;
        this.myScope = scope;
        this.myElementClass = elementClass;
        this.myClassFilter = classFilter == null ? this.allFilter() : classFilter;
        this.myBaseClass = baseClass;
        this.myInitialClass = initialClass;
        this.myIsShowMembers = isShowMembers;
        this.myIsShowLibraryContents = isShowLibraryContents;
        this.setTitle(title);
        this.myProject = project;

        this.init();
        if (initialClass != null) {
            this.select(initialClass);
        }

        this.handleSelectionChanged();
    }

    private Filter<PsiClass> allFilter() {
        return element -> true;
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new VerticalFlowLayout());
        DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());
        this.myTree = new Tree(model);
        ProjectAbstractTreeStructureBase treeStructure = new AbstractProjectTreeStructure(this.myProject) {
            @Override
            public boolean isFlattenPackages() {
                return false;
            }

            @Override
            public boolean isShowMembers() {
                return GeneratorDlg.this.myIsShowMembers;
            }

            @Override
            public boolean isHideEmptyMiddlePackages() {
                return true;
            }

            @Override
            public boolean isAbbreviatePackageNames() {
                return false;
            }

            @Override
            public boolean isShowLibraryContents() {
                return GeneratorDlg.this.myIsShowLibraryContents;
            }

            @Override
            public boolean isShowModules() {
                return false;
            }
        };
        this.myBuilder = new ProjectTreeBuilder(this.myProject, this.myTree, model, AlphaComparator.INSTANCE, treeStructure);
        this.myTree.setRootVisible(false);
        this.myTree.setShowsRootHandles(true);
        this.myTree.expandRow(0);
        this.myTree.getSelectionModel().setSelectionMode(1);
        this.myTree.setCellRenderer(new NodeRenderer());
        UIUtil.setLineStyleAngled(this.myTree);
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(this.myTree);
        scrollPane.setPreferredSize(JBUI.size(500, 100));
        scrollPane.putClientProperty(UIUtil.KEEP_BORDER_SIDES, 13);
        this.myTree.addTreeSelectionListener(e -> GeneratorDlg.this.handleSelectionChanged());
        new TreeSpeedSearch(this.myTree);
        this.myTabbedPane = new TabbedPaneWrapper(this.getDisposable());
        final JPanel dummyPanel = new JPanel(new BorderLayout());
        String name = null;
        this.myGotoByNamePanel = new ChooseByNamePanel(this.myProject, this.createChooseByNameModel(), (String) name, this.myScope.isSearchInLibraries(), this.getContext()) {
            @Override
            protected void showTextFieldPanel() {
            }

            @Override
            protected void close(boolean isOk) {
                super.close(isOk);
                if (isOk) {
                    GeneratorDlg.this.doOKAction();
                } else {
                    GeneratorDlg.this.doCancelAction();
                }

            }

            @Override
            @NotNull
            protected Set<Object> filter(@NotNull Set<Object> elements) {
                Set var10000 = GeneratorDlg.this.doFilter(elements);
                return var10000;
            }

            @Override
            protected void initUI(ChooseByNamePopupComponent.Callback callback, ModalityState modalityState, boolean allowMultipleSelection) {
                super.initUI(callback, modalityState, allowMultipleSelection);
                dummyPanel.add(GeneratorDlg.this.myGotoByNamePanel.getPanel(), "Center");
                IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(IdeFocusTraversalPolicy.getPreferredFocusedComponent(GeneratorDlg.this.myGotoByNamePanel.getPanel()), true));
            }

            @Override
            protected void showList() {
                super.showList();
                if (GeneratorDlg.this.myInitialClass != null && this.myList.getModel().getSize() > 0) {
                    this.myList.setSelectedValue(GeneratorDlg.this.myInitialClass, true);
                    GeneratorDlg.this.myInitialClass = null;
                }

            }

            @Override
            protected void chosenElementMightChange() {
                GeneratorDlg.this.handleSelectionChanged();
            }
        };
        Disposer.register(this.myDisposable, this.myGotoByNamePanel);
        this.myTabbedPane.addTab(IdeBundle.message("tab.chooser.search.by.name", new Object[0]), dummyPanel);
        this.myTabbedPane.addTab(IdeBundle.message("tab.chooser.project", new Object[0]), scrollPane);
        this.myGotoByNamePanel.invoke(new GeneratorDlg.MyCallback(), this.getModalityState(), false);
        this.myTabbedPane.addChangeListener(e -> GeneratorDlg.this.handleSelectionChanged());
        Component component = this.myTabbedPane.getComponent();
        panel.add(component);
        listModel = new ListModel(c);
        methodJList = new JBList<>(listModel);
        methodJList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        methodJList.addListSelectionListener(e -> SwingUtilities.invokeLater(() -> {
            List<ListPsiMethod> methods = methodJList.getSelectedValuesList();
            SelectedInfo info = new SelectedInfo();
            info.setPsiClass(calcSelectedClass());
            info.setMethods(methods);
            code = info.toString();
            textPane.setText(code);

        }));
        JScrollPane listScrollPane = ScrollPaneFactory.createScrollPane(methodJList);
        scrollPane.setPreferredSize(JBUI.size(500, 100));
        panel.add(new JLabel("Methods:"));
        panel.add(listScrollPane);
        textPane = new JTextArea();
        textPane.setEditable(false);
        textPane.setText("Generate Code");
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textPane);
        textScrollPane.setPreferredSize(JBUI.size(500, 100));
        panel.add(new JLabel("Result:"));
        panel.add(textScrollPane);
        return panel;
    }

    private Set<Object> doFilter(Set<Object> elements) {
        Set<Object> result = new LinkedHashSet();
        Iterator var3 = elements.iterator();

        while (var3.hasNext()) {
            Object o = var3.next();
            if (this.myElementClass.isInstance(o) && this.getFilter().isAccepted((PsiClass) o)) {
                result.add(o);
            }
        }

        return result;
    }

    protected ChooseByNameModel createChooseByNameModel() {
        if (this.myBaseClass == null) {
            return new GeneratorDlg.MyGotoClassModel(this.myProject, this);
        } else {
            GeneratorDlg.BaseClassInheritorsProvider inheritorsProvider = this.getInheritorsProvider(this.myBaseClass);
            if (inheritorsProvider != null) {
                return new GeneratorDlg.SubclassGotoClassModel(this.myProject, this, inheritorsProvider);
            } else {
                throw new IllegalStateException("inheritors provider is null");
            }
        }
    }

    private void handleSelectionChanged() {
        PsiClass selection = this.calcSelectedClass();
        if (selection != null) {
            listModel.setMethods(selection.getMethods());
            listModel.fireContentsChanged(methodJList);
        }
//        this.setOKActionEnabled(selection != null);

    }

    @Override
    protected void doOKAction() {
        this.mySelectedClass = this.calcSelectedClass();
        if (this.mySelectedClass != null) {
            if (!this.myClassFilter.isAccepted(this.mySelectedClass)) {
                Messages.showErrorDialog(this.myTabbedPane.getComponent(), SymbolPresentationUtil.getSymbolPresentableText(this.mySelectedClass) + " is not acceptable");
            } else {
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable tText = new StringSelection(textPane.getText());
                clip.setContents(tText, null);
                GeneratorDlg.super.doOKAction();
            }
        }
    }

    @Override
    public PsiClass getSelected() {
        return this.mySelectedClass;
    }

    @Override
    public void select(@NotNull PsiClass aClass) {
        this.selectElementInTree(aClass);
    }

    @Override
    public void selectDirectory(@NotNull PsiDirectory directory) {
        this.selectElementInTree(directory);
    }

    @Override
    public void showDialog() {
        this.show();
    }

    @Override
    public void showPopup() {
        ChooseByNamePopup popup = ChooseByNamePopup.createPopup(this.myProject, this.createChooseByNameModel(), this.getContext());
        popup.invoke(new ChooseByNamePopupComponent.Callback() {
            @Override
            public void elementChosen(Object element) {
                GeneratorDlg.this.mySelectedClass = (PsiClass) element;
                ((Navigatable) element).navigate(true);
            }
        }, this.getModalityState(), true);
    }

    private PsiClass getContext() {
        return this.myBaseClass != null ? this.myBaseClass : this.myInitialClass;
    }

    private void selectElementInTree(@NotNull PsiElement element) {

        ApplicationManager.getApplication().invokeLater(() -> {
            if (this.myBuilder != null) {
                VirtualFile vFile = PsiUtilCore.getVirtualFile(element);
                this.myBuilder.select(element, vFile, false);
            }
        }, this.getModalityState());
    }

    private ModalityState getModalityState() {
        return ModalityState.stateForComponent(this.getRootPane());
    }

    @Nullable
    protected PsiClass calcSelectedClass() {
        if (this.getTabbedPane().getSelectedIndex() == 0) {
            return (PsiClass) this.getGotoByNamePanel().getChosenElement();
        } else {
            TreePath path = this.getTree().getSelectionPath();
            if (path == null) {
                return null;
            } else {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                return this.getSelectedFromTreeUserObject(node);
            }
        }
    }

    @Override
    public void dispose() {
        if (this.myBuilder != null) {
            Disposer.dispose(this.myBuilder);
            this.myBuilder = null;
        }

        super.dispose();
    }

    @Override
    protected String getDimensionServiceKey() {
        return "#com.intellij.ide.util.TreeClassChooserDialog";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.myGotoByNamePanel.getPreferredFocusedComponent();
    }

    @NotNull
    protected Project getProject() {
        Project var10000 = this.myProject;
        return var10000;
    }

    GlobalSearchScope getScope() {
        return this.myScope;
    }

    @NotNull
    protected Filter<PsiClass> getFilter() {
        Filter var10000 = this.myClassFilter;

        return var10000;
    }

    PsiClass getBaseClass() {
        return this.myBaseClass;
    }

    PsiClass getInitialClass() {
        return this.myInitialClass;
    }

    protected TabbedPaneWrapper getTabbedPane() {
        return this.myTabbedPane;
    }

    protected Tree getTree() {
        return this.myTree;
    }

    protected ChooseByNamePanel getGotoByNamePanel() {
        return this.myGotoByNamePanel;
    }

    private class MyCallback extends ChooseByNamePopupComponent.Callback {
        private MyCallback() {
        }

        @Override
        public void elementChosen(Object element) {
            GeneratorDlg.this.mySelectedClass = (PsiClass) element;
            GeneratorDlg.this.close(0);
        }
    }

    private static class SubclassGotoClassModel<T extends PsiNamedElement> extends GeneratorDlg.MyGotoClassModel {
        private final GeneratorDlg.BaseClassInheritorsProvider myInheritorsProvider;
        private boolean myFastMode;

        public SubclassGotoClassModel(@NotNull Project project, @NotNull GeneratorDlg treeClassChooserDialog, @NotNull GeneratorDlg.BaseClassInheritorsProvider inheritorsProvider) {
            super(project, treeClassChooserDialog);
            this.myFastMode = true;
            this.myInheritorsProvider = inheritorsProvider;

            assert this.myInheritorsProvider.getBaseClass() != null;

        }

        @Override
        public void processNames(final Processor<String> nameProcessor, boolean checkBoxState) {
            if (this.myFastMode) {
                this.myFastMode = this.myInheritorsProvider.searchForInheritorsOfBaseClass().forEach(new Processor<PsiClass>() {
                    private final long start = System.currentTimeMillis();

                    @Override
                    public boolean process(PsiClass aClass) {
                        if (System.currentTimeMillis() - this.start > 500L && !ApplicationManager.getApplication().isUnitTestMode()) {
                            return false;
                        } else {
                            if (GeneratorDlg.SubclassGotoClassModel.this.getTreeClassChooserDialog().getFilter().isAccepted(aClass) && aClass.getName() != null) {
                                nameProcessor.process(aClass.getName());
                            }

                            return true;
                        }
                    }
                });
            }

            if (!this.myFastMode) {
                String[] var3 = this.myInheritorsProvider.getNames();
                int var4 = var3.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    String name = var3[var5];
                    nameProcessor.process(name);
                }
            }

        }

        @Override
        protected boolean isAccepted(PsiClass aClass) {
            if (this.myFastMode) {
                return this.getTreeClassChooserDialog().getFilter().isAccepted(aClass);
            } else {
                return (aClass == this.getTreeClassChooserDialog().getBaseClass() || this.myInheritorsProvider.isInheritorOfBaseClass(aClass)) && this.getTreeClassChooserDialog().getFilter().isAccepted(aClass);
            }
        }
    }

    public abstract static class BaseClassInheritorsProvider {
        private final PsiClass myBaseClass;
        private final GlobalSearchScope myScope;

        public BaseClassInheritorsProvider(PsiClass baseClass, GlobalSearchScope scope) {
            this.myBaseClass = baseClass;
            this.myScope = scope;
        }

        public PsiClass getBaseClass() {
            return this.myBaseClass;
        }

        public GlobalSearchScope getScope() {
            return this.myScope;
        }

        @NotNull
        protected abstract Query<PsiClass> searchForInheritors(PsiClass var1, GlobalSearchScope var2, boolean var3);

        protected abstract boolean isInheritor(PsiClass var1, PsiClass var2, boolean var3);

        protected abstract String[] getNames();

        protected Query<PsiClass> searchForInheritorsOfBaseClass() {
            return this.searchForInheritors(this.myBaseClass, this.myScope, true);
        }

        protected boolean isInheritorOfBaseClass(PsiClass aClass) {
            return this.isInheritor(aClass, this.myBaseClass, true);
        }
    }

    protected static class MyGotoClassModel extends GotoClassModel2 {
        private final GeneratorDlg myTreeClassChooserDialog;

        public MyGotoClassModel(@NotNull Project project, GeneratorDlg treeClassChooserDialog) {
            super(project);
            this.myTreeClassChooserDialog = treeClassChooserDialog;
        }

        GeneratorDlg getTreeClassChooserDialog() {
            return this.myTreeClassChooserDialog;
        }

        @Override
        @NotNull
        public Object[] getElementsByName(@NotNull String name, @NotNull FindSymbolParameters parameters, @NotNull ProgressIndicator canceled) {
            String patternName = parameters.getLocalPatternName();
            java.util.List<PsiClass> classes = this.myTreeClassChooserDialog.getClassesByName(name, parameters.isSearchInLibraries(), patternName, this.myTreeClassChooserDialog.getScope());
            Object[] var10000;
            if (classes.size() == 0) {
                var10000 = ArrayUtil.EMPTY_OBJECT_ARRAY;

                return var10000;
            } else if (classes.size() == 1) {
                var10000 = this.isAccepted(classes.get(0)) ? ArrayUtil.toObjectArray(classes) : ArrayUtil.EMPTY_OBJECT_ARRAY;
                return var10000;
            } else {
                Set<String> qNames = ContainerUtil.newHashSet();
                List<PsiClass> list = new ArrayList(classes.size());
                Iterator<PsiClass> var8 = classes.iterator();

                while (var8.hasNext()) {
                    PsiClass aClass = var8.next();
                    if (qNames.add(this.getFullName(aClass)) && this.isAccepted(aClass)) {
                        list.add(aClass);
                    }
                }

                var10000 = ArrayUtil.toObjectArray(list);

                return var10000;
            }
        }

        @Override
        @Nullable
        public String getPromptText() {
            return null;
        }

        protected boolean isAccepted(PsiClass aClass) {
            return this.myTreeClassChooserDialog.getFilter().isAccepted(aClass);
        }
    }

    @Nullable
    private static Filter<PsiClass> createFilter(@Nullable final ClassFilter classFilter) {
        return classFilter == null ? null : new Filter<PsiClass>() {
            @Override
            public boolean isAccepted(PsiClass element) {
                return (Boolean) ReadAction.compute(() -> {
                    return classFilter.isAccepted(element);
                });
            }
        };
    }

    @Nullable
    protected PsiClass getSelectedFromTreeUserObject(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (!(userObject instanceof ClassTreeNode)) {
            return null;
        } else {
            ClassTreeNode descriptor = (ClassTreeNode) userObject;
            return descriptor.getPsiClass();
        }
    }

    @NotNull
    protected List<PsiClass> getClassesByName(String name, boolean checkBoxState, String pattern, GlobalSearchScope searchScope) {
        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(this.getProject());
        PsiClass[] classes = cache.getClassesByName(name, checkBoxState ? searchScope : GlobalSearchScope.projectScope(this.getProject()).intersectWith(searchScope));
        ArrayList var10000 = ContainerUtil.newArrayList(classes);

        return var10000;
    }

    @NotNull
    protected GeneratorDlg.BaseClassInheritorsProvider getInheritorsProvider(@NotNull PsiClass baseClass) {

        GeneratorDlg.JavaInheritorsProvider var10000 = new GeneratorDlg.JavaInheritorsProvider(this.getProject(), baseClass, this.getScope());

        return var10000;
    }

    public static class InheritanceJavaClassFilterImpl implements ClassFilter {
        private final PsiClass myBase;
        private final boolean myAcceptsSelf;
        private final boolean myAcceptsInner;
        @NotNull
        private final Condition<? super PsiClass> myAdditionalCondition;

        public InheritanceJavaClassFilterImpl(PsiClass base, boolean acceptsSelf, boolean acceptInner, @Nullable Condition<? super PsiClass> additionalCondition) {
            this.myAcceptsSelf = acceptsSelf;
            this.myAcceptsInner = acceptInner;
            if (additionalCondition == null) {
                additionalCondition = Conditions.alwaysTrue();
            }

            this.myAdditionalCondition = additionalCondition;
            this.myBase = base;
        }

        @Override
        public boolean isAccepted(PsiClass aClass) {
            if (!this.myAcceptsInner && !(aClass.getParent() instanceof PsiJavaFile)) {
                return false;
            } else if (!this.myAdditionalCondition.value(aClass)) {
                return false;
            } else {
                return this.myAcceptsSelf || !aClass.getManager().areElementsEquivalent(aClass, this.myBase);
            }
        }
    }

    private static class JavaInheritorsProvider extends GeneratorDlg.BaseClassInheritorsProvider {
        private final Project myProject;

        public JavaInheritorsProvider(Project project, PsiClass baseClass, GlobalSearchScope scope) {
            super(baseClass, scope);
            this.myProject = project;
        }

        @Override
        @NotNull
        protected Query<PsiClass> searchForInheritors(PsiClass baseClass, GlobalSearchScope searchScope, boolean checkDeep) {
            Query var10000 = ClassInheritorsSearch.search(baseClass, searchScope, checkDeep);
            return var10000;
        }

        @Override
        protected boolean isInheritor(PsiClass clazz, PsiClass baseClass, boolean checkDeep) {
            return clazz.isInheritor(baseClass, checkDeep);
        }

        @Override
        protected String[] getNames() {
            return PsiShortNamesCache.getInstance(this.myProject).getAllClassNames();
        }
    }

    public String getCode() {
        return code;
    }
}
