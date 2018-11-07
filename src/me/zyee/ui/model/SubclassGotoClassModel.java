package me.zyee.ui.model;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.Processor;
import me.zyee.ui.BaseClassInheritorsProvider;
import me.zyee.ui.dialog.GeneratorDlg;
import org.jetbrains.annotations.NotNull;

/**
 * @author yee
 * @date 2018/11/2
 */
public class SubclassGotoClassModel<T extends PsiNamedElement> extends MyGotoClassModel {
    private final BaseClassInheritorsProvider myInheritorsProvider;
    private boolean myFastMode;

    public SubclassGotoClassModel(@NotNull Project project, @NotNull GeneratorDlg treeClassChooserDialog, @NotNull BaseClassInheritorsProvider inheritorsProvider) {
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
                        if (SubclassGotoClassModel.this.getTreeClassChooserDialog().getFilter().isAccepted(aClass) && aClass.getName() != null) {
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
