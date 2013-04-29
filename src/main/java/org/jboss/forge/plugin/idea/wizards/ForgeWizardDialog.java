package org.jboss.forge.plugin.idea.wizards;

import com.intellij.openapi.project.Project;
import com.intellij.ui.wizard.WizardDialog;

public class ForgeWizardDialog extends WizardDialog<ForgeWizardModel> {

	public ForgeWizardDialog(ForgeWizardModel model) {
		super((Project) null, false, model);
	}

	@Override
	public String getTitle() {
		return super.getTitle();
	}
}
