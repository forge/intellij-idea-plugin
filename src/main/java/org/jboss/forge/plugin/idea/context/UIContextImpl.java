/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.context;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.plugin.idea.service.ForgeService;

import com.intellij.openapi.project.Project;

public class UIContextImpl extends AbstractUIContext
{
   private final UISelection<?> initialSelection;
   private final UIProvider provider;

   private final Project project;
   private UIProgressMonitor monitor;

   UIContextImpl(Project project, UISelection<?> initialSelection, UIProvider provider)
   {
      this.project = project;
      this.initialSelection = initialSelection;
      this.provider = provider;
      initialize();
   }

   private void initialize()
   {
      Imported<UIContextListener> listener = ForgeService.getInstance()
               .lookupImported(UIContextListener.class);
      for (UIContextListener uiContextListener : listener)
      {
         if (uiContextListener != null)
         {
            try
            {
               uiContextListener.contextInitialized(this);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   @Override
   public void close()
   {
      super.close();
      Imported<UIContextListener> listener = ForgeService.getInstance()
               .lookupImported(UIContextListener.class);
      for (UIContextListener uiContextListener : listener)
      {
         if (uiContextListener != null)
         {
            try
            {
               uiContextListener.contextDestroyed(this);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      }

   }

   @SuppressWarnings("unchecked")
   @Override
   public UISelection<?> getInitialSelection()
   {
      return initialSelection;
   }

   @Override
   public UIProvider getProvider()
   {
      return provider;
   }

   public Project getProject()
   {
      return project;
   }

   public void setProgressMonitor(UIProgressMonitor monitor)
   {
      this.monitor = monitor;
   }

   public UIProgressMonitor getProgressMonitor()
   {
      return monitor;
   }
}
