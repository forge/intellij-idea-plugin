/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.runtime;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.Messages;
import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * @author Adam Wyłuda
 */
public class UIPromptImpl implements UIPrompt
{
    private volatile String stringValue;
    private volatile boolean booleanValue;

    @Override
    public String prompt(final String message)
    {
        ApplicationManager.getApplication().invokeAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                stringValue = Messages.showInputDialog("", message, Messages.getQuestionIcon());
            }
        }, ModalityState.any());
        return stringValue;
    }

    @Override
    public String promptSecret(String message)
    {
        // TODO Implement promptSecret()
        return prompt(message);
    }

    @Override
    public boolean promptBoolean(final String message)
    {
        ApplicationManager.getApplication().invokeAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                booleanValue = Messages.showYesNoDialog(message, "", Messages.getQuestionIcon()) == Messages.YES;
            }
        }, ModalityState.any());
        return booleanValue;
    }

    @Override
    public boolean promptBoolean(final String message, final boolean defaultValue)
    {
        ApplicationManager.getApplication().invokeAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                int result = Messages.showYesNoCancelDialog(message, "", Messages.getQuestionIcon());
                booleanValue = result == Messages.CANCEL ? defaultValue : result == Messages.YES;
            }
        }, ModalityState.any());
        return booleanValue;
    }
}
