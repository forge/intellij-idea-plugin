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
import net.miginfocom.swing.MigLayout;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;

import javax.swing.*;
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
            private CheckBoxList checkBoxList;

            @Override
            public void buildUI(Container container)
            {
                final UISelectMany inputMany = (UISelectMany) input;

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

                JPanel panel = new JPanel(new MigLayout("fill"));
                panel.setBorder(IdeBorderFactory.createTitledBorder(label, false));

                checkBoxList = new CheckBoxList();
                checkBoxList.setStringItems(choices);
                checkBoxList.setBorder(IdeBorderFactory.createBorder());
                panel.add(checkBoxList, "grow,height :150:200");

                checkBoxList.setCheckBoxListListener(new CheckBoxListListener()
                {
                    @Override
                    public void checkBoxSelectionChanged(int index, boolean value)
                    {
                        PluginService.getInstance().submitFormUpdate(
                                new FormUpdateCallback(converterFactory, inputMany, getSelectedItems(checkBoxList),
                                        valueChangeListener)
                        );
                    }
                });

                container.add(panel, "span 2,growx");
            }

            @Override
            public void updateState()
            {
                checkBoxList.setEnabled(input.isEnabled());
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
