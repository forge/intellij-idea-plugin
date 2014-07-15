/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Adam Wyłuda
 */
public class JavaClassChooserComponentBuilder extends AbstractChooserComponentBuilder
{
    @Override
    @SuppressWarnings("unchecked")
    protected TextFieldWithBrowseButton createTextField()
    {
        final TextFieldWithBrowseButton[] holder = new TextFieldWithBrowseButton[1];
        TextFieldWithBrowseButton textField = new TextFieldWithBrowseButton(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String initialValue = holder[0].getText();
                String value = IDEUtil.chooseClass(context, initialValue);
                if (value != null)
                {
                    holder[0].setText(value);
                }
            }
        });
        holder[0] = textField;
        return textField;
    }

    @Override
    protected Class<?> getProducedType()
    {
        return String.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.JAVA_CLASS_PICKER;
    }
}
