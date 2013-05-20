/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.components;

import org.jboss.forge.addon.ui.input.InputComponent;

public class FallbackTextBoxComponentBuilder extends TextBoxComponentBuilder {
	@Override
	public final boolean handles(InputComponent<?, ?> input) {
		return true;
	}

}
