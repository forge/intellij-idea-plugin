/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jboss.forge.plugin.idea.service.threads.CommandLoadingThread;
import org.jboss.forge.plugin.idea.service.threads.ValidationThread;
import org.jboss.forge.plugin.idea.util.CommandUtil;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;

/**
 * Maintains plugin state.
 *
 * @author Adam Wyłuda
 */
public class PluginService implements ApplicationComponent
{
    private static final int RECENT_COMMANDS_LIMIT = 3;

    private List<String> recentCommands = new ArrayList<>();
    private ValidationThread validationThread = new ValidationThread();
    private CommandLoadingThread commandLoadingThread = new CommandLoadingThread();

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
        validationThread.start();
        commandLoadingThread.start();
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
        UICommandMetadata metadata = command.getMetadata(context);

        // To make sure it will be the last element on the list
        recentCommands.remove(metadata.getName());
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

    public synchronized void submitFormUpdate(FormUpdateCallback callback)
    {
        validationThread.setNextCallback(callback);
    }

    public synchronized List<UICommand> getEnabledCommands(UIContext uiContext)
    {
        List<UICommand> result;

        if (isCacheCommands())
        {
            result = commandLoadingThread.getCommands(uiContext);
            commandLoadingThread.reload(uiContext);
        }
        else
        {
            result = CommandUtil.getEnabledCommands(uiContext);
        }

        return result;
    }

    /**
     * Makes request to reload command cache.
     */
    public synchronized void reloadCommands(UIContext uiContext)
    {
        if (isCacheCommands())
        {
            commandLoadingThread.reload(uiContext);
        }
    }

    /**
     * Makes sure that next call to {@link #getEnabledCommands(org.jboss.forge.addon.ui.context.UIContext)}
     * will return fresh instances of UICommand.
     */
    public synchronized void invalidateAndReloadCommands(UIContext uiContext)
    {
        if (isCacheCommands())
        {
            commandLoadingThread.invalidate();
            commandLoadingThread.reload(uiContext);
        }
    }

    private boolean isCacheCommands()
    {
        return ForgeService.getInstance().getState().isCacheCommands();
    }
}
