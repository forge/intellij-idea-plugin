package org.jboss.forge.plugin.idea.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;

/**
 * IntelliJ service utilities.
 *
 * @author Adam Wyłuda
 */
public class ServiceHelper
{
    private ServiceHelper()
    {
    }

    public static ForgeService getForgeService()
    {
        return ServiceManager.getService(ForgeService.class);
    }

    public static UIService getUIService()
    {
        return ServiceManager.getService(UIService.class);
    }

    /**
     * Makes sure that Furnace is loaded and then executes given callback on IntelliJ UI thread.
     */
    public static void loadFurnaceAndRun(Runnable callback)
    {
        if (getForgeService().isLoaded())
        {
            runOnUIThread(callback);
        }
        else
        {
            getForgeService().startAsync();
            startForgeInitTask();
            runAfterFurnaceLoaded(callback);
        }
    }

    /**
     * Starts Forge initiation background task.
     */
    private static void startForgeInitTask()
    {
        // TODO Implement startForgeInitTask()
    }

    /**
     * Binds given action to Furnace lifecycle.
     */
    private static void runAfterFurnaceLoaded(Runnable callback)
    {
        // TODO Implement runAfterFurnaceLoaded()
    }

    /**
     * Runs provided Runnable on Swing dispatch thread.
     */
    private static void runOnUIThread(Runnable runnable)
    {
        if (ApplicationManager.getApplication().isDispatchThread())
        {
            runnable.run();
        }
        else
        {
            ApplicationManager.getApplication().invokeLater(runnable);
        }
    }
}
