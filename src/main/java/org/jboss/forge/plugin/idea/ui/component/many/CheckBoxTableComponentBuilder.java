/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component.many;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

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
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.Function;

import net.miginfocom.swing.MigLayout;

/**
 * @author Adam Wyłuda
 */
public class CheckBoxTableComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(UIContext context, final InputComponent<?, Object> input)
    {
        return new ForgeComponent()
        {
            private CheckBoxList<String> checkBoxList;
            private UISelectMany inputMany = (UISelectMany) input;
            private Converter<Object, String> converter
                    = InputComponents.getItemLabelConverter(converterFactory, inputMany);

            @Override
            public void buildUI(Container container)
            {
                JPanel panel = new JPanel(new MigLayout("fill"));

                String label = InputComponents.getLabelFor(input, false);
                panel.setBorder(IdeBorderFactory.createTitledBorder(label, false));

                checkBoxList = new CheckBoxList<>();
                checkBoxList.setBorder(IdeBorderFactory.createBorder());

                JBScrollPane scrollPane = new JBScrollPane(checkBoxList);
                panel.add(scrollPane, "grow,height :150:200");

                checkBoxList.setCheckBoxListListener(new CheckBoxListListener()
                {
                    @Override
                    public void checkBoxSelectionChanged(int index, boolean value)
                    {
                        PluginService.getInstance().submitFormUpdate(
                                new FormUpdateCallback(converterFactory, inputMany, getValue(),
                                        valueChangeListener)
                        );
                    }
                });

                container.add(panel, "span 2,growx");
                checkBoxList.setToolTipText(input.getDescription());
                addNoteLabel(container, checkBoxList).setText(input.getNote());
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
                updateNote(checkBoxList, input.getNote());
            }

            private void reloadValues()
            {
                // Usage of List is necessary to preserve item order
                List<String> choices = new ArrayList<>();

                for (String item : getInputValueChoices())
                {
                    choices.add(item);
                }

                checkBoxList.setItems(choices, new Function<String, String>()
                {
                    @Override
                    public String fun(String s)
                    {
                        return s;
                    }
                });

                // Select input values
                for (String item : getInputValue())
                {
                    checkBoxList.setItemSelected(item, true);
                }
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
        return new Class<?>[]{UISelectMany.class};
    }
}
