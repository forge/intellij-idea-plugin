/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.proxy.Proxies;

import com.intellij.openapi.ui.ComboBox;

public class ComboComponentBuilder extends ComponentBuilder
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

      ComboBox combo = new ComboBox(model);
      combo.setRenderer(new ListCellRenderer()
      {

         @Override
         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                  boolean cellHasFocus)
         {
            Object obj = model.getElementAt(index);
            return new JLabel(converter.convert(obj));
         }
      });
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
            Object selectedItem = model.getSelectedItem();
            InputComponents.setValueFor(converterFactory, input, selectedItem);
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
   protected Class<Object> getProducedType()
   {
      return Object.class;
   }

   @Override
   protected InputType getSupportedInputType()
   {
      return InputType.SELECT_ONE_DROPDOWN;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UISelectOne.class };
   }
}
