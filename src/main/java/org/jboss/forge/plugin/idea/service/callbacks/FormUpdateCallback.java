/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service.callbacks;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.ui.listeners.ValueChangeListener;

/**
 * Updates specified form input and notifies {@link org.jboss.forge.plugin.idea.ui.listeners.ValueChangeListener}.
 *
 * @author Adam Wyłuda
 */
public class FormUpdateCallback implements Runnable
{
    private final ConverterFactory converterFactory;
    private final InputComponent<?, ?> input;
    private final Object value;
    private final ValueChangeListener valueChangeListener;

    public FormUpdateCallback(ConverterFactory converterFactory, InputComponent<?, ?> input, Object value, ValueChangeListener valueChangeListener)
    {
        this.converterFactory = converterFactory;
        this.input = input;
        this.value = value;
        this.valueChangeListener = valueChangeListener;
    }

    public InputComponent<?, ?> getInput()
    {
        return input;
    }

    @Override
    public void run()
    {
        InputComponents.setValueFor(converterFactory, input, value);

        if (valueChangeListener != null)
        {
            valueChangeListener.run();
        }
    }
}
