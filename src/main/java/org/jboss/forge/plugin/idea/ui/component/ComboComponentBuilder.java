/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;

import com.intellij.openapi.ui.ComboBox;

public class ComboComponentBuilder extends ComponentBuilder
{

   @SuppressWarnings("unchecked")
   @Override
   public ForgeComponent build(UIContext context, final InputComponent<?, Object> input)
   {
      return new LabeledComponent(input, new ForgeComponent()
      {
         private ComboBox combo;
         private UISelectOne<Object> selectOne = (UISelectOne<Object>) input;
         private Converter<Object, String> converter = InputComponents.getItemLabelConverter(converterFactory,
                  selectOne);
         private DefaultComboBoxModel<String> model;

         @Override
         public void buildUI(Container container)
         {
            model = new DefaultComboBoxModel<>();

            combo = new ComboBox(model);
            container.add(combo);
            combo.addItemListener((ItemListener) e -> {
               // To prevent nullifying input's value when model is cleared
               if (e.getStateChange() == ItemEvent.SELECTED)
               {
                  Object selectedItem = model.getSelectedItem();

                  PluginService.getInstance().submitFormUpdate(
                           new FormUpdateCallback(converterFactory, input,
                                    selectedItem, valueChangeListener));
               }
            });
            combo.setToolTipText(input.getDescription());
            addNoteLabel(container, combo).setText(input.getNote());

         }

         @Override
         public void updateState()
         {
            combo.setEnabled(input.isEnabled());
            combo.setToolTipText(input.getDescription());

            if (!getInputValueChoices().equals(getChoices()) ||
                     !getInputValue().equals(getValue()))
            {
               reloadValue();
            }
            updateNote(combo, input.getNote());
         }

         private void reloadValue()
         {
            Iterable<Object> valueChoices = selectOne.getValueChoices();
            if (valueChoices != null)
            {
               model.removeAllElements();
               for (String choice : getInputValueChoices())
               {
                  model.addElement(choice);
               }
            }

            // Set Default Value
            Object value = InputComponents.getValueFor(input);
            if (value == null)
            {
               model.setSelectedItem(null);
            }
            else
            {
               String convertedValue = converter.convert(value);
               model.setSelectedItem(convertedValue);
            }
         }

         private List<String> getInputValueChoices()
         {
            List<String> list = new ArrayList<>();

            for (Object item : selectOne.getValueChoices())
            {
               if (item != null)
               {
                  list.add(converter.convert(item));
               }
            }

            return list;
         }

         private String getInputValue()
         {
            Object value = selectOne.getValue();
            return value == null ? "" : converter.convert(value);
         }

         private List<String> getChoices()
         {
            List<String> result = new ArrayList<>();

            for (int i = 0; i < model.getSize(); i++)
            {
               result.add(model.getElementAt(i));
            }

            return result;
         }

         private String getValue()
         {
            return (String) model.getSelectedItem();
         }
      });
   }

   @Override
   protected Class<Object> getProducedType()
   {
      return Object.class;
   }

   @Override
   protected String getSupportedInputType()
   {
      return InputType.DROPDOWN;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UISelectOne.class };
   }
}
