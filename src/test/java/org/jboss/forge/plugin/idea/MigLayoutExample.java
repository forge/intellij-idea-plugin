/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class MigLayoutExample
{

    public static void main(String[] args) throws Exception
    {
        MigLayout layout = new MigLayout("fillx,wrap 2", "[left]rel[grow,fill]");
        JPanel panel = new JPanel(layout);
        panel.add(new JLabel("Label One:"));
        panel.add(new JTextField());
        panel.add(new JLabel("Label Two:"));
        panel.add(new JTextField());
        panel.add(new JLabel("Label Three:"), "span 2");
        panel.add(new JTextField(), "span 2,grow");
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(new BorderLayout());
        jf.add(panel, BorderLayout.CENTER);
        jf.setSize(300, 300);
        jf.setVisible(true);
    }
}
