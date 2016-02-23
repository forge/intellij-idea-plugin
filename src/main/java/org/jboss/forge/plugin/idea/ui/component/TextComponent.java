/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Container;
import java.util.List;
import java.util.Objects;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.plugin.idea.service.PluginService;
import org.jboss.forge.plugin.idea.service.callbacks.FormUpdateCallback;
import org.jboss.forge.plugin.idea.util.CompletionUtil;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.TextFieldWithAutoCompletion;

/**
 * Represents text components like TextArea or PasswordField.
 *
 * @author Adam Wyłuda
 */
public class TextComponent extends ForgeComponent
{
   private final UIContext context;
   private final InputComponent<?, Object> input;
   private final boolean oneLineMode;

   private TextFieldWithAutoCompletion<String> component;
   private Converter<Object, String> converter;

   public TextComponent(UIContext context, InputComponent<?, Object> input, boolean oneLineMode)
   {
      this.context = context;
      this.input = input;
      this.oneLineMode = oneLineMode;

      this.converter = converterFactory.getConverter(
               input.getValueType(), String.class);
   }

   @Override
   public void buildUI(Container container)
   {
      boolean hasCompletions = CompletionUtil.hasCompletions(input);
      component = CompletionUtil.createTextFieldWithAutoCompletion(context, hasCompletions);
      component.setOneLineMode(oneLineMode);
      component.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void beforeDocumentChange(DocumentEvent event)
         {
         }

         @Override
         public void documentChanged(DocumentEvent event)
         {
            PluginService.getInstance().submitFormUpdate(
                     new FormUpdateCallback(converterFactory, input, component.getText(), valueChangeListener));
         }
      });
      container.add(component);
      component.setToolTipText(input.getDescription());
      addNoteLabel(container, component).setText(input.getNote());
   }

   @Override
   public void updateState()
   {
      component.setEnabled(input.isEnabled());
      component.setToolTipText(input.getDescription());

      if (!getValue().equals(getInputValue()))
      {
         reloadValue();
      }
      if (CompletionUtil.hasCompletions(input))
      {
         component.setVariants(getCompletions());
      }
      updateNote(component, input.getNote());
   }

   private void reloadValue()
   {
      component.setText(getInputValue());
   }

   private String getInputValue()
   {
      Object value = InputComponents.getValueFor(input);
      if (value == null)
      {
         return "";
      }
      else
      {
         return Objects.toString(converter.convert(value), "");
      }
   }

   private String getValue()
   {
      return component.getText();
   }

   private List<String> getCompletions()
   {
      return CompletionUtil.getCompletions(converterFactory, context, input,
               component != null ? component.getText() : null);
   }
}
