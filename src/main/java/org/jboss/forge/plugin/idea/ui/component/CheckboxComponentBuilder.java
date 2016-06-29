/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;

/**
 * Creates a Checkbox
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CheckboxComponentBuilder extends ComponentBuilder
{

   @Override
   public ForgeComponent build(UIContext context, final InputComponent<?, Object> input)
   {
      return new ForgeComponent()
      {
         private JCheckBox checkbox;
         private Converter<Object, Boolean> converter = converterFactory
                  .getConverter(input.getValueType(), Boolean.class);

         @Override
         public void buildUI(Container container)
         {
            // Create the label
            String text = input.getLabel() == null ? input.getName() : input
                     .getLabel();
            checkbox = new JCheckBox(text);

            checkbox.addActionListener(new ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  PluginService.getInstance().submitFormUpdate(
                           new FormUpdateCallback(converterFactory, input,
                                    checkbox.isSelected(), valueChangeListener));
               }
            });

            container.add(checkbox, "skip");
            checkbox.setToolTipText(input.getDescription());
            addNoteLabel(container, checkbox).setText(input.getNote());
         }

         @Override
         public void updateState()
         {
            checkbox.setEnabled(input.isEnabled());

            if (checkbox.isSelected() != getInputValue())
            {
               reloadValue();
            }
            checkbox.setToolTipText(input.getDescription());
            updateNote(checkbox, input.getNote());
         }

         private void reloadValue()
         {
            Boolean value = getInputValue();
            checkbox.setSelected(value == null ? false : value);
         }

         private boolean getInputValue()
         {
            Object value = InputComponents.getValueFor(input);
            return value != null && converter.convert(value);
         }
      };
   }

   @Override
   protected Class<Boolean> getProducedType()
   {
      return Boolean.class;
   }

   @Override
   protected String getSupportedInputType()
   {
      return InputType.CHECKBOX;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UIInput.class };
   }

}
