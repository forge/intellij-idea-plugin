/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.context;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.container.util.Assert;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.input.InputComponent;

public class UIBuilderImpl implements UIBuilder {
	private List<InputComponent<?, ?>> inputs = new ArrayList<InputComponent<?, ?>>();
	private UIContext context;

	public UIBuilderImpl(UIContext context) {
		this.context = context;
	}

	@Override
	public UIBuilder add(InputComponent<?, ?> input) {
		Assert.notNull(input, "Input must not be null");
		inputs.add(input);
		return this;
	}

	public List<InputComponent<?, ?>> getInputs() {
		return inputs;
	}

	@Override
	public UIContext getUIContext() {
		return context;
	}
}
