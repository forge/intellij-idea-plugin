/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import java.io.File;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.extensions.PluginId;

/**
 * This is a singleton for the {@link Furnace} class.
 * <p/>
 * Use {@link org.jboss.forge.plugin.idea.service.ServiceHelper#loadFurnaceAndRun(Runnable)} to start any interaction
 * with Forge.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
@State(
         name = "ForgeConfiguration",
         storages = {
                  @Storage(id = "other", file = StoragePathMacros.APP_CONFIG + "/other.xml")
         })
public class ForgeService implements ApplicationComponent, PersistentStateComponent<ForgeService.State>
{
   private transient Furnace furnace;
   private static final PluginId PLUGIN_ID = PluginId.getId("org.jboss.forge.plugin.idea");
   private State state = new State();

   ForgeService()
   {
   }

   public static ForgeService getInstance()
   {
      return ServiceManager.getService(ForgeService.class);
   }

   @Override
   public void initComponent()
   {
      createFurnace();
      initializeAddonRepositories(true);
      // Using a lenient addon compatibility strategy
      furnace.setAddonCompatibilityStrategy(AddonCompatibilityStrategies.LENIENT);
   }

   @Override
   public void disposeComponent()
   {
      stop();
   }

   @NotNull
   @Override
   public String getComponentName()
   {
      return getClass().getSimpleName();
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
      String description = plugin.getDescription();
      String version = "(unknown)";
      String str = "Bundled with Forge";
      int bundledIdx = description.indexOf(str);
      if (bundledIdx > -1)
      {
         version = description.substring(bundledIdx + str.length(),
                  description.indexOf(System.lineSeparator(), bundledIdx)).trim();
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
