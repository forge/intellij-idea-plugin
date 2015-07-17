/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Container;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;

/**
 * @author Adam Wyłuda
 */
public class SpinnerComponentBuilder extends ComponentBuilder
{

    @SuppressWarnings("unchecked")
    @Override
    public ForgeComponent build(UIContext context, final InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new ForgeComponent()
        {
            private JSpinner spinner;
            private Converter<Object, Integer> converter = converterFactory
                    .getConverter(input.getValueType(), Integer.class);

            @Override
            public void buildUI(Container container)
            {
                spinner = new JSpinner();
                container.add(spinner);

                JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0");
                editor.getTextField().setHorizontalAlignment(JTextField.LEFT);
                spinner.setEditor(editor);

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
                spinner.setToolTipText(input.getDescription());
                addNoteLabel(container, spinner).setText(input.getNote());
            }

            @Override
            public void updateState()
            {
                spinner.setEnabled(input.isEnabled());

                if (getValue() != getInputValue())
                {
                    reloadValue();
                }
                spinner.setToolTipText(input.getDescription());
                updateNote(spinner, input.getNote());
            }

            private void reloadValue()
            {
                spinner.setValue(getInputValue());
            }

            private int getInputValue()
            {
                Integer value = converter.convert(InputComponents.getValueFor(input));
                return value != null ? value : 0;
            }

            private int getValue()
            {
                return (int) spinner.getValue();
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