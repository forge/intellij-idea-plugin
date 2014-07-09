/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import java.io.File;

/**
 * @author Adam Wyłuda
 */
public class DirectoryChooserComponentBuilder extends AbstractFileChooserComponentBuilder
{
    @Override
    protected TextFieldWithBrowseButton createTextField()
    {
        TextFieldWithBrowseButton textField = new TextFieldWithBrowseButton();
        textField.addBrowseFolderListener("Select a directory", null, IDEUtil.projectFromContext(context),
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
        return textField;
    }

    @Override
    protected Class<?> getProducedType()
    {
        return File.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.DIRECTORY_PICKER;
    }
}
