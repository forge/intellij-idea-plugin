/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.context;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UISelection;

public class UIContextImpl extends AbstractUIContext
{
    private final UISelection<?> initialSelection;
    private final UIProvider provider;

    UIContextImpl(UISelection<?> initialSelection, UIProvider provider)
    {
        this.initialSelection = initialSelection;
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UISelection<?> getInitialSelection()
    {
        return initialSelection;
    }

    @Override
    public UIProvider getProvider()
    {
        return provider;
    }
}
