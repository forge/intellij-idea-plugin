/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.components;

import java.awt.Container;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.plugin.idea.wizards.ForgeWizardStep;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.util.InputComponents;

/**
 * Creates a Checkbox
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class CheckboxComponentBuilder extends ComponentBuilder {

	@Override
	public JComponent build(ForgeWizardStep step,
			final InputComponent<?, Object> input, Container container) {
		// Create the label
		String text = (input.getLabel() == null ? input.getName() : input
				.getLabel());
		final JCheckBox checkbox = new JCheckBox(text);
		// Set Default Value
		final ConverterFactory converterFactory = ForgeService.INSTANCE
				.lookup(ConverterFactory.class);
		if (converterFactory != null) {
			Converter<Object, Boolean> converter = converterFactory
					.getConverter(input.getValueType(), Boolean.class);
			Boolean value = converter.convert(InputComponents
					.getValueFor(input));
			checkbox.setSelected(value == null ? false : value);
		}

		checkbox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				InputComponents.setValueFor(converterFactory, input,
						checkbox.isSelected());
			}
		});
		container.add(checkbox, "span 2");
		return checkbox;
	}

	@Override
	protected Class<Boolean> getProducedType() {
		return Boolean.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputTypes.CHECKBOX;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}

}
