/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;

public class TextBoxComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(UIContext context, InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new TextComponent(context, input, true));
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.TEXTBOX;
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
}
