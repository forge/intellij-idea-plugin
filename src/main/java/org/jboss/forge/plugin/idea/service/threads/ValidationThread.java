/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service.threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;

/**
 * Updates and validates Forge wizard forms. Ensures that many request are performed serially.
 *
 * @author Adam Wyłuda
 * @see org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback
 */
public class ValidationThread extends Thread
{
    private final BlockingQueue<FormUpdateCallback> queue = new LinkedBlockingQueue<>();

    public ValidationThread()
    {
        super("ForgeValidationThread");
    }

    /**
     * Appends form update request to the queue. If there is already request pending for the
     * same component, it will be replaced with new callback.
     */
    public void setNextCallback(FormUpdateCallback nextCallback)
    {
        // Discard previous request for this component
        for (FormUpdateCallback callback : queue)
        {
            if (callback.getInput() == nextCallback.getInput())
            {
                queue.remove(callback);
                break;
            }
        }

        queue.add(nextCallback);
    }

    @Override
    public void run()
    {
        while (!isInterrupted())
        {
            try
            {
                FormUpdateCallback callback = queue.poll(1, TimeUnit.SECONDS);
                if (callback != null)
                {
                    callback.run();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
