/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.components;

import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.proxy.Proxies;

import com.intellij.openapi.ui.ComboBox;

@SuppressWarnings("rawtypes")
public class ComboEnumComponentBuilder extends ComponentBuilder
{
   @SuppressWarnings("unchecked")
   @Override
   public JComponent build(final InputComponent<?, Object> input, Container container)
   {
      // Create the label
      JLabel label = new JLabel();
      label.setText(input.getLabel() == null ? input.getName() : input
               .getLabel());
      container.add(label);

      final ConverterFactory converterFactory = ForgeService.INSTANCE
               .getConverterFactory();
      final UISelectOne<Object> selectOne = (UISelectOne<Object>) input;
      final Converter<Object, String> converter = (Converter<Object, String>) InputComponents
               .getItemLabelConverter(converterFactory, selectOne);
      final DefaultComboBoxModel model = new DefaultComboBoxModel();
      Enum[] enumConstants = input.getValueType().asSubclass(Enum.class)
               .getEnumConstants();
      for (Enum enum1 : enumConstants)
      {
         model.addElement(enum1.name());
      }

      ComboBox combo = new ComboBox(model);
      container.add(combo);
      String value = converter.convert(InputComponents.getValueFor(input));
      Iterable<Object> valueChoices = selectOne.getValueChoices();
      if (valueChoices != null)
      {
         for (Object choice : valueChoices)
         {
            model.addElement(Proxies.unwrap(choice));
         }
      }
      combo.addItemListener(new ItemListener()
      {

         @Override
         public void itemStateChanged(ItemEvent e)
         {
            String selectedItem = (String) model.getSelectedItem();
            Class valueType = input.getValueType();
            InputComponents.setValueFor(converterFactory, input,
                     Enum.valueOf(valueType, selectedItem));
         }
      });

      // Set Default Value
      if (value == null)
      {
         if (model.getSize() > 0)
         {
            model.setSelectedItem(model.getElementAt(0));
         }
      }
      else
      {
         model.setSelectedItem(value);
      }
      return combo;
   }

   @Override
   protected Class<Enum> getProducedType()
   {
      return Enum.class;
   }

   @Override
   protected InputType getSupportedInputType()
   {
      return InputType.SELECT_ONE_DROPDOWN;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UISelectOne.class, UIInput.class };
   }
}
