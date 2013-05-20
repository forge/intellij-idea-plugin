/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.forge.plugin.idea.ForgeService;
import org.jboss.forge.plugin.idea.wizards.ForgeWizardDialog;
import org.jboss.forge.plugin.idea.wizards.ForgeWizardModel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupAdapter;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;

/**
 * Creates a popup list and displays all the currently registered
 * {@link UICommand} instances
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ShowForgeMenuAction extends AnAction {
	volatile boolean active;

	@Override
	public void actionPerformed(final AnActionEvent e) {
		if (active)
			return;
		active = true;
		final VirtualFile[] selectedFiles = e
				.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);

		final JBList list = new JBList();
		DefaultListModel model = new DefaultListModel();

		final Project project = e.getData(DataKeys.PROJECT);

		final List<UICommand> allCandidates = getAllCandidates();
		model.setSize(allCandidates.size());

		list.setCellRenderer(new ListCellRendererWrapper<UICommand>() {
			@Override
			public void customize(JList list, UICommand data, int index,
					boolean selected, boolean hasFocus) {
				if (data != null) {
					setIcon(AllIcons.Nodes.Plugin);
					setText(data.getMetadata().getName());

					// if (hasFocus)
					// {
					// HintManager.getInstance().showInformationHint(editor,
					// data.getMetadata().getDescription());
					// }
				}
			}
		});

		for (int i = 0; i < allCandidates.size(); i++) {
			model.set(i, allCandidates.get(i));
		}

		list.setModel(model);

		final PopupChooserBuilder listPopupBuilder = JBPopupFactory
				.getInstance().createListPopupBuilder(list);
		listPopupBuilder.setTitle("Run a Forge command");
		listPopupBuilder.addListener(new JBPopupAdapter() {
			@Override
			public void onClosed(LightweightWindowEvent event) {
				ShowForgeMenuAction.this.active = false;
			}
		});
		listPopupBuilder.setItemChoosenCallback(new Runnable() {
			@Override
			public void run() {
				int selectedIndex = list.getSelectedIndex();
				UICommand selectedCommand = allCandidates.get(selectedIndex);
				openWizard(selectedCommand, selectedFiles);
			}
		}).createPopup().showCenteredInCurrentWindow(project);
	}

	private void openWizard(UICommand command, VirtualFile[] files) {
		ForgeWizardModel model = new ForgeWizardModel(command.getMetadata()
				.getName(), command, files);
		ForgeWizardDialog dialog = new ForgeWizardDialog(model);
		dialog.show();
	}

	private List<UICommand> getAllCandidates() {
		List<UICommand> result = new ArrayList<UICommand>();
		AddonRegistry addonRegistry = ForgeService.INSTANCE.getAddonRegistry();
		Set<ExportedInstance<UICommand>> exportedInstances = addonRegistry
				.getExportedInstances(UICommand.class);
		for (ExportedInstance<UICommand> instance : exportedInstances) {
			UICommand uiCommand = instance.get();
			if (!(uiCommand instanceof UIWizardStep)) {
				result.add(uiCommand);
			}
		}
		return result;
	}

}
