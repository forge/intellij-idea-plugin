/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.ui.JBColor;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PasswordComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(final InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new ForgeComponent()
        {
            private JPasswordField component;

            @Override
            public void buildUI(Container container)
            {
                component = new JPasswordField();

                Converter<Object, String> converter = converterFactory.getConverter(
                        input.getValueType(), String.class);
                String value = converter.convert(InputComponents.getValueFor(input));
                component.setText(value == null ? "" : value);

                component.getDocument().addDocumentListener(new DocumentListener()
                {
                    @Override
                    public void removeUpdate(DocumentEvent e)
                    {
                        PluginService.getInstance().submitFormUpdate(
                                new FormUpdateCallback(converterFactory, input,
                                        component.getText(), valueChangeListener));
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e)
                    {
                        PluginService.getInstance().submitFormUpdate(
                                new FormUpdateCallback(converterFactory, input,
                                        component.getText(), valueChangeListener));
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e)
                    {
                        PluginService.getInstance().submitFormUpdate(
                                new FormUpdateCallback(converterFactory, input,
                                        component.getText(), valueChangeListener));
                    }
                });

                container.add(component);
            }

            @Override
            public void updateState()
            {
                component.setEnabled(input.isEnabled());
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
        });
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.SECRET;
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
