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
import org.jboss.forge.plugin.idea.ForgeService;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ApplicationComponent;

/**
 * Loaded when the plugin initializes
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeLoader implements ApplicationComponent
{
   @Override
   public void initComponent()
   {
      Forge forge = new ForgeImpl();
      // TODO: Change this
      forge.addRepository(AddonRepositoryMode.MUTABLE, new File(OperatingSystemUtils.getUserForgeDir(), "addons"));
      ForgeService.INSTANCE.setForge(forge);

      // Starting Forge
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      ForgeService.INSTANCE.start(classLoader);
   }

   @Override
   public void disposeComponent()
   {
      ForgeService.INSTANCE.stop();
      ForgeService.INSTANCE.setForge(null);
   }

   @Override
   @NotNull
   public String getComponentName()
   {
      return "ForgeLoader";
   }
}
