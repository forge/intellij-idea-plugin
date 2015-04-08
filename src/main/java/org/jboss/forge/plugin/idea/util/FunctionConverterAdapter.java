/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import org.jboss.forge.addon.convert.Converter;

import com.intellij.util.Function;

/**
 * Forge Converter to IntelliJ Function adapter.
 *
 * @author Adam Wyłuda
 */
public class FunctionConverterAdapter<SOURCE_TYPE, TARGET_TYPE> implements Function<SOURCE_TYPE, TARGET_TYPE>
{
    private final Converter<SOURCE_TYPE, TARGET_TYPE> converter;

    public FunctionConverterAdapter(Converter<SOURCE_TYPE, TARGET_TYPE> converter)
    {
        this.converter = converter;
    }

    @Override
    public TARGET_TYPE fun(SOURCE_TYPE source)
    {
        return converter.convert(source);
    }
}
