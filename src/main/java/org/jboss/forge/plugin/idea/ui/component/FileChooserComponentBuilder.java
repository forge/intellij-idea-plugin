/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.plugin.idea.util.CompletionUtil;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileChooserComponentBuilder extends AbstractChooserComponentBuilder
{
    @Override
    @SuppressWarnings("unchecked")
    protected ComponentWithBrowseButton<TextFieldWithAutoCompletion> createTextField(InputComponent<?, Object> input)
    {
        boolean hasCompletions = CompletionUtil.hasCompletions(input);

        final TextFieldWithAutoCompletion textField = CompletionUtil.createTextFieldWithAutoCompletion(context, hasCompletions);
        ComponentWithBrowseButton<TextFieldWithAutoCompletion> component =
                new ComponentWithBrowseButton<>(textField, null);

        component.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String initialValue = textField.getText();
                String value = IDEUtil.chooseFile(
                        context,
                        FileChooserDescriptorFactory.createSingleLocalFileDescriptor(),
                        initialValue);
                if (value != null)
                {
                    textField.setText(value);
                }
            }
        });
        return component;
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
}
