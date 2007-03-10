package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@user.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A simple class that can be used to show the user a dialog to indicate the 
 * progress of some task using the ProgressCallBack interface.  Since certain
 * classes in fw module interact with the database and certain operations can 
 * take quite a long time, letting the user know how it's going is nice.  
 * However, fw module classes don't (and shouldn't) know anything about the UI
 * as this is the responsibility of the app module classes.  So, this class 
 * can be passed in by app classes to certain fw long-running methods to 
 * bridge the gap and provide feedback to the user.
 *   
 * @author manningr
 */
public class ProgessCallBackDialog extends JDialog 
                                   implements ProgressCallBack {

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(ProgessCallBackDialog.class);
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ProgessCallBackDialog.class);
    
    static interface i18n {
        //i18n[ProgressCallBackDialog.defaultLoadingPrefix=Loading:]
        String DEFAULT_LOADING_PREFIX = 
            s_stringMgr.getString("ProgressCallBackDialog.defaultLoadingPrefix");
        
        //i18n[ProgressCallBackDialog.initialLoadingPrefix=Loading...]
        String INITIAL_LOADING_PREFIX = 
            s_stringMgr.getString("ProgressCallBackDialog.initialLoadingPrefix");
    }
                
    private int itemCount = 0;
    
    private JProgressBar progressBar = null;
    
    private JLabel statusLabel = null;
    
    private String _loadingPrefix = i18n.DEFAULT_LOADING_PREFIX;
    
    public ProgessCallBackDialog(Dialog owner, String title, int totalItems) {
        super(owner, title);
        setLocationRelativeTo(owner);
        init(totalItems);
    }

    public ProgessCallBackDialog(Frame owner, String title, int totalItems) {
        super(owner, title);
        setLocationRelativeTo(owner);
        init(totalItems);
    }

    private void init(int totalItems) {
        itemCount = totalItems;
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                createGUI();
            }
        });
        
    }
    
    /**
     * Sets the text that is displayed before each thing being loaded.  By 
     * default this is the string "Loading:".
     * @param loadingPrefix
     */
    public void setLoadingPrefix(String loadingPrefix) {
        if (loadingPrefix != null) {
            _loadingPrefix = loadingPrefix;
        }
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#currentlyLoading(java.lang.String)
     */
    public void currentlyLoading(final String simpleName) {
        final StringBuilder statusText = new StringBuilder();
        statusText.append(_loadingPrefix);
        statusText.append(" ");
        statusText.append(simpleName);
        try {
            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    statusLabel.setText(statusText.toString());
                    progressBar.setValue(progressBar.getValue() + 1);
                    if (progressBar.getValue() == itemCount - 1) {
                        ProgessCallBackDialog.this.setVisible(false);
                        return;
                    }                    
                }
            });
        } catch (Exception e) {
            s_log.error("Unexpected exception: "+e.getMessage(), e);
        }
    }

    private void createGUI() {
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        
        statusLabel = new JLabel(i18n.INITIAL_LOADING_PREFIX);
        dialogPanel.add(statusLabel, c);
        
        progressBar = new JProgressBar(0, itemCount);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,10,0);
        c.weightx = 1.0;

        dialogPanel.add(progressBar, c);
        super.getContentPane().add(dialogPanel);
        super.pack();
        super.setSize(new Dimension(200, 100));  
        super.setVisible(true);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ApplicationArguments.initialize(new String[] {});
        String[] tables = new String[] { 
            "table_a",
            "table_b",
            "table_c",
            "table_d",
            "table_e",
        };
        JFrame parent = new JFrame();
        GUIUtils.centerWithinScreen(parent);
        parent.setSize(new Dimension(200, 200));
        parent.setVisible(true);
        ProgessCallBackDialog dialog = 
            new ProgessCallBackDialog(parent, "test", 5);  
        
        dialog.setVisible(true);
        for (int i = 0; i < 5; i++) {
            dialog.currentlyLoading(tables[i]);
            Thread.sleep(1000);
        }
        System.exit(0);
    }

}
