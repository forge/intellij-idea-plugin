/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.service.callbacks;

import java.awt.*;
import java.util.List;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.awt.RelativePoint;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.plugin.idea.context.UIContextFactory;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.ui.CommandListPopupBuilder;

/**
 * @author <a href="mailto:danielsoro@gmail.com">Daniel Cunha (soro)</a>
 */
public class CommandListPopupCallBack implements Runnable
{
   private final Project project;
   private final Editor editor;
   private final RelativePoint relativePoint;
   private final VirtualFile[] selectedFiles;

   public CommandListPopupCallBack(AnActionEvent event)
   {
      this.project = event.getData(DataKeys.PROJECT);
      VirtualFile[] files = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
      // If no file is selected, then set project directory as selection
      if (files == null || files.length == 0)
      {
         files = new VirtualFile[] { event.getData(DataKeys.PROJECT_FILE_DIRECTORY) };
      }
      this.editor = event.getData(DataKeys.EDITOR);
      this.selectedFiles = files;
      this.relativePoint = JBPopupFactory.getInstance().guessBestPopupLocation(event.getDataContext());
   }

   @Override
   public void run()
   {
      UIContext uiContext = UIContextFactory.create(project, editor, selectedFiles);

      List<UICommand> candidates = PluginService.getInstance().getEnabledCommands(uiContext);

      JBPopup list = new CommandListPopupBuilder()
               .setUIContext(uiContext)
               .setCommands(candidates)
               .setRecentCommands(PluginService.getInstance().getRecentCommands(candidates, uiContext))
               .build();

      if (project != null)
      {
         list.showCenteredInCurrentWindow(project);
      }
   }

}
