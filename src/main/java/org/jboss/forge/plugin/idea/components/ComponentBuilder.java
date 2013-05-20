/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.components;

import java.awt.Container;

import javax.swing.JComponent;

import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.proxy.Proxies;

public abstract class ComponentBuilder
{

   /**
    * Builds a UI Component object based on the input
    *
    * @param input
    * @param page TODO
    *
    * @return
    */
   public abstract JComponent build(final InputComponent<?, Object> input,
            final Container container);

   /**
    * Returns the supported type this control may produce
    *
    * @return
    */
   protected abstract Class<?> getProducedType();

   /**
    * Returns the supported input type for this component
    *
    * @return
    */
   protected abstract InputType getSupportedInputType();

   /**
    * Returns the subclasses of {@link InputComponent}
    *
    * @return
    */
   protected abstract Class<?>[] getSupportedInputComponentTypes();

   /**
    * Tests if this builder may handle this specific input
    *
    * @param input
    * @return
    */
   public boolean handles(InputComponent<?, ?> input)
   {
      boolean handles = false;
      InputType inputTypeHint = InputComponents.getInputType(input);

      for (Class<?> inputType : getSupportedInputComponentTypes())
      {
         if (inputType.isAssignableFrom(input.getClass()))
         {
            handles = true;
            break;
         }
      }

      if (handles)
      {
         if (inputTypeHint != null)
         {
            handles = Proxies.areEquivalent(inputTypeHint,
                     getSupportedInputType());
         }
         else
         {
            // Fallback to standard type
            handles = getProducedType().isAssignableFrom(
                     input.getValueType());
         }
      }

      return handles;
   }

}
