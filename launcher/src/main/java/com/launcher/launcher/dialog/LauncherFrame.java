package com.launcher.launcher.dialog;

import com.launcher.concurrency.ObservableFuture;
import com.launcher.launcher.FancyBackgroundPanel;
import com.launcher.launcher.Instance;
import com.launcher.launcher.InstanceList;
import com.launcher.launcher.FancyLauncher;
import com.launcher.launcher.launch.LaunchListener;
import com.launcher.launcher.launch.LaunchOptions;
import com.launcher.launcher.launch.LaunchOptions.UpdatePolicy;
import com.launcher.launcher.swing.*;
import com.launcher.launcher.util.SharedLocale;
import com.launcher.launcher.util.SwingExecutor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.ref.WeakReference;

import static com.launcher.launcher.util.SharedLocale.tr;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;

/**
 * The main launcher frame.
 */
@Log
public class LauncherFrame extends JFrame {

    private final FancyLauncher launcher;

    @Getter
    private final InstanceTable instancesTable = new InstanceTable();
    private final InstanceTableModel instancesModel;
    @Getter
    private final JScrollPane instanceScroll = new JScrollPane(instancesTable);
    private WebpagePanel webView;
    private JSplitPane splitPane;
    private final JButton launchButton = new JButton();
    private final JButton refreshButton = new JButton();
    private final JButton optionsButton = new JButton();
    private final JButton specsUpdateButton = new JButton();
    private final JCheckBox updateCheck = new JCheckBox(SharedLocale.tr("launcher.downloadUpdates"));
    private boolean isUpdateable = false;

