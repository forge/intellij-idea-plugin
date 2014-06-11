/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.ui.MessageType;
import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;

/**
 * Forge notification
 *
 * @author Adam Wyłuda
 */
public class ForgeNotifications
{
    private static final NotificationGroup NOTIFICATION_GROUP =
            NotificationGroup.balloonGroup("Forge Notifications");

    private static final String SUCCESS_MESSAGE = "Command executed successfully";
    private static final String FAILURE_MESSAGE = "Command execution failed";

    public static void showExecutionResult(Result result)
    {
        result = getFirstResult(result);
        NOTIFICATION_GROUP.createNotification(messageFrom(result), messageTypeOf(result)).notify(null);
    }

    private static String messageFrom(Result result)
    {
        if (result.getMessage() != null && !result.getMessage().isEmpty())
        {
            return result.getMessage();
        }

        if (result instanceof Failed)
        {
            return SUCCESS_MESSAGE;
        }

        return FAILURE_MESSAGE;
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