/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.plugin.idea.ui.component.many.DirectoryChooserMultipleComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.many.FileChooserMultipleComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.many.TextBoxMultipleComponentBuilder;

/**
 * A factory for {@link ComponentBuilder} instances.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public enum ComponentBuilderRegistry
{
    INSTANCE;

    private ComponentBuilder[] componentBuilders = {
            new CheckboxComponentBuilder(),
            new ComboComponentBuilder(),
            new RadioComponentBuilder(),
            new TextBoxComponentBuilder(),
            new PasswordComponentBuilder(),
            new TextAreaComponentBuilder(),
            new FileChooserComponentBuilder(),
            new DirectoryChooserComponentBuilder(),
            new JavaClassChooserComponentBuilder(),
            new JavaPackageChooserComponentBuilder(),
            new TextBoxMultipleComponentBuilder(),
            new FileChooserMultipleComponentBuilder(),
            new DirectoryChooserMultipleComponentBuilder(),
            new FallbackTextBoxComponentBuilder()};

    public ComponentBuilder getBuilderFor(InputComponent<?, ?> input)
    {
        for (ComponentBuilder builder : componentBuilders)
        {
            if (builder.handles(input))
            {
                return builder;
            }
        }
        throw new IllegalArgumentException(
                "No UI component found for input type of "
                        + input.getValueType()
        );
    }
}
