/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component.many;

import com.intellij.ui.CheckBoxList;
import com.intellij.ui.CheckBoxListListener;
import com.intellij.ui.IdeBorderFactory;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.ServiceHelper;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;
import org.jboss.forge.plugin.idea.util.ForgeProxies;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adam Wyłuda
 */
public class CheckBoxTableComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(final InputComponent<?, Object> input)
    {
        return new ForgeComponent()
        {
            @Override
            public void buildUI(Container container)
            {
                final UISelectMany inputMany = ForgeProxies.proxyTo(UISelectMany.class, input);

                final ConverterFactory converterFactory = ServiceHelper.getForgeService().getConverterFactory();
                Converter<Object, String> converter = InputComponents.getItemLabelConverter(converterFactory, inputMany);

                Map<String, Boolean> choices = new HashMap<>();
                for (Object item : inputMany.getValueChoices())
                {
                    choices.put(converter.convert(item), false);
                }

                for (Object item : inputMany.getValue())
                {
                    choices.put(converter.convert(item), true);
                }

                String label = InputComponents.getLabelFor(input, false);

                final CheckBoxList checkBoxList = new CheckBoxList();
                checkBoxList.setBorder(IdeBorderFactory.createTitledBorder(label, false));
                checkBoxList.setStringItems(choices);

                checkBoxList.setCheckBoxListListener(new CheckBoxListListener()
                {
                    @Override
                    public void checkBoxSelectionChanged(int index, boolean value)
                    {
                        InputComponents.setValueFor(converterFactory, inputMany, getSelectedItems(checkBoxList));
                        valueChangeListener.run();
                    }
                });

                container.add(checkBoxList, "span 2,growx,height :350:");
            }

            private List getSelectedItems(CheckBoxList checkBoxList)
            {
                List result = new ArrayList();

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
        return new Class<?>[]{UISelectMany.class};
    }
}
