/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Creates a Checkbox
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CheckboxComponentBuilder extends ComponentBuilder
{

    @Override
    public JComponent build(final InputComponent<?, Object> input,
                            Container container)
    {
        // Create the label
        String text = (input.getLabel() == null ? input.getName() : input
                .getLabel());
        final JCheckBox checkbox = new JCheckBox(text);
        // Set Default Value
        final ConverterFactory converterFactory = ServiceHelper.getForgeService()
                .lookup(ConverterFactory.class);
        if (converterFactory != null)
        {
            Converter<Object, Boolean> converter = converterFactory
                    .getConverter(input.getValueType(), Boolean.class);
            Boolean value = converter.convert(InputComponents
                    .getValueFor(input));
            checkbox.setSelected(value == null ? false : value);
        }
        checkbox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                InputComponents.setValueFor(converterFactory, input,
                        checkbox.isSelected());
            }
        });
        container.add(checkbox, "span 2");
        return checkbox;
    }

    @Override
    protected Class<Boolean> getProducedType()
    {
        return Boolean.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.CHECKBOX;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }

}
