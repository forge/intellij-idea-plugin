/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;

import net.miginfocom.swing.MigLayout;

public class RadioComponentBuilder extends ComponentBuilder
{
   @SuppressWarnings("unchecked")
   @Override
   public ForgeComponent build(UIContext context, final InputComponent<?, Object> input)
   {
      return new LabeledComponent(input, new ForgeComponent()
      {
         private JPanel radioContainer;
         private UISelectOne<Object> selectOne = (UISelectOne<Object>) input;
         private Converter<Object, String> converter = InputComponents
                  .getItemLabelConverter(converterFactory, selectOne);

         @Override
         public void buildUI(Container container)
         {
            radioContainer = new JPanel(new MigLayout("left"));
            container.add(radioContainer);
            radioContainer.setToolTipText(input.getDescription());
            addNoteLabel(container, radioContainer).setText(input.getNote());
         }

         @Override
         public void updateState()
         {
            for (JRadioButton button : getRadioButtons())
            {
               button.setEnabled(input.isEnabled());
            }
            if (!getChoices().equals(getInputValueChoices()))
            {
               reloadChoices();
            }
            else if (!getValue().equals(getInputValue()))
            {
               reloadValue();
            }
            radioContainer.setToolTipText(input.getDescription());
            updateNote(radioContainer, input.getNote());
         }

         private void reloadChoices()
         {
            radioContainer.removeAll();
            ButtonGroup group = new ButtonGroup();

            Object value = getInputValue();

            for (final String label : getInputValueChoices())
            {
               JRadioButton radio = new JRadioButton();

               radio.setText(label);
               radio.setSelected(label.equals(value));

               radio.addActionListener(e -> PluginService.getInstance().submitFormUpdate(
                        new FormUpdateCallback(converterFactory, input,
                                 label, valueChangeListener)));

               radioContainer.add(radio);
               group.add(radio);
            }
         }

         private void reloadValue()
         {
            String value = getInputValue();

            for (JRadioButton radio : getRadioButtons())
            {
               if (radio.getText().equals(value))
               {
                  radio.setSelected(true);
                  return;
               }
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
            if (value == null)
            {
               return "";
            }
            else
            {
               return Objects.toString(converter.convert(value), "");
            }
         }

         private List<String> getChoices()
         {
            List<String> result = new ArrayList<>();

            for (JRadioButton radio : getRadioButtons())
            {
               result.add(radio.getText());
            }

            return result;
         }

         private String getValue()
         {
            for (JRadioButton radio : getRadioButtons())
            {
               if (radio.isSelected())
               {
                  return radio.getText();
               }
            }

            return "";
         }

         private List<JRadioButton> getRadioButtons()
         {
            List<JRadioButton> result = new ArrayList<>();

            for (Component component : radioContainer.getComponents())
            {
               JRadioButton radio = (JRadioButton) component;
               result.add(radio);
            }

            return result;
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
      return InputType.RADIO;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UISelectOne.class };
   }
}
