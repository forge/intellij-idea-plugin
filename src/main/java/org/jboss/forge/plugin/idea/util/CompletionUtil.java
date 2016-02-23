/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.HasCompleter;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;

import com.intellij.ui.TextFieldWithAutoCompletion;

/**
 * @author Adam Wyłuda
 */
@SuppressWarnings("unchecked")
public class CompletionUtil
{
   public static boolean hasCompletions(InputComponent<?, Object> input)
   {
      return input instanceof HasCompleter && ((HasCompleter<?, Object>) input).getCompleter() != null;
   }

   public static List<String> getCompletions(ConverterFactory converterFactory, UIContext context,
            InputComponent<?, Object> input, String text)
   {
      List<String> result = new ArrayList<>();
      UICompleter<Object> completer = ((HasCompleter<?, Object>) input).getCompleter();
      Converter<Object, String> converter = converterFactory.getConverter(input.getValueType(), String.class);

      Iterable<Object> proposals = completer.getCompletionProposals(context, input, text);

      if (proposals != null)
      {
         for (Object object : proposals)
         {
            if (object != null)
            {
               result.add(converter.convert(object));
            }
         }
      }

      return result;
   }

   public static TextFieldWithAutoCompletion<String> createTextFieldWithAutoCompletion(UIContext context,
            InputComponent<?, Object> input)
   {
      return createTextFieldWithAutoCompletion(context, hasCompletions(input));
   }

   public static TextFieldWithAutoCompletion<String> createTextFieldWithAutoCompletion(UIContext context,
            boolean hasCompletions)
   {
      return TextFieldWithAutoCompletion.create(
               IDEUtil.projectFromContext(context),
               Collections.<String> emptyList(),
               hasCompletions,
               "");
   }
}
