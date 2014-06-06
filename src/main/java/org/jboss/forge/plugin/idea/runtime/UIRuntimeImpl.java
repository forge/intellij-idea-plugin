/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.runtime;

import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

/**
 * @author Adam Wyłuda
 */
public class UIRuntimeImpl implements UIRuntime
{
    // TODO Implement UIRuntime

    @Override
    public UIProgressMonitor createProgressMonitor(UIContext context)
    {
        return null;
    }

    @Override
    public UIPrompt createPrompt(UIContext context)
    {
        return null;
    }
}
