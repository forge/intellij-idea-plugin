/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.furnace.util.Lists;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Adam Wyłuda
 */
public class ForgeServiceTest //extends LightCodeInsightFixtureTestCase
{
    // TODO Run test in real IntelliJ environment

    private ForgeService service;

    @Before
    public void prepare()
    {
        // This should be obtained and initialized using IntelliJ API:
        // service = ServiceHelper.getInstance();
        service = new ForgeService();
        service.createFurnace();
        service.initializeAddonRepositories(false);
    }

    @Test
    public void testFurnaceLoading()
    {
        startFurnace();

        assertTrue(service.isLoaded());
    }

    @Test
    public void testCommandLoading()
    {
        startFurnace();

        CommandFactory factory = service.getCommandFactory();
        assertNotNull(factory);

        List<UICommand> commands = Lists.toList(factory.getCommands());
        assertTrue(commands.size() > 0);
    }

    private void startFurnace()
    {
        try
        {
            service.startAsync().get();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
