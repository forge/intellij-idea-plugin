/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.runtime;

import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

import com.intellij.openapi.progress.ProgressIndicator;

/**
 * @author Adam Wyłuda
 */
public class UIProgressMonitorImpl implements UIProgressMonitor
{
    private ProgressIndicator indicator;
    private int totalWork = Integer.MAX_VALUE;

    public UIProgressMonitorImpl()
    {
    }

    public void setIndicator(ProgressIndicator indicator)
    {
        this.indicator = indicator;
    }

    @Override
    public void beginTask(String name, int totalWork)
    {
        if (totalWork <= 0)
        {
            throw new IllegalArgumentException("Total work must be greater than 0");
        }

        indicator.setText(name);
        this.totalWork = totalWork;
    }

    @Override
    public void done()
    {
        indicator.setFraction(1.0);
    }

    @Override
    public boolean isCancelled()
    {
        return indicator.isCanceled();
    }

    @Override
    public void setCancelled(boolean value)
    {
    }

    @Override
    public void setTaskName(String name)
    {
        indicator.setText(name);
    }

    @Override
    public void subTask(String name)
    {
        indicator.setText2(name);
    }

    @Override
    public void worked(int work)
    {
        indicator.setFraction(((double) work) / ((double) totalWork));
    }
}
