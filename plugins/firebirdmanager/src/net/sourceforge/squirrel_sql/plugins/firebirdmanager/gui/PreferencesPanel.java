/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
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
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.FirebirdManagerHelper;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.FirebirdManagerPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.PreferencesManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PreferencesPanel extends JPanel 
implements ActionListener {                              
	private static final long serialVersionUID = -1866418931878192416L;

	// Logger for this class
    //private final static ILogger log = LoggerController.createLogger(PreferencesPanel.class);    
	private FirebirdManagerPreferenceBean firebirdManagagerPreferences = null;
    // Internationalized strings for this class.
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(PreferencesPanel.class);
    private String title = null;

    
    // gui
    private JLabel lblDatabaseFolder = new JLabel();
    private JLabel lblPort = new JLabel();
    private JLabel lblUsername = new JLabel();
    private JTextField jtextfieldDatabaseFolder = new JTextField();
    private JButton jbuttonDatabaseFolder = new JButton();
    private JTextField jtextfieldPort = new JTextField();
    private JTextField jtextfieldUsername = new JTextField();
    //private JLabel lblTitle = new JLabel();
    private JLabel lblServer = new JLabel();
    private JTextField jtextfieldServer = new JTextField();
    private JLabel lblPropertiesFolder = new JLabel();
    private JButton jbuttonPropertiesFolder = new JButton();
    private JTextField jtextfieldPropertiesFolder = new JTextField();

    
    
    private interface i18n {
		// Labels
    	String PREFERENCES_TITLE = s_stringMgr.getString("global.preferences.title");
		String PREFERENCES_LBL_DATABASE_FOLDER = s_stringMgr.getString("global.preferences.label.database.folder");
		String PREFERENCES_TOOLTIP_DATABASE_FOLDER = s_stringMgr.getString("global.preferences.tooltip.database.folder");
		String PREFERENCES_LBL_PORT = s_stringMgr.getString("global.preferences.label.port");
		String PREFERENCES_LBL_USERNAME = s_stringMgr.getString("global.preferences.label.username");
		String PREFERENCES_LBL_SERVER = s_stringMgr.getString("global.preferences.label.server");
		String PREFERENCES_LBL_PROPERTIES_FOLDER = s_stringMgr.getString("global.preferences.label.properties.folder");
    }
    
    public PreferencesPanel(FirebirdManagerPreferenceBean prefs) {
        super();
        firebirdManagagerPreferences = prefs;
        initLayout();
        readData();
    }
    
    private void initLayout() {
        this.setLayout(new BorderLayout());
        title = i18n.PREFERENCES_TITLE;
        add(createPanel(), BorderLayout.CENTER);
        
        initLabels();
        initComponents();
    }
    
    
    private void initLabels() {
        lblDatabaseFolder.setText(i18n.PREFERENCES_LBL_DATABASE_FOLDER);
        jbuttonDatabaseFolder.setToolTipText(i18n.PREFERENCES_TOOLTIP_DATABASE_FOLDER);
        lblPort.setText(i18n.PREFERENCES_LBL_PORT);
        lblServer.setText(i18n.PREFERENCES_LBL_SERVER);
        lblUsername.setText(i18n.PREFERENCES_LBL_USERNAME);
        lblPropertiesFolder.setText(i18n.PREFERENCES_LBL_PROPERTIES_FOLDER);
    }
    
    private void initComponents() {
    	jbuttonDatabaseFolder.addActionListener(this);
    	Icon iconFileOpen = FirebirdManagerHelper.loadIcon("fileopen16x16.png");
    	jbuttonDatabaseFolder.setIcon(iconFileOpen);
    	jbuttonDatabaseFolder.setText("");
    	jbuttonPropertiesFolder.setIcon(iconFileOpen);
    	jbuttonPropertiesFolder.setText("");
    	jbuttonPropertiesFolder.addActionListener(this);
    }
    
    public JPanel createPanel()
    {
       JPanel jpanel1 = new JPanel();
       jpanel1.setBorder(BorderFactory.createTitledBorder(" " + title + " "));
       FormLayout formlayout1 = new FormLayout("FILL:5DLU:NONE,FILL:DEFAULT:NONE,FILL:5DLU:NONE,FILL:30DLU:NONE,FILL:5DLU:NONE,FILL:80DLU:NONE,FILL:5DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:5DLU:NONE,FILL:DEFAULT:NONE,FILL:5DLU:NONE","CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE");
       CellConstraints cc = new CellConstraints();
       jpanel1.setLayout(formlayout1);

//       lblTitle.setBackground(new Color(204,204,204));
//       lblTitle.setName("lblTitle");
//       lblTitle.setOpaque(true);
//       lblTitle.setText("Default values");
//       EtchedBorder etchedborder1 = new EtchedBorder(EtchedBorder.RAISED,null,null);
//       lblTitle.setBorder(etchedborder1);
//       jpanel1.add(lblTitle,cc.xywh(2,2,9,1));

       lblUsername.setName("lblUsername");
       lblUsername.setText("username");
       jpanel1.add(lblUsername,cc.xy(2,12));

       jtextfieldUsername.setName("jtextfieldUsername");
       jpanel1.add(jtextfieldUsername,cc.xywh(4,12,3,1));

       lblServer.setName("lblServer");
       lblServer.setText("server");
       jpanel1.add(lblServer,cc.xy(2,4));

       jtextfieldServer.setName("jtextfieldServer");
       jpanel1.add(jtextfieldServer,cc.xywh(4,4,5,1));

       lblDatabaseFolder.setName("lblDatabaseFolder");
       lblDatabaseFolder.setText("database folder");
       jpanel1.add(lblDatabaseFolder,cc.xy(2,8));

       jtextfieldDatabaseFolder.setName("jtextfieldDatabaseFolder");
       jpanel1.add(jtextfieldDatabaseFolder,cc.xywh(4,8,5,1));

       lblPropertiesFolder.setName("lblPropertiesFolder");
       lblPropertiesFolder.setText("properties folder");
       jpanel1.add(lblPropertiesFolder,cc.xy(2,10));

       lblPort.setName("lblPort");
       lblPort.setText("port");
       jpanel1.add(lblPort,cc.xy(2,6));

       jtextfieldPort.setName("jtextfieldPort");
       jpanel1.add(jtextfieldPort,cc.xy(4,6));

       jbuttonDatabaseFolder.setActionCommand("JButton");
       jbuttonDatabaseFolder.setName("jbuttonDatabaseFolder");
       jbuttonDatabaseFolder.setText("JButton");
       jpanel1.add(jbuttonDatabaseFolder,cc.xy(10,8));

       jbuttonPropertiesFolder.setActionCommand("JButton");
       jbuttonPropertiesFolder.setName("btnPropertiesFolder");
       jbuttonPropertiesFolder.setText("JButton");
       jpanel1.add(jbuttonPropertiesFolder,cc.xy(10,10));

       jtextfieldPropertiesFolder.setName("jtextfieldPropertiesFolder");
       jpanel1.add(jtextfieldPropertiesFolder,cc.xywh(4,10,5,1));

       return jpanel1;
    }
    
    
    private void readData() {
    	jtextfieldDatabaseFolder.setText(firebirdManagagerPreferences.getDatabaseFolder());
    	jtextfieldPort.setText(firebirdManagagerPreferences.getPort());
    	jtextfieldUsername.setText(firebirdManagagerPreferences.getUser());
    	jtextfieldServer.setText(firebirdManagagerPreferences.getServer());
    	jtextfieldPropertiesFolder.setText(firebirdManagagerPreferences.getPropertiesFolder());
    }
    
    private void save() {
    	firebirdManagagerPreferences.setDatabaseFolder(jtextfieldDatabaseFolder.getText());
    	firebirdManagagerPreferences.setPort(jtextfieldPort.getText());
    	firebirdManagagerPreferences.setUser(jtextfieldUsername.getText());
    	firebirdManagagerPreferences.setServer(jtextfieldServer.getText());
    	firebirdManagagerPreferences.setPropertiesFolder(jtextfieldPropertiesFolder.getText());
    	
    	PreferencesManager.savePreferences(firebirdManagagerPreferences, PreferencesManager.PREFERENCES_BEAN_GLOBAL);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
     */
    public void applyChanges() {
        save();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
     */
    public Component getPanelComponent() {
        return this;
    }

    // ----------------------------------------------------
	private String selectDirectory(String oldDir) {
		String newDir = FirebirdManagerHelper.getFileOrDir(oldDir, false);
		if (!newDir.equals(""))
			return newDir;
		else
			return oldDir;
	}
    
    
	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbuttonDatabaseFolder) {
			jtextfieldDatabaseFolder.setText(selectDirectory(jtextfieldDatabaseFolder.getText()));
		} else if (e.getSource() == jbuttonPropertiesFolder) {
			jtextfieldPropertiesFolder.setText(selectDirectory(jtextfieldPropertiesFolder.getText()));
		}
	}
}
