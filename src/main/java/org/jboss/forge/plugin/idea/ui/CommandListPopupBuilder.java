/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui;

import com.intellij.openapi.ui.popup.*;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.util.Function;
import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.plugin.idea.context.UIContextImpl;
import org.jboss.forge.plugin.idea.runtime.UIProgressMonitorImpl;
import org.jboss.forge.plugin.idea.runtime.UIRuntimeImpl;
import org.jboss.forge.plugin.idea.service.ForgeService;
import org.jboss.forge.plugin.idea.ui.wizard.ForgeWizardDialog;

import javax.swing.*;
import java.util.*;

/**
 * Lists all UI commands.
 *
 * @author Adam Wyłuda
 */
public class CommandListPopupBuilder
{
    private static final Icon FORGE_ICON = new ImageIcon(CommandListPopupBuilder.class.getResource("/icons/forge.png"));
    private static volatile boolean active;

    private UIContext uiContext;
    private List<UICommand> commands;

    public CommandListPopupBuilder setUIContext(UIContext uiContext)
    {
        this.uiContext = uiContext;
        return this;
    }

    public CommandListPopupBuilder setCommands(List<UICommand> commands)
    {
        this.commands = commands;
        return this;
    }

    public static boolean isActive()
    {
        return active;
    }

    public JBPopup build()
    {
        active = true;

        Map<UICommand, UICommandMetadata> metadataIndex = indexMetadata(commands);
        Map<String, List<UICommand>> categories = categorizeCommands(commands, metadataIndex);
        List<Object> elements = categoriesToList(sortCategories(categories, metadataIndex));

        JBList list = buildJBList(elements, metadataIndex);
        JBPopup popup = buildPopup(list, categories, metadataIndex);

        return popup;
    }

    private JBList buildJBList(List<Object> elements, final Map<UICommand, UICommandMetadata> metadataIndex)
    {
        final JBList list = new JBList();
        DefaultListModel model = new DefaultListModel();
        model.setSize(elements.size());

        list.setCellRenderer(new ListCellRendererWrapper<Object>()
        {
            @Override
            public void customize(JList list, Object data, int index,
                                  boolean selected, boolean hasFocus)
            {
                if (data instanceof UICommand)
                {
                    setIcon(FORGE_ICON);

                    UICommand command = (UICommand) data;
                    UICommandMetadata metadata = metadataIndex.get(command);

                    setText(metadata.getName());
                    setToolTipText(metadata.getDescription());
                }
                else if (data instanceof String)
                {
                    String string = (String) data;

                    setText(string);
                    setSeparator();
                }
            }
        });

        for (int i = 0; i < elements.size(); i++)
        {
            model.set(i, elements.get(i));
        }
        list.setModel(model);

        return list;
    }

    private JBPopup buildPopup(final JBList list,
                               final Map<String, List<UICommand>> categories,
                               final Map<UICommand, UICommandMetadata> metadataIndex)
    {
        final PopupChooserBuilder listPopupBuilder = JBPopupFactory.getInstance().createListPopupBuilder(list);
        listPopupBuilder.setTitle("Run a Forge command");
        listPopupBuilder.setResizable(true);
        listPopupBuilder.addListener(new JBPopupAdapter()
        {
            @Override
            public void onClosed(LightweightWindowEvent event)
            {
                CommandListPopupBuilder.this.active = false;
            }
        });
        listPopupBuilder.setItemChoosenCallback(new Runnable()
        {
            @Override
            public void run()
            {
                Object selectedObject = list.getSelectedValue();
                if (selectedObject instanceof UICommand)
                {
                    UICommand selectedCommand = (UICommand) selectedObject;
                    openWizard(selectedCommand);
                }
            }
        });
        listPopupBuilder.setFilteringEnabled(new Function<Object, String>()
        {
            @Override
            public String fun(Object object)
            {
                if (object instanceof UICommand)
                {
                    UICommand command = (UICommand) object;
                    UICommandMetadata metadata = metadataIndex.get(command);

                    return metadata.getCategory().toString() + " " + metadata.getName();
                }
                else if (object instanceof String)
                {
                    StringBuilder categoryStringBuilder = new StringBuilder();
                    categoryStringBuilder.append(object + " ");

                    for (UICommand command : categories.get(object))
                    {
                        categoryStringBuilder.append(metadataIndex.get(command).getName() + " ");
                    }

                    return categoryStringBuilder.toString();
                }
                else
                {
                    throw new IllegalArgumentException("Unknown object type: " + object.getClass());
                }
            }
        });

        return listPopupBuilder.createPopup();
    }

    private Map<UICommand, UICommandMetadata> indexMetadata(List<UICommand> commands)
    {
        Map<UICommand, UICommandMetadata> index = new HashMap<>();

        for (UICommand command : commands)
        {
            UICommandMetadata metadata = command.getMetadata(uiContext);
            index.put(command, metadata);
        }

        return index;
    }

    /**
     * Returns a list of pairs: (category name, list of commands). Sorted by category name, also each command list
     * is sorted by command name.
     */
    private List<Map.Entry<String, List<UICommand>>> sortCategories(Map<String, List<UICommand>> categories,
                                                                    final Map<UICommand, UICommandMetadata> index)
    {
        List<Map.Entry<String, List<UICommand>>> result = new ArrayList<>();

        // Sort each entry and add to the result
        for (Map.Entry<String, List<UICommand>> entry : categories.entrySet())
        {
            Collections.sort(entry.getValue(), new Comparator<UICommand>()
            {
                @Override
                public int compare(UICommand o1, UICommand o2)
                {
                    return index.get(o1).getName().compareTo(
                            index.get(o2).getName());
                }
            });

            result.add(entry);
        }

        // Sort result
        Collections.sort(result, new Comparator<Map.Entry<String, List<UICommand>>>()
        {
            @Override
            public int compare(Map.Entry<String, List<UICommand>> o1, Map.Entry<String, List<UICommand>> o2)
            {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        return result;
    }

    private Map<String, List<UICommand>> categorizeCommands(List<UICommand> commands,
                                                            Map<UICommand, UICommandMetadata> index)
    {
        Map<String, List<UICommand>> categories = new HashMap<>();

        for (UICommand command : commands)
        {
            UICommandMetadata metadata = index.get(command);
            String category = metadata.getCategory().getName();

            if (!categories.containsKey(category))
            {
                categories.put(category, new ArrayList<UICommand>());
            }

            categories.get(category).add(command);
        }

        return categories;
    }

    private List<Object> categoriesToList(List<Map.Entry<String, List<UICommand>>> categories)
    {
        List<Object> list = new ArrayList<>();

        for (Map.Entry<String, List<UICommand>> entry : categories)
        {
            list.add(entry.getKey());
            list.addAll(entry.getValue());
        }

        return list;
    }

    private void openWizard(UICommand command)
    {
        UIProgressMonitor monitor = new UIProgressMonitorImpl();

        ((UIContextImpl) uiContext).setProgressMonitor(monitor);
        UIRuntime uiRuntime = new UIRuntimeImpl(monitor);
        CommandControllerFactory controllerFactory = ForgeService.getInstance().getCommandControllerFactory();
        CommandController controller = controllerFactory.createController(uiContext, uiRuntime, command);

        ForgeWizardDialog dialog = new ForgeWizardDialog(controller);
        dialog.show();
    }
}
