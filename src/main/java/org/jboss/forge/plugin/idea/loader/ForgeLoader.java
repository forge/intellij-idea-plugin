/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.util.OperatingSystemUtils;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.se.init.ForgeFactory;
import org.jetbrains.annotations.NotNull;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.PluginId;

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
      final String logDir = System.getProperty("org.jboss.forge.log.file",
               new File(OperatingSystemUtils.getUserForgeDir(), "log/forge.log").getAbsolutePath());
      // Ensure this value is always set
      System.setProperty("org.jboss.forge.log.file", logDir);
      // Look for a logmanager before any logging takes place
      final String logManagerName = getServiceName(getClass().getClassLoader(), "java.util.logging.LogManager");
      if (logManagerName != null)
      {
         System.setProperty("java.util.logging.manager", logManagerName);
      }

      Forge forge = ForgeFactory.getInstance();
      PluginId pluginId = PluginManager.getPluginByClassName(getClass().getName());
      File pluginHome = new File(PathManager.getPluginsPath(), pluginId.getIdString());
      File addonRepo = new File(pluginHome, "addon-repository");
      forge.addRepository(AddonRepositoryMode.IMMUTABLE, addonRepo);
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

   @SuppressWarnings("resource")
   private static String getServiceName(final ClassLoader classLoader, final String className)
   {
      final InputStream stream = classLoader.getResourceAsStream("META-INF/services/" + className);
      if (stream != null)
      {
         try
         {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null)
            {
               final int i = line.indexOf('#');
               if (i != -1)
               {
                  line = line.substring(0, i);
               }
               line = line.trim();
               if (line.length() == 0)
                  continue;
               return line;
            }

         }
         catch (IOException ignored)
         {
            // ignore
         }
         finally
         {
            try
            {
               stream.close();
            }
            catch (IOException ignored)
            {
               // ignore
            }
         }
      }
      return null;
   }

}
