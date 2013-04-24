/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.loader;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class ForgeLoader implements ApplicationComponent
{
   public ForgeLoader()
   {
   }

   @Override
   public void initComponent()
   {
      // TODO: insert component initialization logic here
   }

   @Override
   public void disposeComponent()
   {
      // TODO: insert component disposal logic here
   }

   @Override
   @NotNull
   public String getComponentName()
   {
      return "org.jboss.forge.plugin.idea.loader.ForgeLoader";
   }
}
