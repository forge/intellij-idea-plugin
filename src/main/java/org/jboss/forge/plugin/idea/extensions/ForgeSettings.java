/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.extensions;

import org.jetbrains.annotations.NotNull;

import com.intellij.util.xmlb.annotations.Tag;

public class ForgeSettings {
	private String addonHome;

	@NotNull
	@Tag("addon-home")
	public String getAddonHome() {
		return addonHome;
	}

	public void setAddonHome(String addonHome) {
		this.addonHome = addonHome;
	}

}
