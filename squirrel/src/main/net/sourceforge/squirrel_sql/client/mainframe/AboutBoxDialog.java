package net.sourceforge.squirrel_sql.client.mainframe;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.HashtableDataSet;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

/**
 * About box dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AboutBoxDialog extends JDialog {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String ABOUT = "About";
    }

    public AboutBoxDialog(IApplication app, Frame owner) {
        super(owner, i18n.ABOUT, true);
        createUserInterface(app);
    }

    private void createUserInterface(IApplication app) {
        final Container contentPane = getContentPane();

        JTabbedPane tabPnl = new JTabbedPane();

        tabPnl.add("About", new AboutPanel(app));  // i18n
        tabPnl.add("System", new SystemPanel(app)); // i18n

        contentPane.setLayout(new BorderLayout());
        contentPane.add(tabPnl, BorderLayout.CENTER);

        // Ok button at bottom of dialog.
        JPanel btnsPnl = new JPanel();
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
        btnsPnl.add(okBtn);
        contentPane.add(btnsPnl, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okBtn);

        pack();
        GUIUtils.centerWithinParent(this);
        setResizable(false);
    }

    private static final class AboutPanel extends JPanel {
        AboutPanel(IApplication app) {
            super();
            setLayout(new BorderLayout());
            setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));
            Icon icon = app.getResources().getIcon(SquirrelResources.ImageNames.SPLASH_SCREEN);
            JLabel iconLbl = new JLabel(icon);
            add(BorderLayout.CENTER, new JLabel(icon));
            JTextArea ta = new JTextArea();
            ta.setEditable(false);
            ta.setOpaque(false);
            ta.append(Version.getVersion());
            ta.append("\n");
            ta.append(Version.getCopyrightStatement());
            ta.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(BorderLayout.SOUTH, ta);
        }
    }

    private static final class SystemPanel extends JPanel {
        SystemPanel(IApplication app) {
            super();
            setLayout(new BorderLayout());
            DataSetViewerTablePanel propsPnl = new DataSetViewerTablePanel();
            DataSetViewer viewer = new DataSetViewer(propsPnl);
            try {
                viewer.show(new HashtableDataSet(System.getProperties()));
            } catch (DataSetException ex) {
                Logger logger = app.getLogger();
                logger.showMessage(Logger.ILogTypes.ERROR, "Error occured displaying System Properties");
                logger.showMessage(Logger.ILogTypes.ERROR, ex);
            }

            add(new JScrollPane(propsPnl), BorderLayout.CENTER);
            add(new MemoryPanel(), BorderLayout.SOUTH);

            setPreferredSize(new Dimension(400, 400));
        }
    }

    private static class MemoryPanel extends PropertyPanel {
        private JLabel _totalMemoryLbl = new JLabel();
        private JLabel _usedMemoryLbl = new JLabel();
        private JLabel _freeMemoryLbl = new JLabel();
        private boolean _killThread = false;
        private Thread _thread = null;

        MemoryPanel() {
            super();
            add(new JLabel("Java heap size:"), _totalMemoryLbl); // i18n
            add(new JLabel("Used heap:"), _usedMemoryLbl); // i18n
            add(new JLabel("Free heap:"), _freeMemoryLbl); // i18n

            JButton gcBtn = new JButton("Garbage Collect"); //i18n
            gcBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    System.gc();
                }
            });
            add(gcBtn, new JLabel(""));

            _thread = new Thread(new MemoryTimer());
            _thread.start();
        }

        public void removeNotify() {
            if (_thread != null) {
                _killThread = true;
                try {
                    _thread.join();
                } catch (InterruptedException ignore) {
                }
                _thread = null;
                _killThread = false;
            }
            super.removeNotify();
        }

        private final class MemoryTimer implements Runnable {
            private static final long MB_VALUE = 1048576;
            private static final long KB_VALUE = 1024;

            private static final String MB = " MB";
            private static final String KB = " KB";
            private static final String BYTES = " bytes";

            private DecimalFormat _fmt = new DecimalFormat("#,##0.0#");


            public void run() {
                Thread.currentThread().setName("Memory Timer");
                for (;;) {
                    Runtime rt = Runtime.getRuntime();
                    final long totalMemory = rt.totalMemory();
                    final long freeMemory = rt.freeMemory();
                    final long usedMemory = totalMemory - freeMemory;
                    _totalMemoryLbl.setText(formatSize(totalMemory));
                    _usedMemoryLbl.setText(formatSize(usedMemory));
                    _freeMemoryLbl.setText(formatSize(freeMemory));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    if (_killThread) {
                        break;
                    }
                }
            }

            // i18n
            private String formatSize(long nbrBytes) {
                StringBuffer buf = new StringBuffer();
                double size = nbrBytes;
                double val = size / MB_VALUE;
                if (val > 1) {
                    return _fmt.format(val).concat(MB);
                }
                val = size / KB_VALUE;
                if (val > 10) {
                    return _fmt.format(val).concat(KB);
                }
                return _fmt.format(val).concat(BYTES);
            }
        }
    }
}
