/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.HasCompleter;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Adam Wyłuda
 */
public class CompletionUtil
{
    public static boolean hasCompletions(InputComponent input)
    {
        HasCompleter hasCompleter = input instanceof HasCompleter ? (HasCompleter) input : null;
        return hasCompleter != null && hasCompleter.getCompleter() != null;
    }

    public static List<String> getCompletions(ConverterFactory converterFactory, UIContext context,
                                              InputComponent input, String text)
    {
        List<String> result = new ArrayList<>();
        UICompleter completer = ((HasCompleter) input).getCompleter();
        Converter converter = converterFactory.getConverter(input.getValueType(), String.class);

        Iterable proposals = completer.getCompletionProposals(context, input, text);

        if (proposals != null)
        {
            for (Object object : proposals)
            {
                result.add((String) converter.convert(object));
            }
        }

        return result;
    }

    public static TextFieldWithAutoCompletion createTextFieldWithAutoCompletion(UIContext context, boolean hasCompletions)
    {
        return TextFieldWithAutoCompletion.create(
                IDEUtil.projectFromContext(context),
                Collections.<String>emptyList(),
                hasCompletions,
                ""
        );
    }
}
