package org.jboss.forge.plugin.idea.util;

import com.intellij.util.Function;
import org.jboss.forge.addon.convert.Converter;

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
