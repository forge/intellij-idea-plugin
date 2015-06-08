/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.ui.TextFieldWithAutoCompletion;

public class FileChooserComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(final UIContext context, InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new ChooserComponent(context, input)
        {
            @Override
            public ActionListener createBrowseButtonActionListener(final TextFieldWithAutoCompletion textField)
            {
                return new ActionListener()
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
                            textField.setText(new File(value).toString());
                        }
                    }
                };
            }

            @Override
            protected String getValue()
            {
                String value = super.getValue();
                return (value != null && !value.isEmpty()) ? new File(value).toString() : "";
            }

            @Override
            protected String getInputValue()
            {
                String inputValue = super.getInputValue();
                return (inputValue != null && !inputValue.isEmpty()) ? new File(inputValue).toString() : "";
            }
        });
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
