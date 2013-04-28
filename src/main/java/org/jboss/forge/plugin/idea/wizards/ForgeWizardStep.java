/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.wizards;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jboss.forge.plugin.idea.components.ComponentBuilder;
import org.jboss.forge.plugin.idea.components.ComponentBuilderRegistry;
import org.jboss.forge.plugin.idea.context.UIBuilderImpl;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.input.InputComponent;

import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;

public class ForgeWizardStep extends WizardStep<ForgeWizardModel>
{
   private UICommand command;
   private UIContext context;

   public ForgeWizardStep(UICommand command, UIContext context)
   {
      super(command.getMetadata().getDescription());
      this.command = command;
   }

   public UICommand getCommand()
   {
      return command;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public JComponent prepare(WizardNavigationState state)
   {
      UIBuilderImpl uiBuilder = new UIBuilderImpl(context);
      try
      {
         command.initializeUI(uiBuilder);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      // Build panel
      JPanel container = new JPanel(new MigLayout("fillx,wrap 2", "[left]rel[grow,fill]", "[]10[]"));
      for (InputComponent input : uiBuilder.getInputs())
      {
         ComponentBuilder builder = ComponentBuilderRegistry.INSTANCE.getBuilderFor(input);
         builder.build(this, input, container);
      }
      return container;
   }
}
