/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.ui.components.JBTextField;
import org.jboss.forge.addon.ui.hints.InputType;

import javax.swing.text.JTextComponent;

public class TextBoxComponentBuilder extends AbstractTextComponentBuilder
{
    @Override
    protected String getSupportedInputType()
    {
        return InputType.TEXTBOX;
    }

    @Override
    protected JTextComponent createTextComponent()
    {
        return new JBTextField();
    }
}
