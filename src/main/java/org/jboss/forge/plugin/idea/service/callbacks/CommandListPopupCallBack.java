/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.service.callbacks;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.plugin.idea.context.UIContextFactory;
import org.jboss.forge.plugin.idea.service.ForgeService;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.ui.CommandListPopupBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:danielsoro@gmail.com">Daniel Cunha (soro)</a>
 */
public class CommandListPopupCallBack implements Runnable
{
    private final Project project;
    private final VirtualFile[] selectedFiles;

    public CommandListPopupCallBack(Project project, VirtualFile... selectedFiles)
    {
        this.project = project;
        this.selectedFiles = selectedFiles;
    }

    @Override
    public void run()
    {
        UIContext uiContext = UIContextFactory.create(project, selectedFiles);

        List<UICommand> candidates = getAllCandidates(uiContext);

        new CommandListPopupBuilder()
                .setUIContext(uiContext)
                .setCommands(candidates)
                .setRecentCommands(PluginService.getInstance().getRecentCommands(candidates, uiContext))
                .build()
                .showInFocusCenter();
    }

    private List<UICommand> getAllCandidates(UIContext uiContext)
    {
        List<UICommand> commands = new ArrayList<UICommand>();
        CommandFactory commandFactory = ForgeService.getInstance().getCommandFactory();

        for (UICommand command : commandFactory.getCommands())
        {
            if (Commands.isEnabled(command, uiContext))
            {
                commands.add(command);
            }
        }

        return commands;
    }
}
