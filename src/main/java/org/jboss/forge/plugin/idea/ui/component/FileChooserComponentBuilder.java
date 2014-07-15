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

public class FileChooserComponentBuilder extends AbstractChooserComponentBuilder
{
    @Override
    protected TextFieldWithBrowseButton createTextField()
    {
        TextFieldWithBrowseButton textField = new TextFieldWithBrowseButton();
        textField.addBrowseFolderListener("Select a file", null, IDEUtil.projectFromContext(context),
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
        return textField;
    }

    @Override
    protected Class<File> getProducedType()
    {
        return File.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.FILE_PICKER;
    }
}
