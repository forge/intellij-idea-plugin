/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.wizards;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.plugin.idea.components.ComponentBuilder;
import org.jboss.forge.plugin.idea.components.ComponentBuilderRegistry;
import org.jboss.forge.plugin.idea.context.UIBuilderImpl;
import org.jboss.forge.proxy.Proxies;

import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;

public class ForgeWizardStep extends WizardStep<ForgeWizardModel>
{
   private UICommand uiCommand;
   private UIContext context;

   public ForgeWizardStep(UICommand command, UIContext context)
   {
      super(command.getMetadata().getDescription());
      this.uiCommand = command;
   }

   public UICommand getUICommand()
   {
      return uiCommand;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public JComponent prepare(WizardNavigationState state)
   {
      UIBuilderImpl uiBuilder = new UIBuilderImpl(context);
      try
      {
         uiCommand.initializeUI(uiBuilder);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      // Build panel
      JPanel container = new JPanel(new MigLayout("fillx,wrap 2",
               "[left]rel[grow,fill]", "[]10[]"));
      for (InputComponent input : uiBuilder.getInputs())
      {
         ComponentBuilder builder = ComponentBuilderRegistry.INSTANCE
                  .getBuilderFor(input);
         builder.build(input, container);
      }
      return container;
   }

   @Override
   public ForgeWizardStep onNext(ForgeWizardModel model)
   {
      // If it's not a wizard, we don't care
      if (!(uiCommand instanceof UIWizard))
      {
         return null;
      }
      UIWizard wiz = (UIWizard) uiCommand;
      NavigationResult nextCommand = null;
      try
      {
         nextCommand = wiz.next(model.getContext());
      }
      catch (Exception e)
      {
         // TODO: Use Eclipse logging mechanism
         e.printStackTrace();
      }
      // No next page
      if (nextCommand == null)
      {
         List<ForgeWizardStep> pageList = model.getSteps();
         int idx = pageList.indexOf(this);
         pageList.subList(idx + 1, pageList.size()).clear();
         return null;
      }
      else
      {
         Class<? extends UICommand> successor = nextCommand.getNext();
         // Do we have any pages already displayed ? (Did we went back
         // already ?)
         ForgeWizardStep nextPage = (ForgeWizardStep) super.onNext(model);
         if (nextPage == null
                  || !isNextStepAssignableFrom(nextPage, successor))
         {
            if (nextPage != null)
            {
               List<ForgeWizardStep> pageList = model.getSteps();
               int idx = pageList.indexOf(nextPage);
               // Clean the old pages
               pageList.subList(idx, pageList.size()).clear();
            }
            UICommand nextStep = ForgeService.INSTANCE.lookup(successor);
            nextPage = new ForgeWizardStep(nextStep, model.getContext());
            model.add(nextPage);
         }
         return nextPage;
      }
   }

   private boolean isNextStepAssignableFrom(ForgeWizardStep nextStep,
            Class<? extends UICommand> successor)
   {
      return Proxies.isInstance(successor, nextStep.getUICommand());
   }

}
