/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.wizard;

import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;
import net.miginfocom.swing.MigLayout;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilderRegistry;

import javax.swing.*;

/**
 * Represents Forge wizard step.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
public class ForgeWizardStep extends WizardStep<ForgeWizardModel>
{
    private final ForgeWizardModel model;
    private final CommandController controller;

    public ForgeWizardStep(ForgeWizardModel model, CommandController controller)
    {
        this.model = model;
        this.controller = controller;

        try
        {
            controller.initialize();
        }
        catch (Exception e)
        {
            // TODO Handle Wizard exceptions
            e.printStackTrace();
        }

        refreshNavigationState();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JComponent prepare(WizardNavigationState state)
    {
        JPanel container = new JPanel(new MigLayout("fillx,wrap 2",
                "[left]rel[grow,fill]", "[]10[]"));

        // TODO Build panel for UICommands

        for (InputComponent input : controller.getInputs().values())
        {
            ComponentBuilder builder = ComponentBuilderRegistry.INSTANCE
                    .getBuilderFor(input);
            builder.build(input, container);
        }

        return container;
    }

    @Override
    public WizardStep onNext(ForgeWizardModel model)
    {
        return navigate(true);
    }

    @Override
    public WizardStep onPrevious(ForgeWizardModel model)
    {
        return navigate(false);
    }

    private WizardStep navigate(boolean forward)
    {
        // If it's not a wizard, we don't care
        if (!(isWizardController()))
        {
            return null;
        }

        try
        {
            CommandController nextController;

            if (forward)
            {
                nextController = getWizardCommandController().next();
            }
            else
            {
                nextController = getWizardCommandController().previous();
            }

            return new ForgeWizardStep(this.model, nextController);
        }
        catch (Exception e)
        {
            // TODO Handle Wizard exceptions
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onFinish()
    {
        try
        {
            controller.execute();
        }
        catch (Exception e)
        {
            // TODO Handle Wizard exceptions
            e.printStackTrace();
        }

        return true;
    }

    public void refreshNavigationState()
    {
        WizardNavigationState navigationState = model.getCurrentNavigationState();

        navigationState.CANCEL.setEnabled(true);
        navigationState.PREVIOUS.setEnabled(isPreviousEnabled());
        navigationState.NEXT.setEnabled(isNextEnabled());
        navigationState.FINISH.setEnabled(isFinishEnabled());
    }

    private boolean isPreviousEnabled()
    {
        if (!isWizardController())
        {
            return false;
        }

        return getWizardCommandController().canMoveToPreviousStep();
    }

    private boolean isNextEnabled()
    {
        if (!isWizardController())
        {
            return false;
        }

        return getWizardCommandController().canMoveToNextStep();
    }

    private boolean isFinishEnabled()
    {
        if (!isWizardController())
        {
            return false;
        }

        return getWizardCommandController().canExecute();
    }

    private boolean isWizardController()
    {
        return controller instanceof WizardCommandController;
    }

    private WizardCommandController getWizardCommandController()
    {
        return (WizardCommandController) controller;
    }
}
