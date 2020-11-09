/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.plugin.idea.service.ServiceHelper;
import org.jboss.forge.plugin.idea.service.callbacks.CommandListPopupCallBack;
import org.jboss.forge.plugin.idea.ui.CommandListPopupBuilder;

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
      // Save all files
      FileDocumentManager.getInstance().saveAllDocuments();
      ServiceHelper.loadFurnaceAndRun(new CommandListPopupCallBack(event));
   }
}
