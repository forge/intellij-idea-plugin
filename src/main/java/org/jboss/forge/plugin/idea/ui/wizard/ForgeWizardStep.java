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
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilderRegistry;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Maps component builders by input component name.
     */
    private Map<String, ForgeComponent> components;

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
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JComponent prepare(WizardNavigationState state)
    {
        JPanel container = new JPanel(new MigLayout("fillx,wrap 2",
                "[left]rel[grow,fill]", "[]10[]"));

        components = new HashMap<>();

        for (InputComponent input : controller.getInputs().values())
        {
            ComponentBuilder builder =
                    ComponentBuilderRegistry.INSTANCE.getBuilderFor(input);
            ForgeComponent component = builder.build(input);
            component.buildUI(container);

            components.put(input.getName(), component);

            component.setValueChangeListener(new Runnable()
            {
                @Override
                public void run()
                {
                    refreshNavigationState();
                    validate();
                }
            });
        }

        refreshNavigationState();

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

    @Override
    public Icon getIcon()
    {
        return null;
    }

    @Override
    public String getExplanation()
    {
        return controller.getMetadata().getDescription();
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
        return controller.canExecute();
    }

    private boolean isWizardController()
    {
        return controller instanceof WizardCommandController;
    }

    private WizardCommandController getWizardCommandController()
    {
        return (WizardCommandController) controller;
    }

    private void validate()
    {
        List<UIMessage> allMessages = controller.validate();

        Map<String, List<UIMessage>> messagesByInputName = new HashMap<>();
        List<UIMessage> commandMessages = new ArrayList<>();

        for (String inputName : components.keySet())
        {
            messagesByInputName.put(inputName, new ArrayList<UIMessage>());
        }

        for (UIMessage message : allMessages)
        {
            if (message.getSource() == null)
            {
                commandMessages.add(message);
            }
            else
            {
                InputComponent source = message.getSource();
                messagesByInputName.get(source.getName()).add(message);
            }
        }

        processComponentMessages(messagesByInputName);
        processCommandMessages(commandMessages, allMessages);
    }

    private void processComponentMessages(Map<String, List<UIMessage>> messages)
    {
        for (Map.Entry<String, List<UIMessage>> entry : messages.entrySet())
        {
            ForgeComponent builder = components.get(entry.getKey());
            builder.displayMessages(entry.getValue());
        }
    }

    private void processCommandMessages(List<UIMessage> commandMessages, List<UIMessage> allMessages)
    {
        // Messages specific for command (not any input) should be displayed first
        for (UIMessage message : commandMessages)
        {
            if (message.getSeverity() == UIMessage.Severity.ERROR)
            {
                model.getDialog().setErrorMessage(message.getDescription());
                return;
            }
        }

        // Display first input validation error
        for (UIMessage message : allMessages)
        {
            if (message.getSeverity() == UIMessage.Severity.ERROR)
            {
                model.getDialog().setErrorMessage(message.getDescription());
                return;
            }
        }

        // If there are no errors
        model.getDialog().setErrorMessage(null);
    }
}
