/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.runtime;

import java.io.PrintStream;

import org.jboss.forge.addon.ui.output.UIOutput;

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

    @Override
    public void success(PrintStream printStream, String s)
    {
        // TODO Implement new UIOutputImpl methods
    }

    @Override
    public void error(PrintStream printStream, String s)
    {
        // TODO Implement new UIOutputImpl methods
    }

    @Override
    public void info(PrintStream printStream, String s)
    {
        // TODO Implement new UIOutputImpl methods
    }

    @Override
    public void warn(PrintStream printStream, String s)
    {
        // TODO Implement new UIOutputImpl methods
    }
}
