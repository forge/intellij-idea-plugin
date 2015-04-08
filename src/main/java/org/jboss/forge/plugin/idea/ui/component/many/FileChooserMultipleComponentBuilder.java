/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component.many;

import java.io.File;

import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;

/**
 * @author Adam Wyłuda
 */
public class FileChooserMultipleComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(InputComponent<?, Object> input)
    {
        return new ListComponent((UIInputMany) input)
        {
            @Override
            protected String editSelectedItem(String item)
            {
                return IDEUtil.chooseFile(context, FileChooserDescriptorFactory.createSingleLocalFileDescriptor(), item);
            }

            @Override
            protected String findItemToAdd()
            {
                return IDEUtil.chooseFile(context, FileChooserDescriptorFactory.createSingleLocalFileDescriptor(), "");
            }
        };
    }

    @Override
    protected Class<?> getProducedType()
    {
        return File.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.FILE_PICKER;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInputMany.class};
    }
}
