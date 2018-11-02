package me.zyee;

import com.intellij.ide.projectView.impl.AbstractProjectTreeStructure;
import com.intellij.openapi.project.Project;
import me.zyee.ui.GeneratorDlg;

/**
 * @author yee
 * @date 2018/11/2
 */
public class GeneratorProjectTreeStructure extends AbstractProjectTreeStructure {
    private GeneratorDlg dlg;

    public GeneratorProjectTreeStructure(Project project, GeneratorDlg dlg) {
        super(project);
        this.dlg = dlg;
    }

    @Override
    public boolean isShowMembers() {
        return dlg.isShowMembers();
    }

    @Override
    public boolean isShowModules() {
        return false;
    }

    @Override
    public boolean isFlattenPackages() {
        return false;
    }

    @Override
    public boolean isAbbreviatePackageNames() {
        return false;
    }

    @Override
    public boolean isHideEmptyMiddlePackages() {
        return false;
    }

    @Override
    public boolean isShowLibraryContents() {
        return dlg.isShowLibraryContents();
    }
}
