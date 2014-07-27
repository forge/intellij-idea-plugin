/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.wizard;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.plugin.idea.runtime.UIProgressMonitorImpl;
import org.jboss.forge.plugin.idea.util.ForgeNotifications;
import org.jboss.forge.plugin.idea.util.IDEUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Adam Wyłuda
 */
public class ForgeCommandTask extends Task.Backgroundable
{
    private final CommandController controller;
    private final UIProgressMonitor monitor;

    public ForgeCommandTask(CommandController controller)
    {
        super(IDEUtil.projectFromContext(controller.getContext()), "Executing Forge command", true);

        this.controller = controller;

        this.monitor = IDEUtil.progressMonitorFromContext(controller.getContext());
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator)
    {
        ((UIProgressMonitorImpl) this.monitor).setIndicator(indicator);
        indicator.setFraction(0.0);

        try
        {
            final UIContext context = controller.getContext();

            Result result = controller.execute();
            ForgeNotifications.showExecutionResult(result);

            ApplicationManager.getApplication().invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    IDEUtil.refreshProject(context);
                    IDEUtil.openSelection(context);
                }
            });
        }
        catch (Exception e)
        {
            ForgeNotifications.showErrorMessage(e);
            e.printStackTrace();
        }
        finally
        {
            try
            {
                controller.close();
            }
            catch (Exception e)
            {
                ForgeNotifications.showErrorMessage(e);
                e.printStackTrace();
            }
        }

        monitor.done();
    }
}
