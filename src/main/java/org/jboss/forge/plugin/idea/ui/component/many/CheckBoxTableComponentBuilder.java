/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component.many;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;

import com.intellij.ui.CheckBoxList;
import com.intellij.ui.CheckBoxListListener;
import com.intellij.ui.components.JBScrollPane;

import net.miginfocom.swing.MigLayout;

/**
 * @author Adam Wyłuda
 */
public class CheckBoxTableComponentBuilder extends ComponentBuilder
{
   @Override
   @SuppressWarnings("unchecked")
   public ForgeComponent build(UIContext context, final InputComponent<?, Object> input)
   {
      return new ForgeComponent()
      {
         private CheckBoxList<String> checkBoxList;
         private TitledBorder noteBorder;
         private UISelectMany<Object> inputMany = (UISelectMany<Object>) input;
         private Converter<Object, String> converter = InputComponents.getItemLabelConverter(converterFactory,
                  inputMany);

         @Override
         public void buildUI(Container container)
         {
            checkBoxList = new CheckBoxList<>();
            checkBoxList.setCheckBoxListListener(new CheckBoxListListener()
            {
               @Override
               public void checkBoxSelectionChanged(int index, boolean value)
               {
                  PluginService.getInstance().submitFormUpdate(
                           new FormUpdateCallback(converterFactory, inputMany, getValue(),
                                    valueChangeListener));
               }
            });

            // Adding titled label
            TitledBorder panelBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                     InputComponents.getLabelFor(input, false),
                     TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION);
            Font titleFont = panelBorder.getTitleFont();
            // Adding note in a border
            noteBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                     input.getNote(),
                     TitledBorder.LEFT, TitledBorder.BOTTOM,
                     titleFont.deriveFont(titleFont.getSize2D() - 2));
            Border border = new CompoundBorder(noteBorder, panelBorder);
            JPanel panel = new JPanel(new MigLayout("fill"));
            JBScrollPane scrollPane = new JBScrollPane(checkBoxList);
            scrollPane.setMinimumSize(new Dimension(300, 300));
            panel.setBorder(border);
            panel.add(scrollPane, "grow,height :150:300");
            container.add(panel, "span 2,growx");

            checkBoxList.setToolTipText(input.getDescription());
         }

         @Override
         public void updateState()
         {
            checkBoxList.setEnabled(input.isEnabled());

            if (!getInputValueChoices().equals(getChoices()) ||
                     !getInputValue().equals(getValue()))
            {
               reloadValues();
            }
            checkBoxList.setToolTipText(input.getDescription());
            noteBorder.setTitle(input.getNote());
         }

         private void reloadValues()
         {
            // Usage of List is necessary to preserve item order
            Map<String, Boolean> choices = new LinkedHashMap<>();
            List<String> inputValue = getInputValue();
            for (String item : getInputValueChoices())
            {
               choices.put(item, inputValue.contains(item));
            }

            checkBoxList.setStringItems(choices);
         }

         private List<String> getInputValueChoices()
         {
            List<String> list = new ArrayList<>();

            for (Object item : inputMany.getValueChoices())
            {
               String value = converter.convert(item);
               list.add(value != null ? value : "");
            }

            return list;
         }

         private List<String> getInputValue()
         {
            List<String> list = new ArrayList<>();

            for (Object item : inputMany.getValue())
            {
               list.add(converter.convert(item));
            }

            return list;
         }

         private List<String> getChoices()
         {
            List<String> result = new ArrayList<>();

            for (int i = 0; i < checkBoxList.getItemsCount(); i++)
            {
               result.add(checkBoxList.getItemAt(i));
            }

            return result;
         }

         private List<String> getValue()
         {
            List<String> result = new ArrayList<>();

            for (int i = 0; i < checkBoxList.getItemsCount(); i++)
            {
               if (checkBoxList.isItemSelected(i))
               {
                  result.add(checkBoxList.getItemAt(i));
               }
            }

            return result;
         }
      };
   }

   @Override
   protected Class<?> getProducedType()
   {
      return Object.class;
   }

   @Override
   protected String getSupportedInputType()
   {
      return InputType.CHECKBOX;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UISelectMany.class };
   }
}
