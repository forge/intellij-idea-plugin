/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.runtime;

import org.jboss.forge.addon.ui.DefaultUIDesktop;
import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * @author Adam Wyłuda
 */
public class UIProviderImpl implements UIProvider
{
    private final UIOutput output;

    public UIProviderImpl()
    {
        this.output = new UIOutputImpl();
    }

    @Override
    public boolean isGUI()
    {
        return true;
    }

    @Override
    public UIOutput getOutput()
    {
        return output;
    }
    
   @Override
   public UIDesktop getDesktop()
   {
      return new DefaultUIDesktop();
   }

    @Override
    public boolean isEmbedded()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Intellij IDEA";
    }
}
