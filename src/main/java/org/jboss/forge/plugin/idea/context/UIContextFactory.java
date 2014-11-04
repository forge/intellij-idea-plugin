/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.context;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.plugin.idea.runtime.UIProviderImpl;
import org.jboss.forge.plugin.idea.service.ForgeService;

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
    public static UIContext create(Project project, VirtualFile[] files)
    {
        UIProvider provider = new UIProviderImpl();
        UISelection<?> initialSelection = getSelection(files);

        return new UIContextImpl(project, initialSelection, provider);
    }

    private static UISelection<?> getSelection(VirtualFile[] files)
    {
        UISelection<?> selection;

        if (files == null || files.length == 0)
        {
            selection = Selections.emptySelection();
        }
        else
        {
            List<Resource> resources = filesToResources(files);
            selection = Selections.from(resources.toArray());
        }

        return selection;
    }

    private static List<Resource> filesToResources(VirtualFile[] files)
    {
        List<Resource> result = new LinkedList<>();

        Converter<File, Resource> converter = getResourceConverter();

        for (VirtualFile virtualFile : files)
        {
            result.add(fileToResource(virtualFile, converter));
        }

        return result;
    }

    private static Converter<File, Resource> getResourceConverter()
    {
        ConverterFactory converterFactory = ForgeService.getInstance().getConverterFactory();
        Class<Resource> nativeResourceClass = ForgeService.getInstance().locateNativeClass(Resource.class);
        return converterFactory.getConverter(File.class, nativeResourceClass);
    }

    private static Resource fileToResource(VirtualFile file)
    {
        return fileToResource(file, getResourceConverter());
    }

    private static Resource fileToResource(VirtualFile file, Converter<File, Resource> converter)
    {
        File javaFile = new File(file.getPath());
        Resource resource = converter.convert(javaFile);
        return resource;
    }
}
