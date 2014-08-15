/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jboss.forge.plugin.idea.util.CompletionUtil;

import java.awt.*;
import java.util.List;

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
            private ComponentWithBrowseButton<TextFieldWithAutoCompletion> component;
            private TextFieldWithAutoCompletion inputField;
            private Converter<Object, String> converter = converterFactory.getConverter(
                    input.getValueType(), String.class);

            @Override
            public void buildUI(Container container)
            {
                component = createTextField(input);
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
                                new FormUpdateCallback(converterFactory, input,
                                        inputField.getText(), valueChangeListener));
                    }
                });

                container.add(component);
            }

            @Override
            public void updateState()
            {
                component.setEnabled(input.isEnabled());

                if (!inputField.getText().equals(getInputValue()))
                {
                    reloadValue();
                }

                if (CompletionUtil.hasCompletions(input))
                {
                    inputField.setVariants(getCompletions());
                }
            }

            private void reloadValue()
            {
                String value = getInputValue();
                inputField.setText(value == null ? "" : value);
            }

            private String getInputValue()
            {
                return converter.convert(InputComponents.getValueFor(input));
            }

            private List<String> getCompletions()
            {
                return CompletionUtil.getCompletions(converterFactory, context, input,
                        component != null ? inputField.getText() : null);
            }
        });
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }

    protected abstract ComponentWithBrowseButton<TextFieldWithAutoCompletion> createTextField(
            InputComponent<?, Object> input);
}
