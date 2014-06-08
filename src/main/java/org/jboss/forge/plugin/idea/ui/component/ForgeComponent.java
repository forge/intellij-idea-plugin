/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import org.jboss.forge.addon.ui.output.UIMessage;

import java.awt.*;

/**
 * Represents Forge input component.
 *
 * @author Adam Wyłuda
 */
public abstract class ForgeComponent
{
    protected Runnable valueChangeListener = new Runnable()
    {
        @Override
        public void run()
        {
        }
    };

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

    public void setValueChangeListener(Runnable valueChangeListener)
    {
        this.valueChangeListener = valueChangeListener;
    }
}
