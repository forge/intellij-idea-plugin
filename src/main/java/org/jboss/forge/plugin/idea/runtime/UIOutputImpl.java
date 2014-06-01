/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.runtime;

import org.jboss.forge.addon.ui.output.UIOutput;

import java.io.PrintStream;

/**
 * @author Adam Wyłuda
 */
public class UIOutputImpl implements UIOutput
{
    // TODO Redirect output to Forge console

    @Override
    public PrintStream out()
    {
        return System.out;
    }

    @Override
    public PrintStream err()
    {
        return System.err;
    }
}
