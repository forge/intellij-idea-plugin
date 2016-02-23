/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.wizard;

import org.jboss.forge.addon.ui.controller.CommandController;

import com.intellij.ui.wizard.WizardModel;

/**
 * Represents the model of a wizard
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
public class ForgeWizardModel extends WizardModel
{
   private ForgeWizardDialog dialog;

   public ForgeWizardModel(CommandController originalController)
   {
      super(originalController.getMetadata().getName());

      ForgeWizardStep step = new ForgeWizardStep(this, originalController);
      add(step);
      step.refreshNavigationState();
   }

   public ForgeWizardDialog getDialog()
   {
      return dialog;
   }

   public void setDialog(ForgeWizardDialog dialog)
   {
      this.dialog = dialog;
   }
}
