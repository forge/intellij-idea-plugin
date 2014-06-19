/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;

import javax.swing.text.JTextComponent;

/**
 * @author Adam Wyłuda
 */
public abstract class AbstractTextComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new TextComponent(input, createTextComponent()));
    }

    @Override
    protected Class<String> getProducedType()
    {
        return String.class;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }

    protected abstract JTextComponent createTextComponent();
}
