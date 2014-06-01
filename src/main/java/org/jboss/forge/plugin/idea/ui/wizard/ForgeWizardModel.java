/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.wizard;

import com.intellij.ui.wizard.WizardModel;
import com.intellij.ui.wizard.WizardNavigationState;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.wizard.UIWizard;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Represents the model of a wizard
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeWizardModel extends WizardModel
{
    private UICommand originalCommand;
    private UIContext context;
    private List<ForgeWizardStep> steps;

    @SuppressWarnings("unchecked")
    public ForgeWizardModel(UICommand command, UIContext context)
    {
        super(command.getMetadata(context).getName());
        this.context = context;

        this.originalCommand = command;
        try
        {
            Field field = WizardModel.class.getDeclaredField("mySteps");
            field.setAccessible(true);
            steps = (List<ForgeWizardStep>) field.get(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        addWizardSteps();
        resetNavigationState();
    }

    private void resetNavigationState()
    {
        WizardNavigationState navState = getCurrentNavigationState();
        boolean isWizard = originalCommand instanceof UIWizard;
        navState.setEnabledToAll(isWizard);
        navState.PREVIOUS.setEnabled(false);
    }

    private void addWizardSteps()
    {
        add(new ForgeWizardStep(originalCommand, context));

        // TODO: Add more steps
    }

    public List<ForgeWizardStep> getSteps()
    {
        return steps;
    }

    public UIContext getContext()
    {
        return context;
    }

    public UICommand getOriginalCommand()
    {
        return originalCommand;
    }

}
