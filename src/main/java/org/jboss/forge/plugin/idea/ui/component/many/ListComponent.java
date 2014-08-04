/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component.many;

import com.intellij.ui.AddEditDeleteListPanel;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Adam Wyłuda
 */
public abstract class ListComponent extends ForgeComponent
{
    private final UIInputMany<?> input;

    private ListPanel panel;

    public ListComponent(UIInputMany<?> input)
    {
        this.input = input;
    }

    @Override
    public void buildUI(Container container)
    {
        String label = InputComponents.getLabelFor(input, false);

        List<String> initialValue = new ArrayList<>();

        Iterable inputValue = (Iterable) InputComponents.getValueFor(input);
        if (inputValue != null)
        {
            Converter stringConverter = converterFactory.getConverter(input.getValueType(), String.class);

            for (Object item : inputValue)
            {
                initialValue.add((String) stringConverter.convert(item));
            }
        }

        panel = new ListPanel(label, initialValue);
        container.add(panel, "span 2,growx");
    }

    @Override
    public void updateState()
    {
        panel.setEnabled(input.isEnabled());
    }

    protected abstract String editSelectedItem(String item);

    protected abstract String findItemToAdd();

    protected void componentUpdated()
    {
        PluginService.getInstance().submitFormUpdate(
                new FormUpdateCallback(converterFactory, input, panel.getValue(), valueChangeListener));
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
    }
}
