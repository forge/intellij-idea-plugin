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
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    private static class ValidationThread extends Thread
    {
        private BlockingQueue<FormUpdateCallback> queue = new LinkedBlockingQueue<>();

        public ValidationThread()
        {
            super("ForgeValidationThread");
        }

        public void setNextCallback(FormUpdateCallback nextCallback)
        {
            // Discard previous request for this component
            for (FormUpdateCallback callback : queue)
            {
                if (callback.getInput() == nextCallback.getInput())
                {
                    queue.remove(callback);
                    break;
                }
            }

            queue.add(nextCallback);
        }

        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    queue.take().run();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}
