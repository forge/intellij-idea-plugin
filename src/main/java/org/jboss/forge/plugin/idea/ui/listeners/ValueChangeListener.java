/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.ui.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;
import org.jboss.forge.plugin.idea.ui.wizard.ForgeWizardModel;
import org.jboss.forge.plugin.idea.ui.wizard.NavigationState;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;

/**
 * @author <a href="mailto:danielsoro@gmail.com">Daniel Cunha (soro)</a>
 */
public class ValueChangeListener implements Runnable
{
   private final ForgeWizardModel model;
   private final Map<String, ForgeComponent> components;
   private final NavigationState navigationState;

   public ValueChangeListener(ForgeWizardModel model, Map<String, ForgeComponent> components,
            NavigationState navigationState)
   {
      this.model = model;
      this.components = components;
      this.navigationState = navigationState;
   }

   @Override
   public void run()
   {
      validate();
   }

   public void updateComponentsState()
   {
      for (ForgeComponent component : components.values())
      {
         component.updateState();
      }
   }

   private void validate()
   {
      final List<UIMessage> allMessages = navigationState.getController().validate();

      final Map<String, List<UIMessage>> messagesByInputName = new HashMap<>();
      final List<UIMessage> commandMessages = new ArrayList<>();

      for (String inputName : components.keySet())
      {
         messagesByInputName.put(inputName, new ArrayList<UIMessage>());
      }

      for (UIMessage message : allMessages)
      {
         if (message.getSource() == null)
         {
            commandMessages.add(message);
         }
         else
         {
            InputComponent<?, ?> source = message.getSource();
            messagesByInputName.get(source.getName()).add(message);
         }
      }

      // Must be invoked on UI thread
      ApplicationManager.getApplication().invokeLater((Runnable) () -> {
         processComponentMessages(messagesByInputName);
         processCommandMessages(commandMessages, allMessages);

         updateComponentsState();

         navigationState.refreshNavigationState();
      }, ModalityState.any());
   }

   private void processComponentMessages(Map<String, List<UIMessage>> messages)
   {
      for (Map.Entry<String, List<UIMessage>> entry : messages.entrySet())
      {
         ForgeComponent builder = components.get(entry.getKey());
         builder.displayMessages(entry.getValue());
      }
   }

   private void processCommandMessages(List<UIMessage> commandMessages, List<UIMessage> allMessages)
   {
      // Messages specific for command (not any input) should be displayed first
      for (UIMessage message : commandMessages)
      {
         if (message.getSeverity() == UIMessage.Severity.ERROR)
         {
            model.getDialog().setErrorMessage(message.getDescription());
            return;
         }
      }

      // Display first input validation error
      for (UIMessage message : allMessages)
      {
         if (message.getSeverity() == UIMessage.Severity.ERROR)
         {
            model.getDialog().setErrorMessage(message.getDescription());
            return;
         }
      }

      // If there are no errors
      model.getDialog().setErrorMessage(null);
   }
}
