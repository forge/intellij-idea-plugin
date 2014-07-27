/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.ui.ComboBox;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.forge.plugin.idea.service.ForgeService;
import org.jboss.forge.plugin.idea.util.ForgeProxies;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ComboComponentBuilder extends ComponentBuilder
{

    @SuppressWarnings("unchecked")
    @Override
    public ForgeComponent build(final InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new ForgeComponent()
        {

            private ComboBox combo;

            @Override
            public void buildUI(Container container)
            {
                final ConverterFactory converterFactory = ForgeService.getInstance()
                        .getConverterFactory();
                final UISelectOne<Object> selectOne = ForgeProxies.proxyTo(UISelectOne.class, input);
                final Converter<Object, String> converter = (Converter<Object, String>) InputComponents
                        .getItemLabelConverter(converterFactory, selectOne);
                final DefaultComboBoxModel model = new DefaultComboBoxModel();

                combo = new ComboBox(model);
                container.add(combo);
                String value = converter.convert(InputComponents.getValueFor(input));
                Iterable<Object> valueChoices = selectOne.getValueChoices();
                if (valueChoices != null)
                {
                    model.removeAllElements();
                    for (Object choice : valueChoices)
                    {
                        model.addElement(converter.convert(Proxies.unwrap(choice)));
                    }
                }
                combo.addItemListener(new ItemListener()
                {

                    @Override
                    public void itemStateChanged(ItemEvent e)
                    {
                        Object selectedItem = model.getSelectedItem();
                        InputComponents.setValueFor(converterFactory, input, selectedItem);
                        valueChangeListener.run();
                    }
                });

                // Set Default Value
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

            @Override
            public void updateState()
            {
                combo.setEnabled(input.isEnabled());
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
