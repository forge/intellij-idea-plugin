/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.ui.components.JBLabel;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;

import java.awt.*;
import java.util.List;

/**
 * Component with label.
 *
 * @author Adam Wyłuda
 */
public class LabeledComponent extends ForgeComponent
{
    private final JBLabel label;
    private final ForgeComponent component;

    public LabeledComponent(InputComponent<?, Object> input, ForgeComponent component)
    {
        this.label = new JBLabel(getLabelValue(input));
        this.component = component;
    }

    @Override
    public void buildUI(Container container)
    {
        container.add(label);
        component.buildUI(container);
    }

    private String getLabelValue(InputComponent<?, Object> input)
    {
        String labelValue = input.getLabel() == null ? input.getName() : input.getLabel();
        labelValue = labelValue.endsWith(":") ? labelValue : labelValue + ":";
        return labelValue;
    }

    @Override
    public void displayMessages(List<UIMessage> messages)
    {
        component.displayMessages(messages);
    }

    @Override
    public void setErrorMessage(UIMessage message)
    {
        component.setErrorMessage(message);
    }

    @Override
    public void clearErrorMessage()
    {
        component.clearErrorMessage();
    }

    @Override
    public void setValueChangeListener(Runnable valueChangeListener)
    {
        component.setValueChangeListener(valueChangeListener);
    }
}