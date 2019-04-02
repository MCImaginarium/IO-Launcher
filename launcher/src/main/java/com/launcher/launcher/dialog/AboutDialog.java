package com.launcher.launcher.dialog;
import com.launcher.launcher.FancyBackgroundPanel;
import java.awt.GridLayout;
import com.launcher.launcher.swing.ActionListeners;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
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
		try {
			Image logoImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/96x96.png"));
			ImageIcon logoIcon = new ImageIcon(logoImage); 	
			JLabel logoLabel = new JLabel(null, logoIcon, SwingConstants.CENTER);
			container.add(logoLabel, "align center, wrap");
		} catch (Exception ex) {} 
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