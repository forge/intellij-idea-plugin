/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupAdapter;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.plugin.idea.context.UIContextImpl;
import org.jboss.forge.plugin.idea.context.UISelectionImpl;
import org.jboss.forge.plugin.idea.runtime.UIProviderImpl;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all UI commands.
 *
 * @author Adam Wyłuda
 */
public class CommandListPopup
{
    // TODO Design and implement CommandListPopup

    volatile boolean active;

    public void show()
    {
        if (active)
            return;
        active = true;
        final VirtualFile[] selectedFiles = null;
//        final VirtualFile[] selectedFiles = e
//                .getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);

        final JBList list = new JBList();
        DefaultListModel model = new DefaultListModel();

        final Project project = null;
//        final Project project = e.getData(DataKeys.PROJECT);

        final List<UICommand> allCandidates = getAllCandidates();
        model.setSize(allCandidates.size());

        list.setCellRenderer(new ListCellRendererWrapper<UICommand>()
        {
            @Override
            public void customize(JList list, UICommand data, int index,
                                  boolean selected, boolean hasFocus)
            {
                if (data != null)
                {
                    setIcon(AllIcons.Nodes.Plugin);

                    // TODO Pass UIContext to getMetadata()
//                    setText(data.getMetadata().getName());
                    setText(data.getMetadata(
                            new UIContextImpl(new UISelectionImpl<Object>(new ArrayList<>()),
                                    new UIProviderImpl()) )
                            .getName());

                    // if (hasFocus)
                    // {
                    // HintManager.getInstance().showInformationHint(editor,
                    // data.getMetadata().getDescription());
                    // }
                }
            }
        });

        for (int i = 0; i < allCandidates.size(); i++)
        {
            model.set(i, allCandidates.get(i));
        }

        list.setModel(model);

        final PopupChooserBuilder listPopupBuilder = JBPopupFactory
                .getInstance().createListPopupBuilder(list);
        listPopupBuilder.setTitle("Run a Forge command");
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
                int selectedIndex = list.getSelectedIndex();
                UICommand selectedCommand = allCandidates.get(selectedIndex);
                openWizard(selectedCommand, selectedFiles);
            }
        }).createPopup().showInFocusCenter();//showCenteredInCurrentWindow(project);
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
        // TODO Call command.isEnabled() with UIContext
        return !(command instanceof UIWizardStep);
    }

    private void openWizard(UICommand command, VirtualFile[] files)
    {
        // TODO Use CommandController to obtain UICommand metadata
//        ForgeWizardModel model = new ForgeWizardModel(command.getMetadata().getName(), command, files);
//        ForgeWizardDialog dialog = new ForgeWizardDialog(model);
//        dialog.show();
    }
}
