/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import java.util.concurrent.Future;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;

/**
 * IntelliJ service utilities.
 *
 * @author Adam Wyłuda
 */
public class ServiceHelper
{
    private ServiceHelper()
    {
    }

    /**
     * Makes sure that Furnace is loaded and then executes given callback on IntelliJ UI thread.
     */
    public static void loadFurnaceAndRun(Runnable callback)
    {
        if (ForgeService.getInstance().isLoaded())
        {
            runOnUIThread(callback);
        }
        else
        {
            Future<Furnace> future = ForgeService.getInstance().startAsync();
            startForgeInitTask(future, callback);
        }
    }

    /**
     * Starts Forge initiation background task.
     */
    private static void startForgeInitTask(final Future<Furnace> future, final Runnable callback)
    {
        new Task.Backgroundable(null, "Starting Forge", true)
        {
            public void run(ProgressIndicator indicator)
            {
                indicator.setText("Loading JBoss Forge "+ ForgeService.getForgeVersion());
                indicator.setFraction(0.0);
                try
                {
                    future.get();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                indicator.setFraction(1.0);

                registerProjectListener();

                runOnUIThread(callback);
            }
        }.setCancelText("Stop loading").queue();
    }

    /**
     * Runs provided Runnable on Swing dispatch thread.
     */
    private static void runOnUIThread(Runnable runnable)
    {
        if (ApplicationManager.getApplication().isDispatchThread())
        {
            runnable.run();
        }
        else
        {
            ApplicationManager.getApplication().invokeLater(runnable);
        }
    }

    private static void registerProjectListener()
    {
        ProjectFactory factory = ForgeService.getInstance().lookup(ProjectFactory.class);

        if (factory != null)
        {
            factory.addProjectListener(new ProjectListener()
            {
                @Override
                public void projectCreated(final Project project)
                {
                    ApplicationManager.getApplication().invokeLater((Runnable) () -> IDEUtil.openProject(project.getRoot().getFullyQualifiedName()));
                }
            });
        }
    }
}
