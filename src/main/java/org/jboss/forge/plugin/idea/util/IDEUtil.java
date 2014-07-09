/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.plugin.idea.context.UIContextImpl;

import java.io.File;

/**
 * @author Adam Wyłuda
 */
public class IDEUtil
{
    public static void refreshProject(UIContext context)
    {
        refreshProject(projectFromContext(context));
    }

    public static void refreshProject(Project project)
    {
        project.getBaseDir().refresh(true, true);
    }

    public static void openSelection(UIContext context)
    {
        Project project = projectFromContext(context);

        FileResource resource = ForgeProxies.proxyTo(FileResource.class, context.getSelection());

        if (resource != null)
        {
            File file = new File(resource.getFullyQualifiedName());
            openFile(project, file);
        }
    }

    public static void openFile(Project project, File file)
    {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }

    public static Project projectFromContext(UIContext context)
    {
        context = Proxies.unwrap(context);
        Assert.isTrue(context instanceof UIContextImpl, "UIContext must be an instance of UIContextImpl");

        return ((UIContextImpl) context).getProject();
    }
}
