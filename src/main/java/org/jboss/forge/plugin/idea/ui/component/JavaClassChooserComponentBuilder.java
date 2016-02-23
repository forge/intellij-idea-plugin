/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import com.intellij.ui.TextFieldWithAutoCompletion;

/**
 * @author Adam Wyłuda
 */
public class JavaClassChooserComponentBuilder extends ComponentBuilder
{
   @Override
   public ForgeComponent build(final UIContext context, InputComponent<?, Object> input)
   {
      return new LabeledComponent(input, new ChooserComponent(context, input)
      {
         @Override
         public ActionListener createBrowseButtonActionListener(final TextFieldWithAutoCompletion<String> textField)
         {
            return new ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  String initialValue = textField.getText();
                  String value = IDEUtil.chooseClass(context, initialValue);
                  if (value != null)
                  {
                     textField.setText(value);
                  }
               }
            };
         }
      });
   }

   @Override
   protected Class<?> getProducedType()
   {
      return String.class;
   }

   @Override
   protected String getSupportedInputType()
   {
      return InputType.JAVA_CLASS_PICKER;
   }

   @Override
   protected Class<?>[] getSupportedInputComponentTypes()
   {
      return new Class<?>[] { UIInput.class };
   }
}