    /**
     * Create a new frame.
     *
     * @param launcher the launcher
     */
    public LauncherFrame(@NonNull FancyLauncher launcher) {
        super(tr("launcher.title", launcher.getVersion()));

        this.launcher = launcher;
        instancesModel = new InstanceTableModel(launcher.getInstances());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setMinimumSize(new Dimension(400, 400));
        //setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);

        SwingHelper.setFrameIcon(this, FancyLauncher.class, "icon.png");
        
        setSize(800, 530);
        setLocationRelativeTo(null);

        //SwingHelper.removeOpaqueness(getInstancesTable());
        //SwingHelper.removeOpaqueness(getInstanceScroll());
        //getInstanceScroll().setBorder(BorderFactory.createEmptyBorder());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadInstances();
            }
        });
    }

    private void initComponents() {
        JPanel container = createContainerPanel();
        container.setLayout(new MigLayout("fill, insets dialog", "[][]push[][]", "[grow][]"));
        webView = createNewsPanel();
		//splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, webView, instanceScroll);
		//splitPane.setSize(780, 530);
		isUpdateable = launcher.getUpdateManager().getPendingUpdate();
		if (isUpdateable) {
			try {
				Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/update.png"));
				specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
			} catch (Exception ex) {}                          
		} else {
			try {
				Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/specs.png"));
				specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
			} catch (Exception ex) {} 
		}
        specsUpdateButton.setVisible(true);
        launcher.getUpdateManager().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("pendingUpdate")) {
                    isUpdateable = (boolean) evt.getNewValue();
                    if (isUpdateable) {
                        try {
                            Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/update.png"));
                            specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
                        } catch (Exception ex) {}                          
                    } else {
                        try {
                            Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/specs.png"));
                            specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
                        } catch (Exception ex) {} 
                    }
                }
            }
        });

        updateCheck.setSelected(true);
        //splitPane.setDividerLocation(9999);
        //splitPane.setDividerSize(0);
        //splitPane.setOpaque(false);
        container.add(webView, "grow, wrap, span 0, gapbottom unrel");

        instancesTable.setModel(instancesModel);
		instancesTable.setRowHeight(48);
		instancesTable.setOpaque(false);
       //container.add(webView);
	    container.add(instanceScroll, "grow, span 0, w null:110, h null:48");
       //SwingHelper.flattenJSplitPane(splitPane);
    
        //START Refresh Button
        JButton refreshButton = new JButton();
        try {
            Image refreshImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/refresh.png"));
            refreshButton.setIcon(new ImageIcon(refreshImage));
        } catch (Exception ex) {}   
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setIcon(null);
                refreshButton.setText("REFRESH");
                refreshButton.setBackground(Color.GREEN);
                refreshButton.setPreferredSize(new Dimension(72, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setText(null);
                refreshButton.setBackground(UIManager.getColor("control"));
                try {
                    Image refreshImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/refresh.png"));
                    refreshButton.setIcon(new ImageIcon(refreshImage));
                } catch (Exception ex) {}   
                refreshButton.setPreferredSize(new Dimension(72, 50));
            }
        });
        refreshButton.setPreferredSize(new Dimension(72, 50));
        container.add(refreshButton);
		refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				refreshButton.setText("UPDATE");
				refreshButton.setBackground(Color.GREEN);
                refreshButton.setPreferredSize(new Dimension(72, 50));
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				refreshButton.setBackground(UIManager.getColor("control"));
                try {
                    Image refreshImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/refresh.png"));
                    refreshButton.setIcon(new ImageIcon(refreshImage));
                } catch (Exception ex) {}   
                refreshButton.setPreferredSize(new Dimension(72, 50));
			}
		});        
        //END Refresh Button

        //START Update Check Box and Text Container
        container.add(updateCheck);
		updateCheck.setOpaque(false);
        updateCheck.setPreferredSize(new Dimension(72, 50));
        //END Update Check Box and Text Container

        //START Discord Button
        JButton discordButton = new JButton();
        try {
            Image discordImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/discord.png"));
            discordButton.setIcon(new ImageIcon(discordImage));
        } catch (Exception ex) {}   
        discordButton.addActionListener(ActionListeners.openURL(this, "https://discord.gg/UGHFX3Q"));
        discordButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                discordButton.setIcon(null);
                discordButton.setText("DISCORD");
                discordButton.setBackground(Color.GREEN);
                discordButton.setPreferredSize(new Dimension(72, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                discordButton.setText(null);
                discordButton.setBackground(UIManager.getColor("control"));
                try {
                    Image discordImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/discord.png"));
                    discordButton.setIcon(new ImageIcon(discordImage));
                } catch (Exception ex) {}   
                discordButton.setPreferredSize(new Dimension(72, 50));
            }
        });
        discordButton.setPreferredSize(new Dimension(72, 50));
        container.add(discordButton);
        //END Discord Button
        
        //START Web Button
        JButton webButton = new JButton();
        try {
            Image webImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/web.png"));
            webButton.setIcon(new ImageIcon(webImage));
        } catch (Exception ex) {}  
        webButton.addActionListener(ActionListeners.openURL(this, "https://www.iocraft.org"));
        webButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                webButton.setIcon(null);
                webButton.setText("WEBSITE");
                webButton.setBackground(Color.GREEN);
                webButton.setPreferredSize(new Dimension(72, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                webButton.setText(null);
                webButton.setBackground(UIManager.getColor("control"));
                try {
                    Image webImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/web.png"));
                    webButton.setIcon(new ImageIcon(webImage));
                } catch (Exception ex) {}  
                webButton.setPreferredSize(new Dimension(72, 50));
            }
        });
        webButton.setPreferredSize(new Dimension(72, 50));
        container.add(webButton);
        //END Web Button

        //START Log Button
        JButton logButton = new JButton();
        try {
            Image logImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/log.png"));
            logButton.setIcon(new ImageIcon(logImage));
        } catch (Exception ex) {}          
        logButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logButton.setIcon(null);
                logButton.setText("CHAT");
                logButton.setBackground(Color.GREEN);
                logButton.setPreferredSize(new Dimension(72, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logButton.setText(null);
                logButton.setBackground(UIManager.getColor("control"));
                try {
                    Image logImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/log.png"));
                    logButton.setIcon(new ImageIcon(logImage));
                } catch (Exception ex) {}  
                logButton.setPreferredSize(new Dimension(72, 50));
            }
        });
        logButton.setPreferredSize(new Dimension(72, 50));
        container.add(logButton);
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConsoleFrame.showMessages();
            }
        });  
        logButton.addMouseListener(new PopupMouseAdapter() {
            @Override
            protected void showPopup(MouseEvent e) {
                int index = instancesTable.rowAtPoint(e.getPoint());
                Instance selected = null;
                if (index >= 0) {
                    instancesTable.setRowSelectionInterval(index, index);
                    selected = launcher.getInstances().get(index);
                }
                popupInstanceMenu(e.getComponent(), e.getX(), e.getY(), selected);
            }
        });
        logButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logButton.setText("LOG");
                logButton.setBackground(Color.GREEN);
                logButton.setPreferredSize(new Dimension(50, 39));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logButton.setBackground(UIManager.getColor("control"));
                try {
                    Image logImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/log.png"));
                    logButton.setIcon(new ImageIcon(logImage));
                } catch (Exception ex) {} 
                logButton.setPreferredSize(new Dimension(50, 30));
            }
        });
        logButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logButton.setIcon(null);
                logButton.setText("LOG");
                logButton.setBackground(Color.GREEN);
                logButton.setPreferredSize(new Dimension(72, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logButton.setText(null);
                logButton.setBackground(UIManager.getColor("control"));
                try {
                    Image logImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/log.png"));
                    logButton.setIcon(new ImageIcon(logImage));
                } catch (Exception ex) {}  
                logButton.setPreferredSize(new Dimension(72, 50));
            }
        });        
        //END Log Button

        //START SpecUpdate Button
        JButton specsUpdateButton = new JButton();
        if (isUpdateable) {
            try {
                Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/update.png"));
                specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
            } catch (Exception ex) {}                          
        } else {
            try {
                Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/specs.png"));
                specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
            } catch (Exception ex) {} 
        }
        specsUpdateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                specsUpdateButton.setIcon(null);
                specsUpdateButton.setText(isUpdateable ? "UPDATE" : "SPECS");
                specsUpdateButton.setBackground(Color.GREEN);
                specsUpdateButton.setPreferredSize(new Dimension(72, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                specsUpdateButton.setText(null);
                specsUpdateButton.setBackground(UIManager.getColor("control"));
                if (isUpdateable) {
                    try {
                        Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/update.png"));
                        specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
                    } catch (Exception ex) {}                          
                } else {
                    try {
                        Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/specs.png"));
                        specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
                    } catch (Exception ex) {} 
                }
                specsUpdateButton.setPreferredSize(new Dimension(72, 50));
            }
        });
        specsUpdateButton.setPreferredSize(new Dimension(72, 50));
        container.add(specsUpdateButton);
        specsUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isUpdateable) {
                    launcher.getUpdateManager().performUpdate(LauncherFrame.this);
                } else {
                    showSpecs();
                }
            }
        });
        specsUpdateButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				specsUpdateButton.setText(isUpdateable ? "UPDATE" : "SPECS");
				specsUpdateButton.setBackground(Color.GREEN);
				specsUpdateButton.setPreferredSize(new Dimension(72, 50));
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				specsUpdateButton.setBackground(UIManager.getColor("control"));
                    if (isUpdateable) {
                        try {
                            Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/update.png"));
                            specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
                        } catch (Exception ex) {}                          
                    } else {
                        try {
                            Image specsUpdateImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/specs.png"));
                            specsUpdateButton.setIcon(new ImageIcon(specsUpdateImage));
                        } catch (Exception ex) {} 
                    }
				specsUpdateButton.setPreferredSize(new Dimension(72, 50));
			}
		});
        //END SpecUpdate Button

        //START Options Button
        JButton optionsButton = new JButton();
        try {
            Image optionsImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/options.png"));
            optionsButton.setIcon(new ImageIcon(optionsImage));
        } catch (Exception ex) {}   
        optionsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                optionsButton.setIcon(null);
                optionsButton.setText("CHAT");
                optionsButton.setBackground(Color.GREEN);
                optionsButton.setPreferredSize(new Dimension(72, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                optionsButton.setText(null);
                optionsButton.setBackground(UIManager.getColor("control"));
                try {
                    Image optionsImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/options.png"));
                    optionsButton.setIcon(new ImageIcon(optionsImage));
                } catch (Exception ex) {}   
                optionsButton.setPreferredSize(new Dimension(72, 50));
            }
        });
        optionsButton.setPreferredSize(new Dimension(72, 50));
        container.add(optionsButton);
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOptions();
            }
        });    
		optionsButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				optionsButton.setText("OPTIONS");
				optionsButton.setBackground(Color.GREEN);
                optionsButton.setPreferredSize(new Dimension(72, 50));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				optionsButton.setBackground(UIManager.getColor("control"));
                try {
                    Image optionsImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/options.png"));
                    optionsButton.setIcon(new ImageIcon(optionsImage));
                } catch (Exception ex) {}   
                optionsButton.setPreferredSize(new Dimension(72, 50));
			}
		});
        //END Options Button
       
        //START Launch Button
        JButton launchButton = new JButton();
        //try {
            //Image launchImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/launch.png"));
            //launchButton.setIcon(new ImageIcon(launchImage));
        //} catch (Exception ex) {}   
        launchButton.setText("PLAY");
        launchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                launchButton.setIcon(null);
                launchButton.setText("PLAY");
                launchButton.setBackground(Color.GREEN);
                launchButton.setPreferredSize(new Dimension(72, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                launchButton.setText(null);
                launchButton.setText("PLAY");
                launchButton.setBackground(UIManager.getColor("control"));
                //try {
                    //Image launchImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/launch.png"));
                    //launchButton.setIcon(new ImageIcon(launchImage));
                //} catch (Exception ex) {}   
                launchButton.setPreferredSize(new Dimension(72, 50));
            }
        });
        launchButton.setPreferredSize(new Dimension(72, 50));
        container.add(launchButton);
        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launch();
            }
        });
		launchButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				launchButton.setText("PLAY");
				launchButton.setBackground(Color.GREEN);
                refreshButton.setPreferredSize(new Dimension(72, 50));
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				launchButton.setBackground(UIManager.getColor("control"));
                //try {
                    //Image launchImage = ImageIO.read(FancyBackgroundPanel.class.getResourceAsStream("buttons/launch.png"));
                    //launchButton.setIcon(new ImageIcon(launchImage));
                //} catch (Exception ex) {}
                launchButton.setText("PLAY");				
                refreshButton.setPreferredSize(new Dimension(72, 50));
			}
        });
        //END Launch Button

        add(container, BorderLayout.CENTER);
        instancesModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (instancesTable.getRowCount() > 0) {
                    instancesTable.setRowSelectionInterval(0, 0);
                }
            }
        });
        instancesTable.addMouseListener(new DoubleClickToButtonAdapter(launchButton));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadInstances();
                launcher.getUpdateManager().checkForUpdate();
                webView.browse(launcher.getNewsURL(), false);
            }
        });
        instancesTable.addMouseListener(new PopupMouseAdapter() {
            @Override
            protected void showPopup(MouseEvent e) {
                int index = instancesTable.rowAtPoint(e.getPoint());
                Instance selected = null;
                if (index >= 0) {
                    instancesTable.setRowSelectionInterval(index, index);
                    selected = launcher.getInstances().get(index);
                }
                popupInstanceMenu(e.getComponent(), -1, -205, selected);
            }
        });
		//magic :) love u fanbus
		instancesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (arg0.getButton() == MouseEvent.BUTTON1){
					Instance selected = null;
					selected = launcher.getInstances().get(0);
					popupInstanceMenu(instancesTable, -1, -205, selected);
                    System.out.println("Left button clicked, select a task or click Play!");
                } else if (arg0.getButton() == MouseEvent.BUTTON2){
                    System.out.println("Middle button clicked, select a task or click Play!");
                } else if (arg0.getButton() == MouseEvent.BUTTON3) {
                    System.out.println("Right button clicked, select a task or click Play!");
                } 
            }
        });
		
    }
    
    protected JPanel createContainerPanel() {
        return new FancyBackgroundPanel();
    }

    /**
     * Return the news panel.
     *
     * @return the news panel
     */
    protected WebpagePanel createNewsPanel() {
        WebpagePanel panel = WebpagePanel.forURL(launcher.getNewsURL(), false);
        panel.setBrowserBorder(BorderFactory.createEmptyBorder());
        return panel;
    }

    /**
     * Popup the menu for the instances.
     *
     * @param component the component
     * @param x mouse X
     * @param y mouse Y
     * @param selected the selected instance, possibly null
     */
    private void popupInstanceMenu(Component component, int x, int y, final Instance selected) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem;

        if (selected != null) {
            menuItem = new JMenuItem(!selected.isLocal() ? tr("instance.install") : tr("instance.launch"));
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    launch();
                }
            });
            popup.add(menuItem);

            if (selected.isLocal()) {
                popup.addSeparator();

                menuItem = new JMenuItem(SharedLocale.tr("instance.openFolder"));
                menuItem.addActionListener(ActionListeners.browseDir(
                        LauncherFrame.this, selected.getContentDir(), true));
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.openSaves"));
                menuItem.addActionListener(ActionListeners.browseDir(
                        LauncherFrame.this, new File(selected.getContentDir(), "saves"), true));
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.openResourcePacks"));
                menuItem.addActionListener(ActionListeners.browseDir(
                        LauncherFrame.this, new File(selected.getContentDir(), "resourcepacks"), true));
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.openScreenshots"));
                menuItem.addActionListener(ActionListeners.browseDir(
                        LauncherFrame.this, new File(selected.getContentDir(), "screenshots"), true));
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.copyAsPath"));
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        File dir = selected.getContentDir();
                        dir.mkdirs();
                        SwingHelper.setClipboard(dir.getAbsolutePath());
                    }
                });
                popup.add(menuItem);

                popup.addSeparator();

                if (!selected.isUpdatePending()) {
                    menuItem = new JMenuItem(SharedLocale.tr("instance.forceUpdate"));
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            selected.setUpdatePending(true);
                            launch();
                            instancesModel.update();
                        }
                    });
                    popup.add(menuItem);
                }

                menuItem = new JMenuItem(SharedLocale.tr("instance.hardForceUpdate"));
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        confirmHardUpdate(selected);
                    }
                });
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.deleteFiles"));
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        confirmDelete(selected);
                    }
                });
                popup.add(menuItem);
            }

            popup.addSeparator();
        }

        menuItem = new JMenuItem(SharedLocale.tr("launcher.refreshList"));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadInstances();
            }
        });
        popup.add(menuItem);

        popup.show(component, x, y);

    }

    private void confirmDelete(Instance instance) {
        if (!SwingHelper.confirmDialog(this,
                tr("instance.confirmDelete", instance.getTitle()), SharedLocale.tr("confirmTitle"))) {
            return;
        }

        ObservableFuture<Instance> future = launcher.getInstanceTasks().delete(this, instance);

        // Update the list of instances after updating
        future.addListener(new Runnable() {
            @Override
            public void run() {
                loadInstances();
            }
        }, SwingExecutor.INSTANCE);
    }

    private void confirmHardUpdate(Instance instance) {
        if (!SwingHelper.confirmDialog(this, SharedLocale.tr("instance.confirmHardUpdate"), SharedLocale.tr("confirmTitle"))) {
            return;
        }

        ObservableFuture<Instance> future = launcher.getInstanceTasks().hardUpdate(this, instance);

        // Update the list of instances after updating
        future.addListener(new Runnable() {
            @Override
            public void run() {
                launch();
                instancesModel.update();
            }
        }, SwingExecutor.INSTANCE);
    }

    private void loadInstances() {
        ObservableFuture<InstanceList> future = launcher.getInstanceTasks().reloadInstances(this);

        future.addListener(new Runnable() {
            @Override
            public void run() {
                instancesModel.update();
                if (instancesTable.getRowCount() > 0) {
                    instancesTable.setRowSelectionInterval(0, 0);
                }
                requestFocus();
            }
        }, SwingExecutor.INSTANCE);

        ProgressDialog.showProgress(this, future, SharedLocale.tr("launcher.checkingTitle"), SharedLocale.tr("launcher.checkingStatus"));
        SwingHelper.addErrorDialogCallback(this, future);
    }

    private void showOptions() {
        ConfigurationDialog configDialog = new ConfigurationDialog(this, launcher);
        configDialog.setVisible(true);
    }

    private void showSpecs() {
        SpecsDialog specsDialog = new SpecsDialog(this);
        specsDialog.setVisible(true);
    }

    private void launch() { // NOTICE: This enforces 64-bit Java!!!
        String version = System.getProperty("sun.arch.data.model");
        if(!version.contains("64")) {
            SwingHelper.showErrorDialog(null, "Uh oh! You need 64-Bit Java 8 minimum!", "IO-Launcher");
            try {
                Desktop.getDesktop().browse(new URI("https://java.com/en/download/"));
            } catch (IOException | URISyntaxException e) {
            }
            return;
        }
        
        boolean permitUpdate = updateCheck.isSelected();
        Instance instance = launcher.getInstances().get(instancesTable.getSelectedRow());

        LaunchOptions options = new LaunchOptions.Builder()
                .setInstance(instance)
                .setListener(new LaunchListenerImpl(this))
                .setUpdatePolicy(permitUpdate ? UpdatePolicy.UPDATE_IF_SESSION_ONLINE : UpdatePolicy.NO_UPDATE)
                .setWindow(this)
                .build();
        launcher.getLaunchSupervisor().launch(options);
    }

    private static class LaunchListenerImpl implements LaunchListener {
        private final WeakReference<LauncherFrame> frameRef;
        private final FancyLauncher launcher;

        private LaunchListenerImpl(LauncherFrame frame) {
            this.frameRef = new WeakReference<>(frame);
            this.launcher = frame.launcher;
        }

        @Override
        public void instancesUpdated() {
            LauncherFrame frame = frameRef.get();
            if (frame != null) {
                frame.instancesModel.update();
            }
        }

        @Override
        public void gameStarted() {
            LauncherFrame frame = frameRef.get();
            if (frame != null) {
                frame.dispose();
            }
        }

        @Override
        public void gameClosed() {
            launcher.showLauncherWindow();
        }
    }

}
