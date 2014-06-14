/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import org.jboss.forge.furnace.proxy.ClassLoaderAdapterBuilder;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

/**
 * @author Adam Wyłuda
 */
public class ForgeProxies
{
    /**
     * Creates CLAC proxy of given type to specified instance. Returns null if either instance is null or is not
     * assignable to given type.
     */
    public static <T> T proxyTo(Class<T> targetClass, Object instance)
    {
        if (instance == null)
        {
            return null;
        }

        instance = Proxies.unwrap(instance);
        Class nativeTargetClass = ServiceHelper.getForgeService().locateNativeClass(targetClass);

        if (nativeTargetClass.isAssignableFrom(instance.getClass()))
        {
            return (T) ClassLoaderAdapterBuilder
                    .callingLoader(targetClass.getClassLoader())
                    .delegateLoader(instance.getClass().getClassLoader())
                    .enhance(instance, targetClass);
        }
        else
        {
            return null;
        }
    }
}
