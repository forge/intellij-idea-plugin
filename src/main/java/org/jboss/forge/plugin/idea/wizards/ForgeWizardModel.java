/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Eclipse Public License version 1.0, available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.wizards;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.plugin.idea.context.UIContextImpl;
import org.jboss.forge.plugin.idea.context.UISelectionImpl;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.wizard.WizardModel;
import com.intellij.ui.wizard.WizardNavigationState;

/**
 * Represents the model of a wizard
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeWizardModel extends WizardModel {
	private UICommand originalCommand;
	private UIContextImpl context;
	private List<ForgeWizardStep> steps;

	@SuppressWarnings("unchecked")
	public ForgeWizardModel(String title, UICommand command, VirtualFile[] files) {
		super(title);
		context = new UIContextImpl(getSelection(files));
		this.originalCommand = command;
		try {
			Field field = WizardModel.class.getDeclaredField("mySteps");
			field.setAccessible(true);
			steps = (List<ForgeWizardStep>) field.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		addWizardSteps();
		resetNavigationState();
	}

	private void resetNavigationState() {
		WizardNavigationState navState = getCurrentNavigationState();
		boolean isWizard = originalCommand instanceof UIWizard;
		navState.setEnabledToAll(isWizard);
		navState.PREVIOUS.setEnabled(false);
	}

	private void addWizardSteps() {
		add(new ForgeWizardStep(originalCommand, context));

		// TODO: Add more steps
	}

	public List<ForgeWizardStep> getSteps() {
		return steps;
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
