/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import com.intellij.openapi.components.ApplicationComponent;
import org.jboss.forge.plugin.idea.ui.CommandListPopup;
import org.jetbrains.annotations.NotNull;

/**
 * @author Adam Wyłuda
 */
public class UIService implements ApplicationComponent
{
    @Override
    public void initComponent()
    {

    }

    @Override
    public void disposeComponent()
    {

    }

    @NotNull
    @Override
    public String getComponentName()
    {
        return getClass().getSimpleName();
    }

    public CommandListPopup getCommandListPopupMenu()
    {
        // TODO Implement getCommandListPopupMenu()
        return new CommandListPopup();
    }
}
