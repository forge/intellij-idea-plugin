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
            private UISelectOne<Object> selectOne = (UISelectOne) input;
            private Converter<Object, String> converter = InputComponents.getItemLabelConverter(converterFactory, selectOne);
            private DefaultComboBoxModel<String> model;

            @Override
            public void buildUI(Container container)
            {
                model = new DefaultComboBoxModel();

                combo = new ComboBox(model);
                container.add(combo);
                combo.addItemListener(new ItemListener()
                {
                    @Override
                    public void itemStateChanged(ItemEvent e)
                    {
                        // To prevent nullifying input's value when model is cleared
                        if (e.getStateChange() == ItemEvent.SELECTED)
                        {
                            Object selectedItem = model.getSelectedItem();

                            PluginService.getInstance().submitFormUpdate(
                                    new FormUpdateCallback(converterFactory, input,
                                            selectedItem, valueChangeListener));
                        }
                    }
                });
                addNoteLabel(container, combo).setText(input.getNote());

            }

            @Override
            public void updateState()
            {
                combo.setEnabled(input.isEnabled());

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
                String value = converter.convert(InputComponents.getValueFor(input));
                if (value == null)
                {
                    if (model.getSize() > 0)
                    {
                        Object element = model.getElementAt(0);
                        model.setSelectedItem(element);
                        InputComponents.setValueFor(converterFactory, input, element);
                    }
                }
                else
                {
                    model.setSelectedItem(value);
                }
            }

            private List<String> getInputValueChoices()
            {
                List<String> list = new ArrayList<>();

                for (Object item : selectOne.getValueChoices())
                {
                    list.add(converter.convert(item));
                }

                return list;
            }

            private String getInputValue()
            {
                String value = converter.convert(selectOne.getValue());
                return value != null ? value : "";
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
        return new Class<?>[]{UISelectOne.class};
    }
}
