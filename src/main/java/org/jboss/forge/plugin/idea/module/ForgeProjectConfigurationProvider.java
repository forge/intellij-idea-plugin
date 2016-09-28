/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.DefaultModuleConfigurationEditorFactory;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeProjectConfigurationProvider implements ModuleConfigurationEditorProvider
{

   @Override
   public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state)
   {
      final Module module = state.getRootModel().getModule();

      final ModuleType<?> moduleType = ModuleType.get(module);

      if (!(moduleType instanceof ForgeProjectModuleType))
      {
         return ModuleConfigurationEditor.EMPTY;
      }

      final DefaultModuleConfigurationEditorFactory editorFactory = DefaultModuleConfigurationEditorFactory
               .getInstance();
      return new ModuleConfigurationEditor[] {
               editorFactory.createModuleContentRootsEditor(state),
               editorFactory.createOutputEditor(state),
               editorFactory.createClasspathEditor(state)
      };
   }

}
