/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.components;

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.plugin.idea.wizards.ForgeWizardStep;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.util.InputComponents;

public class TextBoxComponentBuilder extends ComponentBuilder {

	@Override
	public JComponent build(ForgeWizardStep step,
			final InputComponent<?, Object> input, Container container) {
		final JTextField textField = new JTextField();
		// Set Default Value
		final ConverterFactory converterFactory = ForgeService.INSTANCE
				.lookup(ConverterFactory.class);
		Converter<Object, String> converter = converterFactory.getConverter(
				input.getValueType(), String.class);
		String value = converter.convert(InputComponents.getValueFor(input));
		textField.setText(value == null ? "" : value);

		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				InputComponents.setValueFor(converterFactory, input,
						textField.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				InputComponents.setValueFor(converterFactory, input,
						textField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				InputComponents.setValueFor(converterFactory, input,
						textField.getText());
			}
		});
		String labelValue = input.getLabel() == null ? input.getName() : input
				.getLabel();
		container.add(new JLabel(labelValue));
		container.add(textField);
		return textField;
	}

	@Override
	protected Class<String> getProducedType() {
		return String.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputTypes.TEXTBOX;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}

}
