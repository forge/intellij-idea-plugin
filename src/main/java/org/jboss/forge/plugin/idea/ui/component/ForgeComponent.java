/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Container;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.plugin.idea.service.ForgeService;
import org.jboss.forge.plugin.idea.ui.listeners.ValueChangeListener;

/**
 * Represents Forge input component.
 *
 * @author Adam Wyłuda
 */
public abstract class ForgeComponent
{
    protected ValueChangeListener valueChangeListener;

    protected ConverterFactory converterFactory = ForgeService.getInstance().getConverterFactory();

    public abstract void buildUI(Container container);

    /**
     * Displays validation messages.
     */
    public void displayMessages(java.util.List<UIMessage> messages)
    {
        for (UIMessage message : messages)
        {
            if (message.getSeverity().equals(UIMessage.Severity.ERROR))
            {
                setErrorMessage(message);
                return;
            }
        }
        clearErrorMessage();
    }

    public void setErrorMessage(UIMessage message)
    {
    }

    public void clearErrorMessage()
    {
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener)
    {
        this.valueChangeListener = valueChangeListener;
    }

    public abstract void updateState();
}
