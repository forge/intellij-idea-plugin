/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.action;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.plugin.idea.service.ServiceHelper;
import org.jboss.forge.plugin.idea.service.callbacks.CommandListPopupCallBack;
import org.jboss.forge.plugin.idea.ui.CommandListPopupBuilder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Creates a popup list and displays all the currently registered {@link UICommand} instances
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
      Project project = event.getData(DataKeys.PROJECT);
      VirtualFile[] files = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
      // If no file is selected, then set project directory as selection
      if (files == null || files.length == 0)
      {
         files = new VirtualFile[] { event.getData(DataKeys.PROJECT_FILE_DIRECTORY) };
      }
      Editor editor = event.getData(DataKeys.EDITOR);
      saveAllFiles();
      CommandListPopupCallBack callback = new CommandListPopupCallBack(project, editor, files);
      ServiceHelper.loadFurnaceAndRun(callback);
   }

   private void saveAllFiles()
   {
      FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
      fileDocumentManager.saveAllDocuments();
   }
}
