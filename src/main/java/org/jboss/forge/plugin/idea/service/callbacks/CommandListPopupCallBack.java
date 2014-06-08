/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.service.callbacks;

import com.intellij.openapi.vfs.VirtualFile;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.plugin.idea.context.UIContextFactory;
import org.jboss.forge.plugin.idea.service.ServiceHelper;
import org.jboss.forge.plugin.idea.ui.CommandListPopupBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:danielsoro@gmail.com">Daniel Cunha (soro)</a>
 */
public class CommandListPopupCallBack implements Runnable
{
    private VirtualFile[] selectedFiles;

    public CommandListPopupCallBack(VirtualFile... selectedFiles)
    {
        this.selectedFiles = selectedFiles;
    }

    @Override
    public void run()
    {
        UIContext uiContext = UIContextFactory.create(selectedFiles);

        new CommandListPopupBuilder()
                .setUIContext(uiContext)
                .setCommands(getAllCandidates(uiContext))
                .build()
                .showInFocusCenter();
    }

    private List<UICommand> getAllCandidates(UIContext uiContext)
    {
        List<UICommand> commands = new ArrayList<UICommand>();
        CommandFactory commandFactory = ServiceHelper.getForgeService().getCommandFactory();

        for (UICommand command : commandFactory.getCommands())
        {
            if (isCandidate(command, uiContext))
            {
                commands.add(command);
            }
        }

        return commands;
    }

    private boolean isCandidate(UICommand command, UIContext uiContext)
    {
        return !(command instanceof UIWizardStep) && command.isEnabled(uiContext);
    }
}
