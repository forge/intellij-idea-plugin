/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.wizard;

import com.intellij.openapi.project.Project;
import com.intellij.ui.wizard.WizardDialog;

public class ForgeWizardDialog extends WizardDialog<ForgeWizardModel>
{

    public ForgeWizardDialog(ForgeWizardModel model)
    {
        super((Project) null, false, model);
    }

    @Override
    public String getTitle()
    {
        return super.getTitle();
    }
}
