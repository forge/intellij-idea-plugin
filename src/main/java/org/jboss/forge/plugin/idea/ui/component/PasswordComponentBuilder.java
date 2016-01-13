/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Container;

import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;

public class PasswordComponentBuilder extends ComponentBuilder
{
   @Override
   public ForgeComponent build(UIContext context, final InputComponent<?, Object> input)
   {
      return new LabeledComponent(input, new ForgeComponent()
      {
         private JPasswordField component;
         private Converter<Object, String> converter = converterFactory.getConverter(
                  input.getValueType(), String.class);

         // No need to make form update during value update
         private boolean settingValue = false;

         @Override
         public void buildUI(Container container)
         {
            component = new JPasswordField();

            component.getDocument().addDocumentListener(new DocumentListener()
            {
               @Override
               public void removeUpdate(DocumentEvent e)
               {
                  if (!settingValue)
                  {
                     PluginService.getInstance().submitFormUpdate(
                              new FormUpdateCallback(converterFactory, input,
                                       new String(component.getPassword()), valueChangeListener));
                  }
               }

               @Override
               public void insertUpdate(DocumentEvent e)
               {
                  if (!settingValue)
                  {
                     PluginService.getInstance().submitFormUpdate(
                              new FormUpdateCallback(converterFactory, input,
                                       new String(component.getPassword()), valueChangeListener));
                  }
               }

               @Override
               public void changedUpdate(DocumentEvent e)
               {
                  PluginService.getInstance().submitFormUpdate(
                           new FormUpdateCallback(converterFactory, input,
                                    new String(component.getPassword()), valueChangeListener));
               }
            });

            container.add(component);
            component.setToolTipText(input.getDescription());
            addNoteLabel(container, component).setText(input.getNote());
         }

         @Override
         public void updateState()
         {
            component.setEnabled(input.isEnabled());

            if (!new String(component.getPassword()).equals(getInputValue()))
            {
               reloadValue();
            }
            component.setToolTipText(input.getDescription());
            updateNote(component, input.getNote());
         }

         private void reloadValue()
         {
            String value = getInputValue();
            try
            {
               settingValue = true;
               component.setText(value == null ? "" : value);
            }
            finally
            {
               settingValue = false;
            }
         }

         private String getInputValue()
         {
            Object value = input.getValue();
            return (value == null) ? null : converter.convert(value);
         }
      });
   }

   @Override
   protected String getSupportedInputType()
   {
      return InputType.SECRET;
   }

   @Override
   protected Class<String> getProducedType()
   {
      return String.class;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UIInput.class };
   }
}
