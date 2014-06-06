/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.wizard;

import com.intellij.ui.wizard.WizardModel;
import com.intellij.ui.wizard.WizardStep;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;

/**
 * Represents the model of a wizard
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
public class ForgeWizardModel extends WizardModel
{
    private CommandController originalController;

    @SuppressWarnings("unchecked")
    public ForgeWizardModel(CommandController originalController)
    {
        super(originalController.getMetadata().getName());
        this.originalController = originalController;

        addWizardSteps();
    }

    private void addWizardSteps()
    {
        add(new ForgeWizardStep(originalController));
    }

    @Override
    public boolean isFirst(WizardStep step)
    {
        CommandController controller = ((ForgeWizardStep) step).getController();
        return controller.getCommand() == originalController.getCommand();
    }

    @Override
    public boolean isLast(WizardStep step)
    {
        CommandController controller = ((ForgeWizardStep) step).getController();

        boolean result = true;

        if (controller instanceof WizardCommandController)
        {
            result = ((WizardCommandController) controller).canExecute();
        }

        return result;
    }
}
