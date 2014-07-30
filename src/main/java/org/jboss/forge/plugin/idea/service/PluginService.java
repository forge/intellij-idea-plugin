/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains plugin state.
 *
 * @author Adam Wyłuda
 */
public class PluginService implements ApplicationComponent
{
    private static final int RECENT_COMMANDS_LIMIT = 3;

    private List<String> recentCommands = new ArrayList<>();

    PluginService()
    {
    }

    public static PluginService getInstance()
    {
        return ServiceManager.getService(PluginService.class);
    }

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

    public synchronized void addRecentCommand(UICommand command, UIContext context)
    {
        // Remove older version of the command
        UICommandMetadata metadata = command.getMetadata(context);
        for (String commandFromList : recentCommands)
        {
            if (metadata.getName().equals(commandFromList))
            {
                recentCommands.remove(commandFromList);
                break;
            }
        }

        recentCommands.add(metadata.getName());

        if (recentCommands.size() > RECENT_COMMANDS_LIMIT)
        {
            recentCommands.remove(0);
        }
    }

    public synchronized List<UICommand> getRecentCommands(List<UICommand> commands, UIContext context)
    {
        List<UICommand> enabledList = new ArrayList<>();

        for (UICommand command : commands)
        {
            UICommandMetadata metadata = command.getMetadata(context);

            // It's necessary to check if the command is still apparent on the list
            if (recentCommands.contains(metadata.getName()) && Commands.isEnabled(command, context))
            {
                enabledList.add(command);
            }
        }

        return enabledList;
    }
}
