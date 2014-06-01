/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.context;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.AbstractUIContext;

public class UIContextImpl extends AbstractUIContext
{
    private final UISelectionImpl<?> currentSelection;
    private final UIProvider provider;

    public UIContextImpl(UISelectionImpl<?> selection, UIProvider provider)
    {
        this.currentSelection = selection;
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UISelectionImpl<?> getInitialSelection()
    {
        return currentSelection;
    }

    @Override
    public UIProvider getProvider()
    {
        return provider;
    }
}
