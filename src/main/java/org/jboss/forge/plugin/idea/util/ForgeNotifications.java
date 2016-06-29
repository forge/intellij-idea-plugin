/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.util.Strings;

import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.ui.MessageType;

/**
 * Forge notification
 *
 * @author Adam Wyłuda
 */
public class ForgeNotifications
{
   private ForgeNotifications(){}
   
   private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Forge Notifications");

   private static final String SUCCESS_MESSAGE = "Command executed successfully";
   private static final String FAILURE_MESSAGE = "Command execution failed";

   public static void showExecutionResult(Result result)
   {
      result = getFirstResult(result);
      NOTIFICATION_GROUP.createNotification(messageFrom(result), messageTypeOf(result)).notify(null);
   }

   public static void showErrorMessage(Exception ex)
   {
      try
      {
         String message = ex.getMessage();
         message = message != null ? message : ex.getClass().getCanonicalName();
         NOTIFICATION_GROUP.createNotification(message, MessageType.ERROR).notify(null);
      }
      catch (Exception anotherException)
      {
         // Don't rethrow, since showErrorMessage is usually used in a catch block
         anotherException.printStackTrace();
      }
   }

   private static String messageFrom(Result result)
   {
      if (result != null)
      {
         if (!Strings.isNullOrEmpty(result.getMessage()))
         {
            return result.getMessage();
         }

         if (result instanceof Failed)
         {
            return FAILURE_MESSAGE;
         }
      }
      return SUCCESS_MESSAGE;
   }

   private static MessageType messageTypeOf(Result result)
   {
      if (result instanceof Failed)
      {
         return MessageType.ERROR;
      }

      return MessageType.INFO;
   }

   private static Result getFirstResult(Result result)
   {
      while (result instanceof CompositeResult)
      {
         result = ((CompositeResult) result).getResults().get(0);
      }

      return result;
   }
}
