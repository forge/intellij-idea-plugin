/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Eclipse Public License version 1.0, available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.wizards;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.plugin.idea.context.UIContextImpl;
import org.jboss.forge.plugin.idea.context.UISelectionImpl;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.context.UIContext;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.wizard.WizardModel;

/**
 * Represents the model of a wizard
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ForgeWizardModel extends WizardModel {
	private UICommand originalCommand;
	private UIContextImpl context;

	public ForgeWizardModel(String title, UICommand command, VirtualFile[] files) {
		super(title);
		context = new UIContextImpl(getSelection(files));
		this.originalCommand = command;
		addWizardSteps();
	}

	private void addWizardSteps() {
		add(new ForgeWizardStep(originalCommand, context));

		// TODO: Add more steps
	}

	@SuppressWarnings("rawtypes")
	private UISelectionImpl<Resource<?>> getSelection(VirtualFile[] files) {
		UISelectionImpl<Resource<?>> selection = null;
		if (files != null) {
			List<Resource<?>> result = new LinkedList<Resource<?>>();
			ConverterFactory converterFactory = ForgeService.INSTANCE
					.lookup(ConverterFactory.class);
			Converter<File, Resource> converter = converterFactory
					.getConverter(File.class, locateNativeClass(Resource.class));

			for (VirtualFile virtualFile : files) {
				File file = new File(virtualFile.getPath());
				Resource<?> resource = converter.convert(file);
				result.add(resource);
			}
			if (!result.isEmpty()) {
				selection = new UISelectionImpl<Resource<?>>(result);
			}
		}
		return selection;
	}

	@SuppressWarnings("unchecked")
	private <T> Class<T> locateNativeClass(Class<T> type) {
		Class<T> result = type;
		AddonRegistry registry = ForgeService.INSTANCE.getAddonRegistry();
		for (Addon addon : registry.getAddons()) {
			try {
				ClassLoader classLoader = addon.getClassLoader();
				result = (Class<T>) classLoader.loadClass(type.getName());
				break;
			} catch (ClassNotFoundException e) {
			}
		}
		return result;
	}

	public UIContext getContext() {
		return context;
	}

	public UICommand getOriginalCommand() {
		return originalCommand;
	}

}
