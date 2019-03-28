/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.launcher.launcher.dialog;

import com.launcher.launcher.swing.ActionListeners;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class AboutDialog extends JDialog {

    public AboutDialog(Window parent) {
        super(parent, "About", ModalityType.DOCUMENT_MODAL);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initComponents();
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new MigLayout("insets dialog"));

        container.add(new JLabel("<html><center><img width=80 height=80 src=https://www.iocraft.org/wp-content/uploads/2019/03/256x256.png>"), "align center, wrap");
        container.add(new JLabel("<html>Licensed under GNU General Public License, version 3.<br><br>"), "align center, wrap");
        container.add(new JLabel("<html>You are using IO-Launcher, an open-source customizable<br>"), "align center, wrap");
        container.add(new JLabel("<html>launcher for Minecraft that anyone can use.<br><br>"), "align center, wrap");
        container.add(new JLabel("<html>This is the offical launcher for our game and server.<br>"), "align center, wrap");
        container.add(new JLabel("<html>You are free to use this software anyway you please, without limitations.<br><br>"), "align center, wrap");
        
        JButton sourceCodeButton = new JButton("Website");      
		container.add(sourceCodeButton, "span, split 3, sizegroup bttn");
        
		JButton okButton = new JButton("OK");
        container.add(okButton, "tag ok, sizegroup bttn");

        add(container, BorderLayout.CENTER);

        getRootPane().setDefaultButton(okButton);
        getRootPane().registerKeyboardAction(ActionListeners.dispose(this), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        okButton.addActionListener(ActionListeners.dispose(this));
        sourceCodeButton.addActionListener(ActionListeners.openURL(this, "https://www.iocraft.org"));

    }

    public static void showAboutDialog(Window parent) {
        AboutDialog dialog = new AboutDialog(parent);
        dialog.setVisible(true);
    }
}

