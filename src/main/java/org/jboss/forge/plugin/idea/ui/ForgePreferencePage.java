/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import net.miginfocom.swing.MigLayout;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.plugin.idea.service.ForgeService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * The Forge preferences Page
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgePreferencePage implements Configurable
{
    private TextFieldWithBrowseButton addonsDirField;
    private JBCheckBox cacheCommandCheckBox;

    @Override
    @Nullable
    public JComponent createComponent()
    {
        addonsDirField = new TextFieldWithBrowseButton();
        addonsDirField.addBrowseFolderListener(
                "Select your preferred addon location",
                "Specifies the directory that addons will be deployed", null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        cacheCommandCheckBox = new JBCheckBox("Cache command list (sometimes new commands might not show up)");

        JPanel panel = new JPanel(new MigLayout("fillx,wrap 2",
                "[left]rel[grow,fill]"));
        panel.setOpaque(false);

        panel.add(new JLabel("Addons Installation Location:"));
        panel.add(addonsDirField);

        panel.add(cacheCommandCheckBox, "span 2");

        JPanel result = new JPanel(new BorderLayout());
        result.add(panel, BorderLayout.NORTH);

        reset();

        return result;
    }

    @Override
    public boolean isModified()
    {
        return !addonsDirField.getText().equals(getForgeState().getAddonDir()) ||
                !(cacheCommandCheckBox.isSelected() == getForgeState().isCacheCommands());
    }

    @Override
    public void apply() throws ConfigurationException
    {
        String addonDir = addonsDirField.getText();

        if (!Strings.isNullOrEmpty(addonDir))
        {
            getForgeState().setAddonDir(addonDir);
        }

        getForgeState().setCacheCommands(cacheCommandCheckBox.isSelected());
    }

    @Override
    public void reset()
    {
        addonsDirField.setText(getForgeState().getAddonDir());
        cacheCommandCheckBox.setSelected(getForgeState().isCacheCommands());
    }

    @Override
    public void disposeUIResources()
    {
        addonsDirField.dispose();
    }

    @Override
    @Nls
    public String getDisplayName()
    {
        return "JBoss Forge";
    }

    @Override
    @Nullable
    @NonNls
    public String getHelpTopic()
    {
        return null;
    }

    private ForgeService.State getForgeState()
    {
        return ForgeService.getInstance().getState();
    }
}
