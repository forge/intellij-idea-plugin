/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.plugin.idea.service.ForgeService;
import org.jboss.forge.plugin.idea.ui.listeners.ValueChangeListener;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;

/**
 * Represents Forge input component.
 *
 * @author Adam Wyłuda
 */
public abstract class ForgeComponent
{
    private static final String NOTE_CLIENT_PROPERTY_KEY = "forge.note";

    protected ValueChangeListener valueChangeListener;

    protected ConverterFactory converterFactory = ForgeService.getInstance().getConverterFactory();

    public abstract void buildUI(Container container);

    /**
     * Displays validation messages.
     */
    public void displayMessages(java.util.List<UIMessage> messages)
    {
        for (UIMessage message : messages)
        {
            if (message.getSeverity().equals(UIMessage.Severity.ERROR))
            {
                setErrorMessage(message);
                return;
            }
        }
        clearErrorMessage();
    }

    public void setErrorMessage(UIMessage message)
    {
    }

    public void clearErrorMessage()
    {
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener)
    {
        this.valueChangeListener = valueChangeListener;
    }

    public abstract void updateState();

    protected JLabel addNoteLabel(Container parent, JComponent component) {
        final JBLabel noteLabel = new JBLabel(UIUtil.ComponentStyle.SMALL);
        // Hide empty labels
        noteLabel.addPropertyChangeListener("text", (PropertyChangeListener) evt -> noteLabel.setVisible(!Strings.isNullOrEmpty(noteLabel.getText())));
        noteLabel.setAnchor(component);
        parent.add(noteLabel,"skip 1,hidemode 2");
        component.putClientProperty(NOTE_CLIENT_PROPERTY_KEY, noteLabel);
        return noteLabel;
    }

    protected void updateNote(JComponent component, String note) {
        JLabel noteLabel = (JLabel) component.getClientProperty(NOTE_CLIENT_PROPERTY_KEY);
        if (noteLabel != null) {
            noteLabel.setText(note);
        }
    }

}
