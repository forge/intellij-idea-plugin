/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component.many;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;
import org.jboss.forge.plugin.idea.util.IDEUtil;

/**
 * @author Adam Wyłuda
 */
@SuppressWarnings("unchecked")
public class JavaClassChooserMultipleComponentBuilder extends ComponentBuilder
{
   @Override
   public ForgeComponent build(final UIContext context, InputComponent<?, Object> input)
   {
      return new ListComponent((UIInputMany<Object>) input)
      {
         @Override
         protected String editSelectedItem(String item)
         {
            return IDEUtil.chooseClass(context, item);
         }

         @Override
         protected String findItemToAdd()
         {
            return IDEUtil.chooseClass(context, "");
         }
      };
   }

   @Override
   protected Class<?> getProducedType()
   {
      return String.class;
   }

   @Override
   protected String getSupportedInputType()
   {
      return InputType.JAVA_CLASS_PICKER;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UIInputMany.class };
   }
}
