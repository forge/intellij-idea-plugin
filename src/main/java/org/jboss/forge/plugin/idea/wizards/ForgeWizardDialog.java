package org.jboss.forge.plugin.idea.wizards;

import com.intellij.openapi.project.Project;
import com.intellij.ui.wizard.WizardDialog;

public class ForgeWizardDialog extends WizardDialog<ForgeWizardModel> {

    public ForgeWizardDialog(Project project, boolean canBeParent, ForgeWizardModel model) {
        super(project, canBeParent, model);
    }


}
