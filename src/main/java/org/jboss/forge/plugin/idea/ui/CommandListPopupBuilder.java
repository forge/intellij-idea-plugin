/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui;

import static org.jboss.forge.plugin.idea.util.CommandUtil.categoriesToList;
import static org.jboss.forge.plugin.idea.util.CommandUtil.categorizeCommands;
import static org.jboss.forge.plugin.idea.util.CommandUtil.indexFilterData;
import static org.jboss.forge.plugin.idea.util.CommandUtil.indexMetadata;
import static org.jboss.forge.plugin.idea.util.CommandUtil.sortCategories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.plugin.idea.context.UIContextImpl;
import org.jboss.forge.plugin.idea.runtime.UIProgressMonitorImpl;
import org.jboss.forge.plugin.idea.runtime.UIRuntimeImpl;
import org.jboss.forge.plugin.idea.service.ForgeService;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.ui.wizard.ForgeCommandTask;
import org.jboss.forge.plugin.idea.ui.wizard.ForgeWizardDialog;
import org.jboss.forge.plugin.idea.util.ForgeNotifications;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupAdapter;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.util.Function;

/**
 * Lists enabled UI commands.
 *
 * @author Adam Wyłuda
 */
public class CommandListPopupBuilder
{
   private static final Icon FORGE_ICON = new ImageIcon(CommandListPopupBuilder.class.getResource("/icons/forge.png"));
   private static volatile boolean active;

   private UIContext uiContext;
   private List<UICommand> commands;
   private List<UICommand> recentCommmands;

   public CommandListPopupBuilder setUIContext(UIContext uiContext)
   {
      this.uiContext = uiContext;
      return this;
   }

   public CommandListPopupBuilder setCommands(List<UICommand> commands)
   {
      this.commands = commands;
      return this;
   }

   public CommandListPopupBuilder setRecentCommands(List<UICommand> recentCommands)
   {
      this.recentCommmands = recentCommands;
      return this;
   }

   public static boolean isActive()
   {
      return active;
   }

   public JBPopup build()
   {
      active = true;

      List<UICommand> allCommands = new ArrayList<>();
      allCommands.addAll(commands);
      allCommands.addAll(recentCommmands);

      Map<UICommand, UICommandMetadata> metadataIndex = indexMetadata(allCommands, uiContext);
      Map<String, List<UICommand>> categories = categorizeCommands(commands, recentCommmands, metadataIndex);
      List<Object> elements = categoriesToList(sortCategories(categories, metadataIndex));
      Map<Object, String> filterIndex = indexFilterData(elements, categories, metadataIndex);

      JBList list = buildJBList(elements, metadataIndex);
      JBPopup popup = buildPopup(list, filterIndex);

      return popup;
   }

   private JBList buildJBList(List<Object> elements, final Map<UICommand, UICommandMetadata> metadataIndex)
   {
      final JBList list = new JBList();
      DefaultListModel model = new DefaultListModel();
      model.setSize(elements.size());

      list.setCellRenderer(new ListCellRendererWrapper<Object>()
      {
         @Override
         public void customize(JList list, Object data, int index,
                  boolean selected, boolean hasFocus)
         {
            if (data instanceof UICommand)
            {
               setIcon(FORGE_ICON);

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

      return list;
   }

   private JBPopup buildPopup(final JBList list,
            final Map<Object, String> filterIndex)
   {
      final PopupChooserBuilder listPopupBuilder = JBPopupFactory.getInstance().createListPopupBuilder(list);
      listPopupBuilder.setTitle("Run a Forge command");
      listPopupBuilder.setResizable(true);
      listPopupBuilder.addListener(new JBPopupAdapter()
      {
         @Override
         public void onClosed(LightweightWindowEvent event)
         {
            CommandListPopupBuilder.this.active = false;
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

               // Make sure that this cached command is still enabled
               if (selectedCommand.isEnabled(uiContext))
               {
                  openWizard(selectedCommand);
               }
            }
         }
      });
      listPopupBuilder.setFilteringEnabled(new Function<Object, String>()
      {
         @Override
         public String fun(Object object)
         {
            return filterIndex.get(object);
         }
      });


      return listPopupBuilder.createPopup();
   }

   private void openWizard(UICommand command)
   {
      String name = command.getMetadata(uiContext).getName();
      command = ForgeService.getInstance()
               .getCommandFactory()
               .getNewCommandByName(uiContext, name);

      // Since wizard will be able to modify Command state, the cache should be invalidated
      PluginService.getInstance().invalidateAndReloadCommands(uiContext);

      PluginService.getInstance().addRecentCommand(command, uiContext);

      UIProgressMonitor monitor = new UIProgressMonitorImpl();

      ((UIContextImpl) uiContext).setProgressMonitor(monitor);
      UIRuntime uiRuntime = new UIRuntimeImpl(monitor);
      CommandControllerFactory controllerFactory = ForgeService.getInstance().getCommandControllerFactory();
      CommandController controller = controllerFactory.createController(uiContext, uiRuntime, command);
      try
      {
         controller.initialize();
      }
      catch (Exception e)
      {
         ForgeNotifications.showErrorMessage(e);
      }
      if (controller.getInputs().isEmpty() && controller.canExecute())
      {
         new ForgeCommandTask(controller).queue();
      }
      else
      {
         ForgeWizardDialog dialog = new ForgeWizardDialog(controller);
         dialog.show();
      }
   }
}
