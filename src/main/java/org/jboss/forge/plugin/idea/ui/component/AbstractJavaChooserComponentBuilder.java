/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import java.awt.*;

/**
 * @author Adam Wyłuda
 */
public abstract class AbstractJavaChooserComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(final InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new ForgeComponent()
        {
            @Override
            public void buildUI(Container container)
            {
                final EditorTextFieldWithBrowseButton fileField = createEditorField();

                // Set Default Value
                final ConverterFactory converterFactory = ServiceHelper.getForgeService()
                        .getConverterFactory();
                Converter<Object, String> converter = converterFactory.getConverter(
                        input.getValueType(), String.class);
                String value = converter.convert(InputComponents.getValueFor(input));
                fileField.setText(value == null ? "" : value);
                fileField.setButtonEnabled(true);

                fileField.getChildComponent().addDocumentListener(new DocumentListener()
                {
                    @Override
                    public void beforeDocumentChange(DocumentEvent event)
                    {
                    }

                    @Override
                    public void documentChanged(DocumentEvent event)
                    {
                        InputComponents.setValueFor(converterFactory, input,
                                fileField.getText());
                        valueChangeListener.run();
                    }
                });

                container.add(fileField);
            }
        });
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }

    protected abstract EditorTextFieldWithBrowseButton createEditorField();
}
