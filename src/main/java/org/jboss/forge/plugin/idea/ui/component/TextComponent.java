/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.JBColor;
import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.ForgeService;
import org.jboss.forge.plugin.idea.util.CompletionUtil;

import java.awt.*;
import java.util.List;

/**
 * Represents text components like TextArea or PasswordField.
 *
 * @author Adam Wyłuda
 */
public class TextComponent extends ForgeComponent
{
    private final UIContext context;
    private final InputComponent<?, Object> input;
    private final boolean oneLineMode;

    private final ConverterFactory converterFactory;

    private TextFieldWithAutoCompletion component;

    public TextComponent(UIContext context, InputComponent<?, Object> input, boolean oneLineMode)
    {
        this.context = context;
        this.input = input;
        this.oneLineMode = oneLineMode;

        this.converterFactory = ForgeService.getInstance().getConverterFactory();
    }

    @Override
    public void buildUI(Container container)
    {
        boolean hasCompletions = CompletionUtil.hasCompletions(input);

        component = CompletionUtil.createTextFieldWithAutoCompletion(context, hasCompletions);

        if (hasCompletions)
        {
            component.setVariants(getCompletions());
        }

        component.setOneLineMode(oneLineMode);

        // Set Default Value
        Converter<Object, String> converter = converterFactory.getConverter(
                input.getValueType(), String.class);
        String value = converter.convert(InputComponents.getValueFor(input));
        component.setText(value == null ? "" : value);

        component.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void beforeDocumentChange(DocumentEvent event)
            {
            }

            @Override
            public void documentChanged(DocumentEvent event)
            {
                InputComponents.setValueFor(converterFactory, input,
                        component.getText());
                if (CompletionUtil.hasCompletions(input))
                {
                    component.setVariants(getCompletions());
                }

                valueChangeListener.run();
            }
        });

        container.add(component);
    }

    @Override
    public void setErrorMessage(UIMessage message)
    {
        // TODO Set foreground color for TextFieldWithAutoCompletion
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

    private List<String> getCompletions()
    {
        return CompletionUtil.getCompletions(converterFactory, context, input,
                component != null ? component.getText() : null);
    }
}
