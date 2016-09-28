/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.icon;

import javax.swing.Icon;

import com.intellij.openapi.util.IconLoader;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface ForgeIcon
{
   Icon FORGE_LOGO = IconLoader.findIcon("/icons/forge.png");
}
