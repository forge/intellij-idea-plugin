/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.extensions;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Strings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * The Forge preferences Page
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ForgePreferencePage implements Configurable {
	private TextFieldWithBrowseButton addonsDirField;
	private String defaultText;

	@Override
	@Nullable
	public JComponent createComponent() {
		defaultText = new File(OperatingSystemUtils.getUserForgeDir(), "addons")
				.getAbsolutePath();
		addonsDirField = new TextFieldWithBrowseButton();
		addonsDirField.setText(defaultText);
		addonsDirField.addBrowseFolderListener(
				"Select your preferred addon location",
				"Specifies the directory that addons will be deployed", null,
				FileChooserDescriptorFactory.createSingleFolderDescriptor());

		JPanel panel = new JPanel(new MigLayout("fillx,wrap 2",
				"[left]rel[grow,fill]"));
		panel.setOpaque(false);

		panel.add(new JLabel("Addons Installation Location:"));
		panel.add(addonsDirField);
		JPanel result = new JPanel(new BorderLayout());
		result.add(panel, BorderLayout.NORTH);
		return result;
	}

	@Override
	public boolean isModified() {
		return true;
		// return !defaultText.equals(addonsDirField.getText());
	}

	@Override
	public void apply() throws ConfigurationException {
		String addonDir = addonsDirField.getText();
		if (Strings.isNullOrEmpty(addonDir)) {
			addonDir = defaultText;
		}

		// TODO: Store the addonDir value somewhere
	}

	@Override
	public void reset() {
		addonsDirField.setText(defaultText);
	}

	@Override
	public void disposeUIResources() {
		addonsDirField.dispose();
	}

	@Override
	@Nls
	public String getDisplayName() {
		return "JBoss Forge";
	}

	@Override
	@Nullable
	@NonNls
	public String getHelpTopic() {
		return null;
	}
}
