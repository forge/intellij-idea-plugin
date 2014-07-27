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
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilderRegistry;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;
import org.jboss.forge.plugin.idea.ui.listeners.ValueChangeListener;
import org.jboss.forge.plugin.idea.util.ForgeNotifications;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import javax.swing.*;
import java.util.HashMap;
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
    private final NavigationState navigationState;

    public ForgeWizardStep(ForgeWizardModel model, CommandController controller)
    {
        this.model = model;
        this.navigationState = new NavigationState(model, controller);

        try
        {
            controller.initialize();
        }
        catch (Exception e)
        {
            ForgeNotifications.showErrorMessage(e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JComponent prepare(WizardNavigationState state)
    {
        JPanel container = new JPanel(new MigLayout("fillx,wrap 2",
                "[left]rel[grow,fill]"));

        Map<String, ForgeComponent> components = new HashMap<>();
        UIContext context = navigationState.getController().getContext();
        ValueChangeListener listener = new ValueChangeListener(model, components, navigationState);

        for (InputComponent input : navigationState.getController().getInputs().values())
        {
            ComponentBuilder builder = ComponentBuilderRegistry.INSTANCE.getBuilderFor(input);
            builder.setContext(context);

            ForgeComponent component = builder.build(input);
            component.buildUI(container);
            component.setValueChangeListener(listener);

            components.put(input.getName(), component);
        }

        navigationState.refreshNavigationState();
        listener.updateComponentsState();

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
        if (!(navigationState.isWizardController()))
        {
            return null;
        }

        try
        {
            CommandController nextController;

            if (forward)
            {
                nextController = navigationState.getWizardCommandController().next();
            }
            else
            {
                nextController = navigationState.getWizardCommandController().previous();
            }

            return new ForgeWizardStep(this.model, nextController);
        }
        catch (Exception e)
        {
            model.getDialog().setErrorMessage(e.getMessage());
            e.printStackTrace();
            return this;
        }
    }

    @Override
    public boolean onFinish()
    {
        try
        {
            Result result = navigationState.getController().execute();
            ForgeNotifications.showExecutionResult(result);

            UIContext context = navigationState.getController().getContext();
            IDEUtil.refreshProject(context);
            IDEUtil.openSelection(context);
        }
        catch (Exception e)
        {
            ForgeNotifications.showErrorMessage(e);
            e.printStackTrace();
        }
        finally
        {
            try
            {
                navigationState.getController().close();
            }
            catch (Exception e)
            {
                ForgeNotifications.showErrorMessage(e);
                e.printStackTrace();
            }
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
        return navigationState.getController().getMetadata().getDescription();
    }

    public void refreshNavigationState()
    {
        this.navigationState.refreshNavigationState();
    }
}
