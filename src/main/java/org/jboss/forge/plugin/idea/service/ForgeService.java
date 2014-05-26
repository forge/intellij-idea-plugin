/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.PluginId;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.se.FurnaceFactory;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * This is a singleton for the {@link Furnace} class.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
public class ForgeService implements ApplicationComponent
{
    private transient Furnace furnace;

    ForgeService()
    {
    }

    @Override
    public void initComponent()
    {
        createFurnace();
        initializeAddonRepositories();
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

    public void start()
    {
        furnace.start();
    }

    public void startAsync()
    {
        furnace.startAsync();
    }

    public void stop()
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
        return (exportedInstance == null) ? null : exportedInstance.get();
    }

    public boolean isLoaded()
    {
        return furnace != null && furnace.getStatus().isStarted();
    }

    private void createFurnace()
    {
        // MODULES-136
        System.setProperty("modules.ignore.jdk.factory", "true");

        furnace = FurnaceFactory.getInstance();
    }

    private void initializeAddonRepositories()
    {
        PluginId pluginId = PluginManager.getPluginByClassName(getClass()
                .getName());
        File pluginHome = new File(PathManager.getPluginsPath(),
                pluginId.getIdString());
        File addonRepo = new File(pluginHome, "addon-repository");
        furnace.addRepository(AddonRepositoryMode.IMMUTABLE, addonRepo);
        furnace.addRepository(AddonRepositoryMode.MUTABLE, new File(
                OperatingSystemUtils.getUserForgeDir(), "addons"));
    }
}
