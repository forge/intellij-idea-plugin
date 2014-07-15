/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * @author Adam Wyłuda
 */
public abstract class AbstractChooserComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(final InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new ForgeComponent()
        {
            private TextFieldWithBrowseButton fileField;

            @Override
            public void buildUI(Container container)
            {
                fileField = createTextField();

                // Set Default Value
                final ConverterFactory converterFactory = ServiceHelper.getForgeService()
                        .getConverterFactory();
                Converter<Object, String> converter = converterFactory.getConverter(
                        input.getValueType(), String.class);
                String value = converter.convert(InputComponents.getValueFor(input));
                fileField.setText(value == null ? "" : value);

                final JTextField textField = fileField.getTextField();
                textField.getDocument().addDocumentListener(new DocumentListener()
                {
                    @Override
                    public void removeUpdate(DocumentEvent e)
                    {
                        InputComponents.setValueFor(converterFactory, input,
                                textField.getText());
                        valueChangeListener.run();
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e)
                    {
                        InputComponents.setValueFor(converterFactory, input,
                                textField.getText());
                        valueChangeListener.run();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e)
                    {
                        InputComponents.setValueFor(converterFactory, input,
                                textField.getText());
                        valueChangeListener.run();
                    }
                });

                container.add(fileField);
            }

            @Override
            public void updateState()
            {
                fileField.setEnabled(input.isEnabled());
            }
        });
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }

    protected abstract TextFieldWithBrowseButton createTextField();
}
