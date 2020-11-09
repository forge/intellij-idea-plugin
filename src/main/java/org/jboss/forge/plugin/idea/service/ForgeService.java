/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import java.io.File;
import java.util.concurrent.Future;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.extensions.PluginId;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.se.FurnaceFactory;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.AddonCompatibilityStrategies;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jetbrains.annotations.Nullable;

/**
 * This is a singleton for the {@link Furnace} class.
 * <p/>
 * Use {@link org.jboss.forge.plugin.idea.service.ServiceHelper#loadFurnaceAndRun(Runnable)} to start any interaction
 * with Forge.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
@State(name = "ForgeConfiguration", storages = {
         @Storage("forge.xml")
})
public class ForgeService implements PersistentStateComponent<ForgeService.State>, Disposable
{
   private transient Furnace furnace;
   private static final PluginId PLUGIN_ID = PluginId.getId("org.jboss.forge.plugin.idea");
   private State state = new State();

   ForgeService()
   {
   }

   public static ForgeService getInstance()
   {
      return ApplicationManager.getApplication().getComponent(ForgeService.class);
   }

   @Override
   public void initializeComponent()
   {
      createFurnace();
      initializeAddonRepositories(true);
      // Using a lenient addon compatibility strategy
      furnace.setAddonCompatibilityStrategy(AddonCompatibilityStrategies.LENIENT);
   }

   @Override
   public void dispose()
   {
      stop();
   }

   Future<Furnace> startAsync()
   {
      return furnace.startAsync();
   }

   void stop()
   {
      furnace.stop();
   }

   public CommandFactory getCommandFactory()
   {
      return lookup(CommandFactory.class);
   }

   public CommandControllerFactory getCommandControllerFactory()
   {
      return lookup(CommandControllerFactory.class);
   }

   public ConverterFactory getConverterFactory()
   {
      return lookup(ConverterFactory.class);
   }

   public <S> S lookup(Class<S> service)
   {
      Imported<S> exportedInstance = null;
      if (furnace != null)
      {
         exportedInstance = furnace.getAddonRegistry().getServices(service);
      }
      return (exportedInstance == null || exportedInstance.isUnsatisfied()) ? null : exportedInstance.get();
   }

   public <S> Imported<S> lookupImported(Class<S> service)
   {
      if (furnace == null)
      {
         createFurnace();
      }
      Imported<S> importedService = null;
      if (furnace != null)
      {
         importedService = furnace.getAddonRegistry().getServices(service);
      }
      return importedService;
   }

   @SuppressWarnings("unchecked")
   public <T> Class<T> locateNativeClass(Class<T> type)
   {
      Class<T> result = type;
      AddonRegistry registry = furnace.getAddonRegistry();
      for (Addon addon : registry.getAddons())
      {
         try
         {
            ClassLoader classLoader = addon.getClassLoader();
            result = (Class<T>) classLoader.loadClass(type.getName());
            break;
         }
         catch (ClassNotFoundException e)
         {
         }
      }
      return result;
   }

   public boolean isLoaded()
   {
      return furnace != null && furnace.getStatus().isStarted();
   }

   void createFurnace()
   {
      // MODULES-136
      System.setProperty("modules.ignore.jdk.factory", "true");

      furnace = FurnaceFactory.getInstance(ForgeService.class.getClassLoader());
   }

   void initializeAddonRepositories(boolean addBundledAddons)
   {
      furnace.addRepository(AddonRepositoryMode.MUTABLE, new File(state.addonDir));

      if (addBundledAddons)
      {
         File pluginHome = new File(PathManager.getPluginsPath(),
                  PLUGIN_ID.getIdString());
         File addonRepo = new File(pluginHome, "addon-repository");
         furnace.addRepository(AddonRepositoryMode.IMMUTABLE, addonRepo);
      }
   }

   /**
    * Ugly hack. Versions.getImplementationVersionFor does not work here
    */
   public static String getForgeVersion()
   {
      IdeaPluginDescriptor plugin = PluginManager.getPlugin(PLUGIN_ID);
      String description = plugin.getChangeNotes();
      String version = "(unknown)";
      String str = "Bundled with Forge";
      int bundledIdx = description.indexOf(str);
      if (bundledIdx > -1)
      {
         int endIndex = description.indexOf(OperatingSystemUtils.isWindows() ? "\n" : System.lineSeparator(), bundledIdx);
         if (endIndex == -1)
         {
            endIndex = description.length();
         }
         version = description.substring(bundledIdx + str.length(), endIndex).trim();
      }
      return version;
   }

   @Nullable
   @Override
   public State getState()
   {
      return this.state;
   }

   @Override
   public void loadState(State state)
   {
      this.state = state;
   }

   public static class State
   {
      private String addonDir = new File(OperatingSystemUtils.getUserForgeDir(), "addons").getAbsolutePath();
      private boolean cacheCommands = true;

      public String getAddonDir()
      {
         return addonDir;
      }

      public void setAddonDir(String addonDir)
      {
         this.addonDir = addonDir;
      }

      public boolean isCacheCommands()
      {
         return cacheCommands;
      }

      public void setCacheCommands(boolean cacheCommands)
      {
         this.cacheCommands = cacheCommands;
      }
   }
}
