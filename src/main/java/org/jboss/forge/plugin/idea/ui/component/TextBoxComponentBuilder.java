/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class TextBoxComponentBuilder extends ComponentBuilder
{
    private JBTextField textField;

    @Override
    public JComponent build(final InputComponent<?, Object> input,
                            Container container)
    {
        textField = new JBTextField();
        // Set Default Value
        final ConverterFactory converterFactory = ServiceHelper.getForgeService().getConverterFactory();
        Converter<Object, String> converter = converterFactory.getConverter(
                input.getValueType(), String.class);
        String value = converter.convert(InputComponents.getValueFor(input));
        textField.setText(value == null ? "" : value);

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
        String labelValue = input.getLabel() == null ? input.getName() : input
                .getLabel();
        JBLabel label = new JBLabel(labelValue);
        container.add(label);
        container.add(textField);
        return textField;
    }

    @Override
    protected Class<String> getProducedType()
    {
        return String.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.TEXTBOX;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }

    @Override
    public void setErrorMessage(UIMessage message)
    {
        textField.setForeground(JBColor.RED);
        textField.setToolTipText(message.getDescription());
    }

    @Override
    public void clearErrorMessage()
    {
        textField.setForeground(null);
        textField.setToolTipText("");
    }
}
