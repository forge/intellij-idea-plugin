/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.*;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.util.Function;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import javax.swing.*;
import java.util.*;

/**
 * Lists all UI commands.
 *
 * @author Adam Wyłuda
 */
public class CommandListPopup
{
    // TODO Refactor to builder pattern

    private static volatile boolean active;

    private final UIContext uiContext;

    public CommandListPopup(UIContext uiContext)
    {
        this.uiContext = uiContext;
    }

    public static boolean isActive()
    {
        return active;
    }

    public void show()
    {
        if (active)
            return;
        active = true;

        final JBList list = new JBList();
        DefaultListModel model = new DefaultListModel();

        List<UICommand> candidates = getAllCandidates();
        final Map<UICommand, UICommandMetadata> metadataIndex = indexMetadata(candidates);
        final Map<String, List<UICommand>> categories = categorizeCommands(candidates, metadataIndex);
        final List<Object> elements = categoriesToList(sortCategories(categories, metadataIndex));
        model.setSize(elements.size());

        list.setCellRenderer(new ListCellRendererWrapper<Object>()
        {
            @Override
            public void customize(JList list, Object data, int index,
                                  boolean selected, boolean hasFocus)
            {
                if (data instanceof UICommand)
                {
                    setIcon(AllIcons.Nodes.Plugin);

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

        final PopupChooserBuilder listPopupBuilder = JBPopupFactory.getInstance().createListPopupBuilder(list);
        listPopupBuilder.setTitle("Run a Forge command");
        listPopupBuilder.setResizable(true);
        listPopupBuilder.addListener(new JBPopupAdapter()
        {
            @Override
            public void onClosed(LightweightWindowEvent event)
            {
                CommandListPopup.this.active = false;
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

        JBPopup popup = listPopupBuilder.createPopup();
        popup.showInFocusCenter();
    }

    private List<UICommand> getAllCandidates()
    {
        List<UICommand> commands = new ArrayList<UICommand>();
        CommandFactory commandFactory = ServiceHelper.getForgeService().getCommandFactory();

        for (UICommand command : commandFactory.getCommands())
        {
            if (isCandidate(command))
            {
                commands.add(command);
            }
        }

        return commands;
    }

    private boolean isCandidate(UICommand command)
    {
        return !(command instanceof UIWizardStep) && command.isEnabled(uiContext);
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
        // TODO Use CommandController to obtain UICommand metadata
//        ForgeWizardModel model = new ForgeWizardModel(command.getMetadata().getName(), command, files);
//        ForgeWizardDialog dialog = new ForgeWizardDialog(model);
//        dialog.show();
    }
}
