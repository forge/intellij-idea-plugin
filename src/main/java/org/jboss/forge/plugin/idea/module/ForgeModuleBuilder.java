/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeModuleBuilder extends ModuleBuilder
{
   @Override
   public ForgeProjectModuleType getModuleType()
   {
      return ForgeProjectModuleType.getInstance();
   }

   @Override
   public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException
   {
   }
}
