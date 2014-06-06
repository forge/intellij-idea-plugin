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

import javax.swing.*;

/**
 * Represents Forge wizard step.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
public class ForgeWizardStep extends WizardStep<ForgeWizardModel>
{
    private CommandController controller;

    public ForgeWizardStep(CommandController controller)
    {
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
    }

    public CommandController getController()
    {
        return controller;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JComponent prepare(WizardNavigationState state)
    {
        JPanel container = new JPanel(new MigLayout("fillx,wrap 2",
                "[left]rel[grow,fill]", "[]10[]"));

        // TODO Build panel for UICommand

//        for (InputComponent input : uiBuilder.getInputs())
//        {
//            ComponentBuilder builder = ComponentBuilderRegistry.INSTANCE
//                    .getBuilderFor(input);
//            builder.build(input, container);
//        }

        return container;
    }

    @Override
    public ForgeWizardStep onNext(ForgeWizardModel model)
    {
        // If it's not a wizard, we don't care
        if (!(controller instanceof WizardCommandController))
        {
            return null;
        }

        try
        {
            return new ForgeWizardStep(((WizardCommandController) controller).next());
        }
        catch (Exception e)
        {
            // TODO Handle Wizard exceptions
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public WizardStep onPrevious(ForgeWizardModel model)
    {
        // If it's not a wizard, we don't care
        if (!(controller instanceof WizardCommandController))
        {
            return null;
        }

        try
        {
            return new ForgeWizardStep(((WizardCommandController) controller).previous());
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
}
