/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.components;

import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * A factory for {@link ControlBuilder} instances.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public enum ComponentBuilderRegistry
{
   INSTANCE;

   private ComponentBuilder[] componentBuilders = {
            new CheckboxComponentBuilder(),
            new ComboEnumComponentBuilder(),
            new ComboComponentBuilder(),
            new RadioComponentBuilder(),
            new FileChooserComponentBuilder(),
            // new CheckboxTableComponentBuilder(),
            new TextBoxComponentBuilder(),
            new PasswordComponentBuilder(),
            new FallbackTextBoxComponentBuilder() };

   public ComponentBuilder getBuilderFor(InputComponent<?, ?> input)
   {
      for (ComponentBuilder builder : componentBuilders)
      {
         if (builder.handles(input))
         {
            return builder;
         }
      }
      throw new IllegalArgumentException(
               "No UI component found for input type of "
                        + input.getValueType());
   }
}
