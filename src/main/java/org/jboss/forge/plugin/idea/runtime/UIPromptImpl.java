/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.runtime;

import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * @author Adam Wyłuda
 */
public class UIPromptImpl implements UIPrompt
{
    @Override
    public String prompt(String message)
    {
        return null;
    }

    @Override
    public String promptSecret(String message)
    {
        return null;
    }

    @Override
    public boolean promptBoolean(String message)
    {
        return false;
    }

    @Override
    public boolean promptBoolean(String message, boolean defaultValue)
    {
        return false;
    }
}
