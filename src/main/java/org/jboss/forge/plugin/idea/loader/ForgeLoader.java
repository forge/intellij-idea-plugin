/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.loader;

import java.io.File;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.util.OperatingSystemUtils;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.proxy.ClassLoaderAdapterCallback;
import org.jboss.forge.se.init.BootstrapClassLoader;
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
public class ForgeLoader implements ApplicationComponent {
	@Override
	public void initComponent() {
		// MODULES-136
		System.setProperty("modules.ignore.jdk.factory", "true");
		final BootstrapClassLoader loader = new BootstrapClassLoader("bootpath");
		Forge forge;
		try {
			Class<?> bootstrapType = loader
					.loadClass("org.jboss.forge.container.ForgeImpl");
			forge = (Forge) ClassLoaderAdapterCallback.enhance(
					ForgeFactory.class.getClassLoader(), loader,
					bootstrapType.newInstance(), Forge.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		PluginId pluginId = PluginManager.getPluginByClassName(getClass()
				.getName());
		File pluginHome = new File(PathManager.getPluginsPath(),
				pluginId.getIdString());
		File addonRepo = new File(pluginHome, "addon-repository");
		forge.addRepository(AddonRepositoryMode.IMMUTABLE, addonRepo);
		forge.addRepository(AddonRepositoryMode.MUTABLE, new File(
				OperatingSystemUtils.getUserForgeDir(), "addons"));
		ForgeService.INSTANCE.setForge(forge);

		// Starting Forge
		ForgeService.INSTANCE.start(loader);
	}

	@Override
	public void disposeComponent() {
		ForgeService.INSTANCE.stop();
		ForgeService.INSTANCE.setForge(null);
	}

	@Override
	@NotNull
	public String getComponentName() {
		return "ForgeLoader";
	}
}
