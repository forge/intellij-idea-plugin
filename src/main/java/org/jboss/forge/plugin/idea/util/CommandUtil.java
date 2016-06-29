/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICategory;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.plugin.idea.service.ForgeService;

/**
 * Forge command utilities.
 *
 * @author Adam Wyłuda
 */
public class CommandUtil
{
    private CommandUtil(){}

    private static final String RECENT_COMMANDS = "Recent Commands";

    public static List<UICommand> getAllCommands()
    {
        CommandFactory commandFactory = ForgeService.getInstance().getCommandFactory();
        return Lists.toList(commandFactory.getCommands());
    }

    public static Map<UICommand, UICommandMetadata> indexMetadata(List<UICommand> commands, UIContext context)
    {
        Map<UICommand, UICommandMetadata> index = new HashMap<>();

        for (UICommand command : commands)
        {
            UICommandMetadata metadata = command.getMetadata(context);
            index.put(command, metadata);
        }

        return index;
    }

    public static Map<Object, String> indexFilterData(List<Object> elements,
                                                      Map<String, List<UICommand>> categories,
                                                      Map<UICommand, UICommandMetadata> metadataIndex)
    {
        Map<Object, String> result = new HashMap<>();

        for (Object object : elements)
        {
            if (object instanceof UICommand)
            {
                UICommand command = (UICommand) object;
                UICommandMetadata metadata = metadataIndex.get(command);

                result.put(object, categoryName(metadata.getCategory()) + " " + metadata.getName());
            }
            else if (object instanceof String)
            {
                StringBuilder categoryStringBuilder = new StringBuilder();

                for (UICommand command : categories.get(object))
                {
                    UICommandMetadata metadata = metadataIndex.get(command);

                    categoryStringBuilder.append(categoryName(metadata.getCategory()) + " " + metadata.getName() + " ");
                }

                result.put(object, categoryStringBuilder.toString());
            }
            else
            {
                throw new IllegalArgumentException("Unknown object type: " + object.getClass());
            }
        }
        return result;
    }

    /**
     * Returns a list of pairs: (category name, list of commands). Sorted by category name, also each command list
     * is sorted by command name.
     */
    public static List<Map.Entry<String, List<UICommand>>> sortCategories(Map<String, List<UICommand>> categories,
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
                String o1Name = o1.getKey();
                String o2Name = o2.getKey();

                if (o1Name.equals(RECENT_COMMANDS))
                {
                    return -1;
                }

                if (o2Name.equals(RECENT_COMMANDS))
                {
                    return 1;
                }

                return o1Name.compareTo(o2Name);
            }
        });

        return result;
    }

    public static Map<String, List<UICommand>> categorizeCommands(List<UICommand> commands,
                                                                  List<UICommand> recentCommands,
                                                                  Map<UICommand, UICommandMetadata> index)
    {
        Map<String, List<UICommand>> categories = new HashMap<>();

        for (UICommand command : commands)
        {
            UICommandMetadata metadata = index.get(command);
            String category = categoryName(metadata.getCategory());

            if (!categories.containsKey(category))
            {
                categories.put(category, new ArrayList<UICommand>());
            }

            categories.get(category).add(command);
        }

        if (!recentCommands.isEmpty())
        {
            categories.put(RECENT_COMMANDS, recentCommands);
        }

        return categories;
    }

    public static List<Object> categoriesToList(List<Map.Entry<String, List<UICommand>>> categories)
    {
        List<Object> list = new ArrayList<>();

        for (Map.Entry<String, List<UICommand>> entry : categories)
        {
            list.add(entry.getKey());
            list.addAll(entry.getValue());
        }

        return list;
    }

    private static String categoryName(UICategory category)
    {
        if (category == null)
        {
            category = Categories.createDefault();
        }

        StringBuilder name = new StringBuilder();

        name.append(category.getName().trim());
        category = category.getSubCategory();

        while (category != null)
        {
            name.append(" / ");
            name.append(category.getName().trim());
            category = category.getSubCategory();
        }

        return name.toString();
    }

    public static List<UICommand> getEnabledCommands(UIContext uiContext)
    {
        return Lists.toList(Commands.getEnabledCommands(getAllCommands(), uiContext));
    }
}
