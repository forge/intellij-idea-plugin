/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.ui;

import com.intellij.ui.wizard.WizardNavigationState;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.plugin.idea.ui.wizard.ForgeWizardModel;

/**
 * @author <a href="mailto:danielsoro@gmail.com">Daniel Cunha (soro)</a>
 */
public class NavigationState
{

    private final ForgeWizardModel model;
    private final CommandController controller;

    public NavigationState(ForgeWizardModel model, CommandController controller)
    {
        this.model = model;
        this.controller = controller;
    }

    public void refreshNavigationState()
    {
        WizardNavigationState navigationState = model.getCurrentNavigationState();

        navigationState.CANCEL.setEnabled(true);
        navigationState.PREVIOUS.setEnabled(isPreviousEnabled());
        navigationState.NEXT.setEnabled(isNextEnabled());
        navigationState.FINISH.setEnabled(isFinishEnabled());
    }

    public boolean isPreviousEnabled()
    {
        if (!isWizardController())
        {
            return false;
        }

        return getWizardCommandController().canMoveToPreviousStep();
    }

    public boolean isNextEnabled()
    {
        if (!isWizardController())
        {
            return false;
        }

        return getWizardCommandController().canMoveToNextStep();
    }

    public boolean isWizardController()
    {
        return controller instanceof WizardCommandController;
    }

    public boolean isFinishEnabled()
    {
        return controller.canExecute();
    }

    public WizardCommandController getWizardCommandController()
    {
        return (WizardCommandController) controller;
    }

    public CommandController getController()
    {
        return controller;
    }
}
