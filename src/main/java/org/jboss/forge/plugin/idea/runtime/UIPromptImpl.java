/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.runtime;

import com.intellij.openapi.ui.Messages;
import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * @author Adam Wyłuda
 */
public class UIPromptImpl implements UIPrompt
{
    @Override
    public String prompt(String message)
    {
        return Messages.showInputDialog("", message, Messages.getQuestionIcon());
    }

    @Override
    public String promptSecret(String message)
    {
        // TODO Implement promptSecret()
        return prompt(message);
    }

    @Override
    public boolean promptBoolean(String message)
    {
        return Messages.showYesNoDialog(message, "", Messages.getQuestionIcon()) == Messages.YES;
    }

    @Override
    public boolean promptBoolean(String message, boolean defaultValue)
    {
        int result = Messages.showYesNoCancelDialog(message, "", Messages.getQuestionIcon());
        return result == Messages.CANCEL ? defaultValue : result == Messages.YES;
    }
}
