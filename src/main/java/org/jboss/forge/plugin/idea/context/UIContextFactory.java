/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.context;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.plugin.idea.runtime.UIProviderImpl;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides UIContext instances with UISelection from IDE.
 *
 * @author Adam Wyłuda
 */
public class UIContextFactory
{
    public static UIContext create(DataContext dataContext)
    {
        UIProvider provider = new UIProviderImpl();
        UISelection<?> initialSelection = getSelection(dataContext);

        return new UIContextImpl(initialSelection, provider);
    }

    private static UISelection<?> getSelection(DataContext dataContext)
    {
        VirtualFile[] files = DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        List<Resource<?>> resources = filesToResources(files);
        UISelection<Resource<?>> selection = new UISelectionImpl<>(resources);
        return selection;
    }

    @SuppressWarnings("rawtypes")
    private static List<Resource<?>> filesToResources(VirtualFile[] files)
    {
        List<Resource<?>> result = new LinkedList<Resource<?>>();
        ConverterFactory converterFactory = ServiceHelper.getForgeService().getConverterFactory();
        Class<Resource> nativeResourceClass = ServiceHelper.getForgeService().locateNativeClass(Resource.class);
        Converter<File, Resource> converter = converterFactory.getConverter(File.class, nativeResourceClass);

        if (files != null)
        {
            for (VirtualFile virtualFile : files)
            {
                File file = new File(virtualFile.getPath());
                Resource<?> resource = converter.convert(file);
                result.add(resource);
            }
        }

        return result;
    }
}
