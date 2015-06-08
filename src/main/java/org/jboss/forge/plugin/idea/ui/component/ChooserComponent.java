/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.util.List;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jboss.forge.plugin.idea.util.CompletionUtil;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.ui.TextFieldWithAutoCompletion;

/**
 * Component with browse button.
 *
 * @author Adam Wyłuda
 */
public abstract class ChooserComponent extends ForgeComponent
{
    private final UIContext context;
    private final InputComponent<?, Object> input;
    private final Converter<Object, String> converter;

    private ComponentWithBrowseButton<TextFieldWithAutoCompletion> component;
    private TextFieldWithAutoCompletion inputField;

    public ChooserComponent(UIContext context,
                            InputComponent<?, Object> input)
    {
        this.context = context;
        this.input = input;

        converter = converterFactory.getConverter(input.getValueType(), String.class);
    }

    public abstract ActionListener createBrowseButtonActionListener(TextFieldWithAutoCompletion textField);

    @Override
    public void buildUI(Container container)
    {
        TextFieldWithAutoCompletion textField = CompletionUtil.createTextFieldWithAutoCompletion(context, input);
        component = new ComponentWithBrowseButton<>(textField, createBrowseButtonActionListener(textField));

        inputField = component.getChildComponent();
        inputField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void beforeDocumentChange(DocumentEvent event)
            {
            }

            @Override
            public void documentChanged(DocumentEvent event)
            {
                PluginService.getInstance().submitFormUpdate(
                        new FormUpdateCallback(converterFactory, input, getValue(), valueChangeListener));
            }
        });

        container.add(component);
        component.setToolTipText(input.getDescription());
        addNoteLabel(container, component).setText(input.getNote());
    }

    @Override
    public void updateState()
    {
        component.setEnabled(input.isEnabled());

        if (!getValue().equals(getInputValue()))
        {
            reloadValue();
        }

        if (CompletionUtil.hasCompletions(input))
        {
            inputField.setVariants(getCompletions());
        }
        updateNote(component, input.getNote());
    }

    private void reloadValue()
    {
        String value = getInputValue();
        inputField.setText(value == null ? "" : value);
    }

    protected String getValue()
    {
        return inputField.getText();
    }

    protected String getInputValue()
    {
        return converter.convert(InputComponents.getValueFor(input));
    }

    private List<String> getCompletions()
    {
        return CompletionUtil.getCompletions(converterFactory, context, input,
                component != null ? inputField.getText() : null);
    }
}
