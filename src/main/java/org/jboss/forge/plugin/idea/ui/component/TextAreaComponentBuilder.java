/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import org.jboss.forge.addon.ui.hints.InputType;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * @author Adam Wyłuda
 */
public class TextAreaComponentBuilder extends AbstractTextComponentBuilder
{
    @Override
    protected JTextComponent createTextComponent()
    {
        return new JTextArea();
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.TEXTAREA;
    }
}
