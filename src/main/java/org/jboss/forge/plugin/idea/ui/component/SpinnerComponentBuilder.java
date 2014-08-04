/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author Adam Wyłuda
 */
public class SpinnerComponentBuilder extends ComponentBuilder
{

    @SuppressWarnings("unchecked")
    @Override
    public ForgeComponent build(final InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new ForgeComponent()
        {
            private JSpinner spinner;

            @Override
            public void buildUI(Container container)
            {
                spinner = new JSpinner();
                container.add(spinner);

                JComponent editor = spinner.getEditor();
                if (editor instanceof JSpinner.DefaultEditor)
                {
                    ((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(JTextField.LEFT);
                }

                spinner.addChangeListener(new ChangeListener()
                {
                    @Override
                    public void stateChanged(ChangeEvent e)
                    {
                        Object selectedItem = spinner.getValue();
                        PluginService.getInstance().submitFormUpdate(
                                new FormUpdateCallback(converterFactory, input,
                                        selectedItem, valueChangeListener));
                    }
                });

                // Set Default Value
                Converter<Object, Integer> converter = converterFactory
                        .getConverter(input.getValueType(), Integer.class);
                Integer value = converter.convert(InputComponents.getValueFor(input));
                spinner.setValue(value == null ? 0 : value);
            }

            @Override
            public void updateState()
            {
                spinner.setEnabled(input.isEnabled());
            }
        });
    }

    @Override
    protected Class<Integer> getProducedType()
    {
        return Integer.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.DEFAULT;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }
}