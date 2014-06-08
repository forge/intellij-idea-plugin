/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileChooserComponentBuilder extends ComponentBuilder
{

    @Override
    public ForgeComponent build(final InputComponent<?, Object> input)
    {
        return new ForgeComponent()
        {
            @Override
            public void buildUI(Container container)
            {
                // Added Label
                JBLabel label = new JBLabel(input.getLabel() == null ? input.getName()
                        : input.getLabel());
                container.add(label);

                final TextFieldWithBrowseButton fileField = new TextFieldWithBrowseButton(
                        new ActionListener()
                        {

                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                // TODO Auto-generated method stub

                            }
                        }
                );
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

                fileField.addBrowseFolderListener("Select a directory", null, null,
                        FileChooserDescriptorFactory.createSingleFolderDescriptor());
                container.add(fileField);
            }
        };
    }

    @Override
    protected Class<File> getProducedType()
    {
        return File.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.FILE_PICKER;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }

}
