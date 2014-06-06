/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
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
 * Creates a popup list and displays all the currently registered
 * {@link UICommand} instances
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
public class ShowCommandListAction extends AnAction
{
    @Override
    public void actionPerformed(AnActionEvent event)
    {
        if (CommandListPopupBuilder.isActive())
        {
            return;
        }

        VirtualFile[] files = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);

        // If no file is selected, then set project directory as selection
        if (files == null || files.length == 0)
        {
            files = new VirtualFile[]{event.getData(DataKeys.PROJECT_FILE_DIRECTORY)};
        }

        final VirtualFile[] selectedFiles = files;
        ServiceHelper.loadFurnaceAndRun(new Runnable()
        {
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
        });
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
