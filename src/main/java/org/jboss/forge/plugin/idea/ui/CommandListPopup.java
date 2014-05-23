package org.jboss.forge.plugin.idea.ui;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupAdapter;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;
import org.jboss.forge.addon.ui.command.UICommand;

import javax.swing.*;
import java.util.List;

/**
 * Lists all UI commands.
 *
 * @author Adam Wyłuda
 */
public class CommandListPopup
{
    // TODO Design and implement CommandListPopup

    volatile boolean active;

    public void show()
    {
        if (active)
            return;
        active = true;
        final VirtualFile[] selectedFiles = null;
//        final VirtualFile[] selectedFiles = e
//                .getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);

        final JBList list = new JBList();
        DefaultListModel model = new DefaultListModel();

        final Project project = null;
//        final Project project = e.getData(DataKeys.PROJECT);

        final List<UICommand> allCandidates = getAllCandidates();
        model.setSize(allCandidates.size());

        list.setCellRenderer(new ListCellRendererWrapper<UICommand>()
        {
            @Override
            public void customize(JList list, UICommand data, int index,
                                  boolean selected, boolean hasFocus)
            {
                if (data != null)
                {
                    setIcon(AllIcons.Nodes.Plugin);

                    // TODO Pass UIContext to getMetadata()
//                    setText(data.getMetadata().getName());

                    // if (hasFocus)
                    // {
                    // HintManager.getInstance().showInformationHint(editor,
                    // data.getMetadata().getDescription());
                    // }
                }
            }
        });

        for (int i = 0; i < allCandidates.size(); i++)
        {
            model.set(i, allCandidates.get(i));
        }

        list.setModel(model);

        final PopupChooserBuilder listPopupBuilder = JBPopupFactory
                .getInstance().createListPopupBuilder(list);
        listPopupBuilder.setTitle("Run a Forge command");
        listPopupBuilder.addListener(new JBPopupAdapter()
        {
            @Override
            public void onClosed(LightweightWindowEvent event)
            {
                CommandListPopup.this.active = false;
            }
        });
        listPopupBuilder.setItemChoosenCallback(new Runnable()
        {
            @Override
            public void run()
            {
                int selectedIndex = list.getSelectedIndex();
                UICommand selectedCommand = allCandidates.get(selectedIndex);
                openWizard(selectedCommand, selectedFiles);
            }
        }).createPopup().showCenteredInCurrentWindow(project);
    }

    private List<UICommand> getAllCandidates()
    {
        // TODO Implement getAllCandidates()
        return Lists.newArrayList();
    }

    private void openWizard(UICommand command, VirtualFile[] files)
    {
        // TODO Use CommandController to obtain UICommand metadata
//        ForgeWizardModel model = new ForgeWizardModel(command.getMetadata().getName(), command, files);
//        ForgeWizardDialog dialog = new ForgeWizardDialog(model);
//        dialog.show();
    }
}
