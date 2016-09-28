/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.module;

import javax.swing.Icon;

import org.jboss.forge.plugin.idea.icon.ForgeIcon;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeProjectModuleType extends ModuleType<ForgeModuleBuilder>
{
   public static final String MODULE_TYPE_ID = "Forge_Module";

   public ForgeProjectModuleType()
   {
      super(MODULE_TYPE_ID);
   }

   public static ForgeProjectModuleType getInstance()
   {
      return (ForgeProjectModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
   }

   @Override
   public ForgeModuleBuilder createModuleBuilder()
   {
      return new ForgeModuleBuilder();
   }

   @Override
   public Icon getBigIcon()
   {
      return ForgeIcon.FORGE_LOGO;
   }

   @Override
   public String getDescription()
   {
      return "Create a Forge Project";
   }

   @Override
   public String getName()
   {
      return "Forge";
   }

   @Override
   public Icon getNodeIcon(@Deprecated boolean isOpened)
   {
      return ForgeIcon.FORGE_LOGO;
   }

}
