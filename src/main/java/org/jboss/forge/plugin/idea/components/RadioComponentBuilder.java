/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.components;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.proxy.Proxies;

public class RadioComponentBuilder extends ComponentBuilder
{

   @SuppressWarnings("unchecked")
   @Override
   public JComponent build(final InputComponent<?, Object> input, Container container)
   {
      JLabel label = new JLabel();
      label.setText(input.getLabel() == null ? input.getName() : input
               .getLabel());
      container.add(label);

      JPanel radioContainer = new JPanel(new FlowLayout());
      container.add(radioContainer);
      final ConverterFactory converterFactory = ForgeService.INSTANCE
               .getConverterFactory();
      UISelectOne<Object> selectOne = (UISelectOne<Object>) input;
      Converter<Object, String> itemLabelConverter = (Converter<Object, String>) InputComponents
               .getItemLabelConverter(converterFactory, selectOne);
      Object originalValue = InputComponents.getValueFor(input);
      Iterable<Object> valueChoices = selectOne.getValueChoices();
      ButtonGroup group = new ButtonGroup();
      if (valueChoices != null)
      {
         for (final Object choice : valueChoices)
         {
            final String itemLabel = itemLabelConverter.convert(choice);
            JRadioButton radio = new JRadioButton();

            radio.setText(itemLabel);
            radio.setSelected(choice.equals(originalValue));
            radio.addActionListener(new ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  InputComponents.setValueFor(converterFactory,
                           input, Proxies.unwrap(choice));
               }
            });
            radioContainer.add(radio);
            group.add(radio);
         }
      }
      return radioContainer;
   }

   @Override
   protected Class<Object> getProducedType()
   {
      return Object.class;
   }

   @Override
   protected InputType getSupportedInputType()
   {
      return InputType.SELECT_ONE_RADIO;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UISelectOne.class };
   }
}
