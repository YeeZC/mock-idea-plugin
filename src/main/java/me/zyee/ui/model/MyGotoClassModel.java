package me.zyee.ui.model;

import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FindSymbolParameters;
import me.zyee.ui.dialog.GeneratorDlg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/11/2
 */
public class MyGotoClassModel extends GotoClassModel2 {
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
        if (classes.size() == 0) {
            return ArrayUtil.EMPTY_OBJECT_ARRAY;
        } else if (classes.size() == 1) {
            return this.isAccepted(classes.get(0)) ? ArrayUtil.toObjectArray(classes) : ArrayUtil.EMPTY_OBJECT_ARRAY;
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

            return ArrayUtil.toObjectArray(list);
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
