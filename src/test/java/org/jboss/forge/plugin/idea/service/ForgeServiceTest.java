/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Adam Wyłuda
 */
public class ForgeServiceTest //extends LightCodeInsightFixtureTestCase
{
    // TODO Run test in real IntelliJ environment

    @Test
    public void testFurnaceLoading() throws Exception
    {
        // This should be obtained and initialized using IntelliJ API:
        // ForgeService service = ServiceHelper.getForgeService();
        ForgeService service = new ForgeService();
        service.createFurnace();
        service.initializeAddonRepositories(false);

        service.startAsync().get();
        assertTrue(service.isLoaded());
    }
}
