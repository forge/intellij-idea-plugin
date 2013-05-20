/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.context;

import org.jboss.forge.addon.ui.context.AbstractUIContext;

public class UIContextImpl extends AbstractUIContext {
	private UISelectionImpl<?> currentSelection;

	public UIContextImpl(UISelectionImpl<?> selection) {
		this.currentSelection = selection;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UISelectionImpl<?> getInitialSelection() {
		return currentSelection;
	}

}
