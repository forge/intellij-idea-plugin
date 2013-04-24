/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.loader;

import java.io.File;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.ForgeImpl;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.util.OperatingSystemUtils;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ApplicationComponent;

public class ForgeLoader implements ApplicationComponent
{

   private Forge forge;

   public ForgeLoader()
   {
   }

   @Override
   public void initComponent()
   {
      forge = new ForgeImpl();
      forge.addRepository(AddonRepositoryMode.MUTABLE, new File(OperatingSystemUtils.getUserForgeDir(), "addons"));
      forge.startAsync();
   }

   @Override
   public void disposeComponent()
   {
      forge.stop();
   }

   @Override
   @NotNull
   public String getComponentName()
   {
      return "org.jboss.forge.plugin.idea.loader.ForgeLoader";
   }
}
