/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component.many;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;
import org.jetbrains.annotations.Nullable;

import com.intellij.ui.AddEditDeleteListPanel;

/**
 * @author Adam Wyłuda
 */
public abstract class ListComponent extends ForgeComponent
{
    private final UIInputMany<Object> input;
    private Converter<Object, String> converter;

    private ListPanel panel;

    // No need to make form update during value update
    private boolean settingValue;

    public ListComponent(UIInputMany<Object> input)
    {
        this.input = input;

        converter = converterFactory.getConverter(input.getValueType(), String.class);
    }

    @Override
    public void buildUI(Container container)
    {
        String label = InputComponents.getLabelFor(input, false);

        List<String> initialValue = new ArrayList<>();

        Iterable inputValue = (Iterable) InputComponents.getValueFor(input);
        if (inputValue != null)
        {
            for (Object item : inputValue)
            {
                initialValue.add((String) converter.convert(item));
            }
        }

        panel = new ListPanel(label, initialValue);
        container.add(panel, "span 2,growx");
    }

    @Override
    public void updateState()
    {
        panel.setEnabled(input.isEnabled());

        if (!getInputValues().equals(panel.getValue()))
        {
            reloadValues();
        }
    }

    protected abstract String editSelectedItem(String item);

    protected abstract String findItemToAdd();

    protected void componentUpdated()
    {
        if (!settingValue)
        {
            PluginService.getInstance().submitFormUpdate(
                    new FormUpdateCallback(converterFactory, input, panel.getValue(), valueChangeListener));
        }
    }

    public void reloadValues()
    {
        try
        {
            settingValue = true;
            panel.setValue(getInputValues());
        }
        finally
        {
            settingValue = false;
        }
    }

    private List<String> getInputValues()
    {
        List<String> list = new ArrayList<>();

        for (Object item : input.getValue())
        {
            list.add(converter.convert(item));
        }

        return list;
    }

    protected class ListPanel extends AddEditDeleteListPanel<String>
    {
        public ListPanel(String title, List<String> initialValue)
        {
            super(title, initialValue);

            myListModel.addListDataListener(new ListDataListener()
            {
                @Override
                public void intervalAdded(ListDataEvent e)
                {
                    ListComponent.this.componentUpdated();
                }

                @Override
                public void intervalRemoved(ListDataEvent e)
                {
                    ListComponent.this.componentUpdated();
                }

                @Override
                public void contentsChanged(ListDataEvent e)
                {
                    ListComponent.this.componentUpdated();
                }
            });
        }

        @Nullable
        @Override
        protected String editSelectedItem(String item)
        {
            return ListComponent.this.editSelectedItem(item);
        }

        @Nullable
        @Override
        protected String findItemToAdd()
        {
            return ListComponent.this.findItemToAdd();
        }

        public List<String> getValue()
        {
            List<String> value = new ArrayList<>();

            Enumeration<String> elements = myListModel.elements();
            while (elements.hasMoreElements())
            {
                value.add(elements.nextElement());
            }

            return value;
        }

        public void setValue(List<String> value)
        {
            myListModel.removeAllElements();

            for (String element : value)
            {
                myListModel.addElement(element);
            }
        }
    }
}
