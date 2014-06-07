/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.proxy.Proxies;

import javax.swing.*;
import java.awt.*;

public abstract class ComponentBuilder
{
    protected Runnable valueChangeListener;

    /**
     * Builds a UI Component object based on the input
     * <p/>
     * TODO Write JavaDoc
     *
     * @param input
     * @param container
     * @return
     */
    public abstract JComponent build(final InputComponent<?, Object> input,
                                     final Container container);

    /**
     * Returns the supported type this control may produce
     *
     * @return
     */
    protected abstract Class<?> getProducedType();

    /**
     * Returns the supported input type for this component
     *
     * @return
     */
    protected abstract String getSupportedInputType();

    /**
     * Returns the subclasses of {@link InputComponent}
     *
     * @return
     */
    protected abstract Class<?>[] getSupportedInputComponentTypes();

    /**
     * Tests if this builder may handle this specific input
     *
     * @param input
     * @return
     */
    public boolean handles(InputComponent<?, ?> input)
    {
        boolean handles = false;
        String inputTypeHint = InputComponents.getInputType(input);

        for (Class<?> inputType : getSupportedInputComponentTypes())
        {
            if (inputType.isAssignableFrom(input.getClass()))
            {
                handles = true;
                break;
            }
        }

        if (handles)
        {
            if (inputTypeHint != null)
            {
                handles = Proxies.areEquivalent(inputTypeHint,
                        getSupportedInputType());
            }
            else
            {
                // Fallback to standard type
                handles = getProducedType().isAssignableFrom(
                        input.getValueType());
            }
        }

        return handles;
    }

    public void setValueChangeListener(Runnable valueChangeListener)
    {
        this.valueChangeListener = valueChangeListener;
    }

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
}
