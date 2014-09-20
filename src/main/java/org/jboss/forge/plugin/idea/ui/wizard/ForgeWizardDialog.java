/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.wizard;

import com.intellij.openapi.project.Project;
import com.intellij.ui.wizard.WizardDialog;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;

import java.awt.*;

/**
 * Forge wizard (and single command) dialog.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
public class ForgeWizardDialog extends WizardDialog<ForgeWizardModel>
{
    private final String name;
    private final UIContext context;

    public ForgeWizardDialog(CommandController originalController)
    {
        super((Project) null, false, new ForgeWizardModel(originalController));

        myModel.setDialog(this);

        this.name = originalController.getMetadata().getName().trim();
        this.context = originalController.getContext();

        refreshTitle();
    }

    @Override
    public String getTitle()
    {
        return super.getTitle();
    }

    public void refreshTitle()
    {
        Object selection = context.getSelection().get();

        setTitle(name + (selection != null ? " [" + selection + "]" : ""));
    }

    @Override
    protected Dimension getWindowPreferredSize()
    {
        return new Dimension(500, 500);
    }

    public void setErrorMessage(String text)
    {
        super.setErrorText(text);
    }
}
