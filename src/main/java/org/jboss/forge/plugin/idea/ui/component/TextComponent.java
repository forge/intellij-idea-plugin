/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.ui.JBColor;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Represents text components like TextArea or PasswordField.
 *
 * @author Adam Wyłuda
 */
public class TextComponent extends ForgeComponent
{
    private final InputComponent<?, Object> input;
    private final JTextComponent component;

    public TextComponent(InputComponent<?, Object> input, JTextComponent component)
    {
        this.input = input;
        this.component = component;
    }

    @Override
    public void buildUI(Container container)
    {
        // Set Default Value
        final ConverterFactory converterFactory = ServiceHelper.getForgeService().getConverterFactory();
        Converter<Object, String> converter = converterFactory.getConverter(
                input.getValueType(), String.class);
        String value = converter.convert(InputComponents.getValueFor(input));
        component.setText(value == null ? "" : value);

        component.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                InputComponents.setValueFor(converterFactory, input,
                        component.getText());
                valueChangeListener.run();
            }

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                InputComponents.setValueFor(converterFactory, input,
                        component.getText());
                valueChangeListener.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                InputComponents.setValueFor(converterFactory, input,
                        component.getText());
                valueChangeListener.run();
            }
        });

        container.add(component);
    }

    @Override
    public void setErrorMessage(UIMessage message)
    {
        component.setForeground(JBColor.RED);
        component.setToolTipText(message.getDescription());
    }

    @Override
    public void clearErrorMessage()
    {
        component.setForeground(null);
        component.setToolTipText("");
    }

    @Override
    public void updateState()
    {
        component.setEnabled(input.isEnabled());
    }
}
