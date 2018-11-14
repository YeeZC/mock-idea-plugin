package me.zyee.ui.dialog;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.projectView.BaseProjectTreeBuilder;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectTreeBuilder;
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePanel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import me.zyee.GeneratorProjectTreeStructure;
import me.zyee.SelectInfoNode;
import me.zyee.config.MockSetting;
import me.zyee.ui.BaseClassInheritorsProvider;
import me.zyee.ui.JavaInheritorsProvider;
import me.zyee.ui.model.MyGotoClassModel;
import me.zyee.ui.model.SubclassGotoClassModel;
import me.zyee.ui.panel.CodePanel;
import me.zyee.ui.panel.PanelBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 这个dialog是抄的idea的
 *
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

    private CodePanel codePanel;

    private SelectInfoNode node;

    public GeneratorDlg(String title, Project project) {
        this(title, project, PsiClass.class, null);
    }

    public GeneratorDlg(String title, Project project, Class<PsiClass> elementClass, @Nullable PsiClass initialClass) {
        this(title, project, GlobalSearchScope.allScope(project), elementClass, null, initialClass);
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
        return element -> !MockSetting.getInstance().isInterfaceOnly() || element.isInterface();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new VerticalFlowLayout());
        panel.add(createSearchPanel());
        panel.add(createMethodListPanel());
        return panel;
    }

    private JComponent createSearchPanel() {
        JScrollPane scrollPane = initTreeScrollPanel();
        this.myTabbedPane = new TabbedPaneWrapper(this.getDisposable());
        final JPanel dummyPanel = new JPanel(new BorderLayout());
        this.myGotoByNamePanel = new ChooseByNamePanel(this.myProject, this.createChooseByNameModel(), "", this.myScope.isSearchInLibraries(), this.getContext()) {
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
                return GeneratorDlg.this.doFilter(elements);
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
        this.myTabbedPane.addTab(IdeBundle.message("tab.chooser.search.by.name"), dummyPanel);
        this.myTabbedPane.addTab(IdeBundle.message("tab.chooser.project"), scrollPane);
        this.myGotoByNamePanel.invoke(new GeneratorDlg.MyCallback(), this.getModalityState(), false);
        this.myTabbedPane.addChangeListener(e -> GeneratorDlg.this.handleSelectionChanged());
        return this.myTabbedPane.getComponent();
    }

    private JPanel createMethodListPanel() {
        codePanel = PanelBuilder.buildCodePanel(getProject(), () -> calcSelectedClass(), getDisposable());
        return codePanel;
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

    @NotNull
    private JScrollPane initTreeScrollPanel() {
        DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());
        this.myTree = new Tree(model);
        ProjectAbstractTreeStructureBase treeStructure = new GeneratorProjectTreeStructure(this.myProject, this);
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
        return scrollPane;
    }

    protected ChooseByNameModel createChooseByNameModel() {
        if (this.myBaseClass == null) {
            return new MyGotoClassModel(this.myProject, this);
        } else {
            BaseClassInheritorsProvider inheritorsProvider = this.getInheritorsProvider(this.myBaseClass);
            if (inheritorsProvider != null) {
                return new SubclassGotoClassModel(this.myProject, this, inheritorsProvider);
            } else {
                throw new IllegalStateException("inheritors provider is null");
            }
        }
    }

    @Override
    protected void doOKAction() {
        this.mySelectedClass = this.calcSelectedClass();
        if (this.mySelectedClass != null) {
            if (!this.myClassFilter.isAccepted(this.mySelectedClass)) {
                Messages.showErrorDialog(this.myTabbedPane.getComponent(), SymbolPresentationUtil.getSymbolPresentableText(this.mySelectedClass) + " is not acceptable");
            } else {
                node = codePanel.getNode();
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

    private void handleSelectionChanged() {
        PsiClass selection = this.calcSelectedClass();
        if (selection != null) {
            codePanel.loadClass();
        }
    }

    @NotNull
    protected Project getProject() {
        return this.myProject;
    }

    public GlobalSearchScope getScope() {
        return this.myScope;
    }

    @NotNull
    public Filter<PsiClass> getFilter() {
        return this.myClassFilter;
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

    public PsiClass getBaseClass() {
        return this.myBaseClass;
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
    public List<PsiClass> getClassesByName(String name, boolean checkBoxState, String pattern, GlobalSearchScope searchScope) {
        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(this.getProject());
        PsiClass[] classes = cache.getClassesByName(name, checkBoxState ? searchScope : GlobalSearchScope.projectScope(this.getProject()).intersectWith(searchScope));
        return ContainerUtil.newArrayList(classes);
    }

    @NotNull
    protected BaseClassInheritorsProvider getInheritorsProvider(@NotNull PsiClass baseClass) {

        return new JavaInheritorsProvider(this.getProject(), baseClass, this.getScope());
    }

    public boolean isShowMembers() {
        return myIsShowMembers;
    }

    public boolean isShowLibraryContents() {
        return myIsShowLibraryContents;
    }

    public SelectInfoNode getNode() {
        if (null != node) {
            node.setContains(new HashSet<>());
        }
        return node;
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
}
