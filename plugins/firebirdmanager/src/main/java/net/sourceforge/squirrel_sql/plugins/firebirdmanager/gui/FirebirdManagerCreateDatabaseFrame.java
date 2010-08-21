/*
 * Copyright (C) 2008 Michael Romankiewicz
 * microm at users.sourceforge.net
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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.FirebirdManagerHelper;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.FirebirdManagerCreateDatabasePreferenceBean;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.FirebirdManagerPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.PreferencesManager;
import org.firebirdsql.management.FBMaintenanceManager;
import org.firebirdsql.management.FBManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Internal frame for database creation
 * @author Michael Romankiewicz
 */
public class FirebirdManagerCreateDatabaseFrame extends DialogWidget
implements IFirebirdManagerFrame, ActionListener, KeyListener {
	private static final long serialVersionUID = 218636820715664639L;
	private final String CR = System.getProperty("line.separator", "\n");
	
	// variables
	// ========================================================================
	// non visible
	// ------------------------------------------------------------------------
	// Logger for this class
    private final static ILogger log = LoggerController.createLogger(FirebirdManagerCreateDatabaseFrame.class);

	private static final StringManager stringManager = StringManagerFactory
			.getStringManager(FirebirdManagerCreateDatabaseFrame.class);
	
	private FirebirdManagerPreferenceBean globalPreferencesBean;
	// session preferences
	private FirebirdManagerCreateDatabasePreferenceBean sessionPreferencesBean = null;

	// visible (gui)
	// ------------------------------------------------------------------------
    private JLabel lblDirectory = new JLabel();
    private JLabel lblFilename = new JLabel();
    private JLabel lblServer = new JLabel();
    private JLabel lblUsername = new JLabel();
    private JLabel lblPassword = new JLabel();
    private JLabel lblPort = new JLabel();
    private JTextField jtextfieldServer = new JTextField();
    private JTextField jtextfieldPort = new JTextField();
    private JTextField jtextfieldDatabaseDirectory = new JTextField();
    private JTextField jtextfieldFilename = new JTextField();
    private JButton jbuttonDatabaseDirectory = new JButton("");
    private JTextField jtextfieldUsername = new JTextField();
    private JPasswordField jpasswordfield = new JPasswordField();
    private JButton jbuttonCreate = new JButton("create");
    private JSeparator horizontallinecomponent1 = new JSeparator();
    private JLabel lblSQLDialect = new JLabel();
    private JRadioButton radioButtonDialect1 = new JRadioButton();
    private ButtonGroup buttongroup1 = new ButtonGroup();
    private JRadioButton radioButtonDialect3 = new JRadioButton();
    private JButton jbuttonLoadProp = new JButton();
    private JButton jbuttonSaveProp = new JButton();
   private IApplication _application;


   /**
     * Constructor
     * @param application
     */
	public FirebirdManagerCreateDatabaseFrame(IApplication application) {
		super("Firebird manager - " + stringManager.getString("createdatabase.title"), true, true, true, true, application);
      _application = application;

      initLayout();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.moveToFront();
	}

	/**
	 * I18n texts
	 */
	private interface i18n {
		// Labels
		String CREATEDB_LBL_DIRECTORY = stringManager.getString("createdatabase.label.directory");
		String CREATEDB_TOOLTIP_DIRECTORY = stringManager.getString("createdatabase.tooltip.directory");
		String CREATEDB_LBL_FILENAME = stringManager.getString("createdatabase.label.filename");
		String CREATEDB_LBL_SERVER = stringManager.getString("createdatabase.label.server");
		String CREATEDB_LBL_PORT = stringManager.getString("createdatabase.label.port");
		String CREATEDB_LBL_USERNAME = stringManager.getString("createdatabase.label.username");
		String CREATEDB_LBL_PASSWORD = stringManager.getString("createdatabase.label.password");
		String CREATEDB_LBL_CREATE = stringManager.getString("createdatabase.label.create");
		String CREATEDB_TOOLTIP_CREATE = stringManager.getString("createdatabase.tooltip.create");
		String GLOBAL_TOOLTIP_BTN_PROP_LOAD = stringManager.getString("global.tooltip.btn.prop.load");
		String GLOBAL_TOOLTIP_BTN_PROP_SAVE = stringManager.getString("global.tooltip.btn.prop.save");
		// Messages
		String CREATEDB_MSG_WARNING = stringManager.getString("createdatabase.message.warning");
		String CREATEDB_MSG_FILEEXISTS = stringManager.getString("createdatabase.message.fileexists");
		String CREATEDB_MSG_CANCELED = stringManager.getString("createdatabase.message.canceled");
		String CREATEDB_MSG_SUCCEDED = stringManager.getString("createdatabase.message.succeded");
		String CREATEDB_MSG_FAILED = stringManager.getString("createdatabase.message.failed");
		String LOAD_PROP_FAILED = stringManager.getString("global.error.prop.load");
		String SAVE_PROP_FAILED = stringManager.getString("global.error.prop.save");
		String CREATEDB_INFO_PROPFILE = stringManager.getString("createdatabase.info.properties.file");
		String GLOBAL_WARNING_FILE_EXISTS = stringManager.getString("global.warning.file.exists");
		String GLOBAL_TITLE_WARNING = stringManager.getString("global.title.warning");
		// Errors
		String CREATEDB_ERROR_DIRECTORY_MISSING = stringManager.getString("createdatabase.error.directory.missing");
		String CREATEDB_ERROR_DIRECTORY_DOESNOTEXISTS = stringManager.getString("createdatabase.error.directory.doesnotexists");
		String CREATEDB_ERROR_FILENAME_MISSING = stringManager.getString("createdatabase.error.filename.missing");
		String CREATEDB_ERROR_SERVER_MISSING = stringManager.getString("createdatabase.error.server.missing");
		String CREATEDB_ERROR_PORT_MISSING = stringManager.getString("createdatabase.error.port.missing");
		String CREATEDB_ERROR_PORT_NOINTEGER = stringManager.getString("createdatabase.error.port.nointeger");
		String CREATEDB_ERROR_USERNAME_MISSING = stringManager.getString("createdatabase.error.username.missing");
	}

	private void initLayout() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(createPanelCreateDB());
		
		initVisualObjects();
		readPreferences();
	}
	
	private JPanel createPanelCreateDB() {
		JPanel jpanelCreateDB = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:5DLU:NONE,RIGHT:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE,FILL:15DLU:NONE,FILL:3DLU:NONE,FILL:10DLU:NONE,FILL:3DLU:NONE,FILL:15DLU:NONE,FILL:3DLU:NONE,FILL:15DLU:NONE,FILL:5DLU:NONE",
				"CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:10DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE");
		CellConstraints cc = new CellConstraints();
		jpanelCreateDB.setLayout(formlayout1);

		lblDirectory.setName("lblDirectory");
		lblDirectory.setText("directory");
		jpanelCreateDB.add(lblDirectory, cc.xy(2, 2));

		lblFilename.setName("lblFilename");
		lblFilename.setText("filename");
		jpanelCreateDB.add(lblFilename, cc.xy(2, 4));

		lblServer.setName("lblServer");
		lblServer.setText("server");
		jpanelCreateDB.add(lblServer, cc.xy(2, 6));

		lblUsername.setName("lblUsername");
		lblUsername.setText("username");
		jpanelCreateDB.add(lblUsername, cc.xy(2, 8));

		lblPassword.setName("lblPassword");
		lblPassword.setText("password");
		jpanelCreateDB.add(lblPassword, cc.xy(2, 10));

		jtextfieldDatabaseDirectory.setName("jtextfieldDatabaseDirectory");
		jpanelCreateDB.add(jtextfieldDatabaseDirectory,cc.xy(4,2));

		jtextfieldFilename.setName("jtextfieldFilename");
		jpanelCreateDB.add(jtextfieldFilename, cc.xywh(4, 4, 9, 1));

		jtextfieldServer.setName("jtextfieldServer");
		jpanelCreateDB.add(jtextfieldServer, cc.xy(4, 6));

		jtextfieldUsername.setName("jtextfieldUsername");
		jpanelCreateDB.add(jtextfieldUsername, cc.xywh(4, 8, 9, 1));

		jpasswordfield.setName("jpasswordfield");
		jpanelCreateDB.add(jpasswordfield, cc.xywh(4, 10, 9, 1));

		lblPort.setName("lblPort");
		lblPort.setText("port");
		jpanelCreateDB.add(lblPort, new CellConstraints(6, 6, 3, 1,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));

		jbuttonDatabaseDirectory.setActionCommand("JButton");
		jbuttonDatabaseDirectory.setName("jbuttonDatabaseDirectory");
		jbuttonDatabaseDirectory.setText("JButton");
		jpanelCreateDB.add(jbuttonDatabaseDirectory, cc.xy(6, 2));

		jtextfieldPort.setName("jtextfieldPort");
		jtextfieldPort.setSelectionEnd(4);
		jtextfieldPort.setSelectionStart(4);
		jtextfieldPort.setText("3050");
		jpanelCreateDB.add(jtextfieldPort, cc.xywh(10, 6, 3, 1));

		jbuttonCreate.setActionCommand("Create");
		jbuttonCreate.setName("jbuttonCreate");
		jbuttonCreate.setText("Create");
		jpanelCreateDB.add(jbuttonCreate, cc.xywh(6, 14, 7, 1));
		
		jpanelCreateDB.add(horizontallinecomponent1, cc.xywh(2, 13, 11, 1));

		jpanelCreateDB.add(createPanelSQLDialect(), cc.xy(4, 12));

		jbuttonLoadProp.setName("jbuttonLoadProp");
		jbuttonLoadProp.setText("...");
		jpanelCreateDB.add(jbuttonLoadProp, cc.xy(10, 2));

		jbuttonSaveProp.setActionCommand("...");
		jbuttonSaveProp.setText("...");
		jpanelCreateDB.add(jbuttonSaveProp, cc.xy(12, 2));

		return jpanelCreateDB;
	}

	public JPanel createPanelSQLDialect() {
		JPanel jpanelSQLDialect = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanelSQLDialect.setLayout(formlayout1);

		lblSQLDialect.setName("lblSQLDialect");
		lblSQLDialect.setText("sql dialect");
		jpanelSQLDialect.add(lblSQLDialect, cc.xy(1, 1));

		radioButtonDialect1.setActionCommand("1");
		radioButtonDialect1.setName("radioButtonDialect1");
		radioButtonDialect1.setText("1");
		buttongroup1.add(radioButtonDialect1);
		jpanelSQLDialect.add(radioButtonDialect1, cc.xy(3, 1));

		radioButtonDialect3.setActionCommand("3");
		radioButtonDialect3.setName("radioButtonDialect3");
		radioButtonDialect3.setText("3");
		buttongroup1.add(radioButtonDialect3);
		jpanelSQLDialect.add(radioButtonDialect3, cc.xy(5, 1));

		return jpanelSQLDialect;
	}
	
	public void setFocusToFirstEmptyInputField() {
		if (jtextfieldDatabaseDirectory.getText().length() == 0) {
			jtextfieldDatabaseDirectory.requestFocusInWindow();
		} else {
			jtextfieldFilename.requestFocusInWindow();
		}
	}

	
	/**
	 * Configure the visual components with texts, icons and listeners
	 */
	private void initVisualObjects() {
		Dimension dim = new Dimension(300, 20);

		lblDirectory.setText(i18n.CREATEDB_LBL_DIRECTORY);
		jtextfieldDatabaseDirectory.addKeyListener(this);
		jtextfieldDatabaseDirectory.setToolTipText(i18n.CREATEDB_TOOLTIP_DIRECTORY);
		jbuttonDatabaseDirectory.setText("");
		jbuttonDatabaseDirectory.setIcon(FirebirdManagerHelper.loadIcon("fileopen16x16.png"));
        jbuttonDatabaseDirectory.setEnabled(jtextfieldServer.getText().toUpperCase().trim().equals("LOCALHOST"));
		jbuttonDatabaseDirectory.setToolTipText(jtextfieldDatabaseDirectory.getToolTipText());
		jbuttonDatabaseDirectory.addActionListener(this);
		jbuttonDatabaseDirectory.addKeyListener(this);

		lblFilename.setText(i18n.CREATEDB_LBL_FILENAME);
		jtextfieldFilename.setPreferredSize(dim);
		jtextfieldFilename.addKeyListener(this);

		lblServer.setText(i18n.CREATEDB_LBL_SERVER);
		jtextfieldServer.addKeyListener(this);
		lblPort.setText(i18n.CREATEDB_LBL_PORT);
		jtextfieldPort.addKeyListener(this);

		lblUsername.setText(i18n.CREATEDB_LBL_USERNAME);
		jtextfieldUsername.addKeyListener(this);

		lblPassword.setText(i18n.CREATEDB_LBL_PASSWORD);
		jtextfieldPort.setText("3050");
		jpasswordfield.addKeyListener(this);
		
		radioButtonDialect1.setSelected(true);

		jbuttonCreate.setText(i18n.CREATEDB_LBL_CREATE);
		jbuttonCreate.setToolTipText(i18n.CREATEDB_TOOLTIP_CREATE);
		jbuttonCreate.setIcon(FirebirdManagerHelper.loadIcon("execute16x16.png"));
		jbuttonCreate.addActionListener(this);
		jbuttonCreate.addKeyListener(this);
		
		jbuttonLoadProp.setText("");
		jbuttonLoadProp.setToolTipText(i18n.GLOBAL_TOOLTIP_BTN_PROP_LOAD);
		jbuttonLoadProp.setIcon(FirebirdManagerHelper.loadIcon("fileopen16x16.png"));
		jbuttonLoadProp.addActionListener(this);
		jbuttonLoadProp.addKeyListener(this);
		
		jbuttonSaveProp.setText("");
		jbuttonSaveProp.setToolTipText(i18n.GLOBAL_TOOLTIP_BTN_PROP_SAVE);
		jbuttonSaveProp.setIcon(FirebirdManagerHelper.loadIcon("save16x16.png"));
		jbuttonSaveProp.addActionListener(this);
		jbuttonSaveProp.addKeyListener(this);
	}  
	
	/**
	 * Set input fields with the last saved session preferences when exists
	 * <br>or otherwise with the global preferences
	 */
	private void readPreferences() {
		globalPreferencesBean = PreferencesManager.getGlobalPreferences();
		jtextfieldServer.setText(globalPreferencesBean.getServer());
		jtextfieldPort.setText(globalPreferencesBean.getPort());
		jtextfieldUsername.setText(globalPreferencesBean.getUser());
		
		// preferences from the last session
		sessionPreferencesBean = (FirebirdManagerCreateDatabasePreferenceBean)PreferencesManager.loadPreferences(PreferencesManager.PREFERENCES_BEAN_CREATE_DATABASE);
		if (sessionPreferencesBean.getUser().length() > 0)
			jtextfieldUsername.setText(sessionPreferencesBean.getUser());
		if (sessionPreferencesBean.getServer().length() > 0)
			jtextfieldServer.setText(sessionPreferencesBean.getServer());
		if (sessionPreferencesBean.getPort().length() > 0)
			jtextfieldPort.setText(sessionPreferencesBean.getPort());
		if (sessionPreferencesBean.getSqlDialect() > 0) {
			if (sessionPreferencesBean.getSqlDialect() == 1) 
				radioButtonDialect1.setSelected(true);
			else
				radioButtonDialect3.setSelected(true);
		}
		controlButtonDatabaseFolder();
	}
	
	/**
	 * Load properties from file
	 */
	private void loadProperties() {
		globalPreferencesBean = PreferencesManager.getGlobalPreferences();
		File file = FirebirdManagerHelper.getPropertiesFile(false, globalPreferencesBean.getPropertiesFolder(), "fcp", i18n.CREATEDB_INFO_PROPFILE);
		if (file != null) {
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(file));
			} catch (FileNotFoundException e) {
	            JOptionPane.showMessageDialog(_application.getMainFrame(), i18n.LOAD_PROP_FAILED);
				log.error(e.getLocalizedMessage());
			} catch (IOException e) {
	            JOptionPane.showMessageDialog(_application.getMainFrame(), i18n.LOAD_PROP_FAILED);
				log.error(e.getLocalizedMessage());
			}
			
			// load properties and fill missing entries with the global preferences 
			jtextfieldServer.setText(prop.getProperty("server", globalPreferencesBean.getServer()));
			jtextfieldPort.setText(prop.getProperty("port", globalPreferencesBean.getPort()));
			jtextfieldDatabaseDirectory.setText(prop.getProperty("directory", globalPreferencesBean.getDatabaseFolder()));
			jtextfieldFilename.setText("");
			jtextfieldUsername.setText(prop.getProperty("user", globalPreferencesBean.getUser()));
			if ("1".equals(prop.getProperty("dialect", "1"))) {
				radioButtonDialect1.setSelected(true);
			} else {
				radioButtonDialect3.setSelected(true);
			}
			controlButtonDatabaseFolder();
		}
	}

	/**
	 * Load properties from file
	 */
	private void saveProperties() {
		File file = FirebirdManagerHelper.getPropertiesFile(true, globalPreferencesBean.getPropertiesFolder(), "fcp", i18n.CREATEDB_INFO_PROPFILE);
		if (file != null) {
			boolean saveFile = true;
			if (file.exists()) {
				saveFile = JOptionPane.showConfirmDialog(
						_application.getMainFrame(), i18n.GLOBAL_WARNING_FILE_EXISTS,
						i18n.GLOBAL_TITLE_WARNING, 
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
			}
			
			if (saveFile) {
				Properties prop = new Properties();
				prop.put("server", jtextfieldServer.getText());
				prop.put("port", jtextfieldPort.getText());
				prop.put("directory", jtextfieldDatabaseDirectory.getText());
				prop.put("user", jtextfieldUsername.getText());
				prop.put("dialect", radioButtonDialect1.isSelected() ? "1"
						: "3");

				try {
					prop.store(new FileOutputStream(file),
							"FirebirdManager - Create database properties");
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(_application.getMainFrame(), i18n.SAVE_PROP_FAILED);
					log.error(e.getLocalizedMessage());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(_application.getMainFrame(), i18n.SAVE_PROP_FAILED);
					log.error(e.getLocalizedMessage());
				}
			}
		}
	}
	
	/**
	 * Save the session preferences
	 */
	private void saveSessionPreferences() {
		sessionPreferencesBean.setUser(jtextfieldUsername.getText());
		sessionPreferencesBean.setServer(jtextfieldServer.getText());
		sessionPreferencesBean.setPort(jtextfieldPort.getText());
		sessionPreferencesBean.setDatabaseFolder(jtextfieldDatabaseDirectory.getText());
		sessionPreferencesBean.setSqlDialect(radioButtonDialect1.isSelected() ? 1 : 3);
		
		PreferencesManager.savePreferences(sessionPreferencesBean, PreferencesManager.PREFERENCES_BEAN_CREATE_DATABASE);
	}

	/**
	 * Check for correct/missing input
	 * @return true = allright, false = error
	 */
	private boolean isInputOK() {
		StringBuffer bufError = new StringBuffer();
		boolean focusSet = false;
		
		if (jtextfieldDatabaseDirectory.getText().trim().length() == 0) {
			bufError.append(i18n.CREATEDB_ERROR_DIRECTORY_MISSING + CR);
			jtextfieldDatabaseDirectory.requestFocusInWindow();
			focusSet = true;
		} else {
			File dir = new File(jtextfieldDatabaseDirectory.getText().trim());
			if (!dir.exists()) {
				bufError.append(i18n.CREATEDB_ERROR_DIRECTORY_DOESNOTEXISTS + CR);
			}
			jtextfieldDatabaseDirectory.requestFocusInWindow();
			focusSet = true;
		}
		if (jtextfieldFilename.getText().trim().length() == 0) {
			bufError.append(i18n.CREATEDB_ERROR_FILENAME_MISSING + CR);
			if (!focusSet) {
				jtextfieldFilename.requestFocusInWindow();
				focusSet = true;
			}
		} 
		if (jtextfieldServer.getText().trim().length() == 0) {
			bufError.append(i18n.CREATEDB_ERROR_SERVER_MISSING + CR);
			if (!focusSet) {
				jtextfieldServer.requestFocusInWindow();
				focusSet = true;
			}
		}
		if (jtextfieldPort.getText().trim().length() == 0) {
			bufError.append(i18n.CREATEDB_ERROR_PORT_MISSING + CR);
			if (!focusSet) {
				jtextfieldPort.requestFocusInWindow();
				focusSet = true;
			}
		} else {
			try {
				Integer.parseInt(jtextfieldPort.getText());
			} catch (NumberFormatException e) {
				bufError.append(i18n.CREATEDB_ERROR_PORT_NOINTEGER + CR);
				if (!focusSet) {
					jtextfieldPort.requestFocusInWindow();
					focusSet = true;
				}
			}
		}
		if (jtextfieldUsername.getText().trim().length() == 0) {
			bufError.append(i18n.CREATEDB_ERROR_USERNAME_MISSING + CR);
			if (!focusSet) {
				jtextfieldUsername.requestFocusInWindow();
				focusSet = true;
			}
		}
		
		if (bufError.length() != 0) {
			JOptionPane.showMessageDialog(_application.getMainFrame(), bufError.toString());
		}
		
		return bufError.length() == 0;
	}
	
	/**
	 * Select a target directory 
	 */
	private void selectDirectory() {
		if (jtextfieldServer.getText().trim().toUpperCase().equals("LOCALHOST")) {
			String sFilename = FirebirdManagerHelper.getFileOrDir(jtextfieldDatabaseDirectory.getText(), false);
			if (!sFilename.equals(""))
				jtextfieldDatabaseDirectory.setText(sFilename);
		}
	}
	
	/**
	 * The create database working method
	 */
	private void createDatabase() {
	    if (!isInputOK())
	        return;
	    
		String filename = jtextfieldDatabaseDirectory.getText().trim() + 
		"/" + jtextfieldFilename.getText();
	    File file = new File(filename);

		FBManager fbManager = new FBManager();
		fbManager.setServer(jtextfieldServer.getText());
		fbManager.setPort(Integer.parseInt(jtextfieldPort.getText()));
		
		if (file.exists()) {
			String msg = i18n.CREATEDB_MSG_FILEEXISTS.replaceAll("{0}", filename);
		     if (JOptionPane.showConfirmDialog(_application.getMainFrame(), msg,
		            i18n.CREATEDB_MSG_WARNING, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		        fbManager.setForceCreate(true);
		    else {
		        JOptionPane.showMessageDialog(_application.getMainFrame(), i18n.CREATEDB_MSG_CANCELED);
		        return;
		    }
		}
		
		// set dialog
		FBMaintenanceManager mm = new FBMaintenanceManager();
		try {
			int sqlDialect = radioButtonDialect1.isSelected() ? 1 : 3;
			mm.setDatabaseDialect(sqlDialect);
		} catch (SQLException e) {
			log.error(e.getLocalizedMessage());
		}
		
		
		try {
            fbManager.start();
    		fbManager.createDatabase(filename, 
    				  jtextfieldUsername.getText(), new String(jpasswordfield.getPassword()));
    		fbManager.stop();
    		
            JOptionPane.showMessageDialog(_application.getMainFrame(), i18n.CREATEDB_MSG_SUCCEDED);
            saveSessionPreferences();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(_application.getMainFrame(), i18n.CREATEDB_MSG_FAILED
            		+ CR + CR + e.getLocalizedMessage());
            log.error(e.getLocalizedMessage());
        }
	} 

	/**
	 * Enable the button if server = localhost, else disable the button  
	 */
	private void controlButtonDatabaseFolder() {
		jbuttonDatabaseDirectory.setEnabled(jtextfieldServer.getText().toUpperCase().trim().equals("LOCALHOST"));
		setDatabaseFolder();
	}
	
	/**
	 * Set the database target folder from the global preferences
	 */
	private void setDatabaseFolder() {
		if (jbuttonDatabaseDirectory.isEnabled()
				&& jtextfieldDatabaseDirectory.getText().trim().length() == 0) {
			if (sessionPreferencesBean.getDatabaseFolder().length() > 0) {
				jtextfieldDatabaseDirectory.setText(sessionPreferencesBean.getDatabaseFolder());
			} else {
				jtextfieldDatabaseDirectory.setText(globalPreferencesBean.getDatabaseFolder());
			}
		}
	}
	
	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbuttonDatabaseDirectory) {
			selectDirectory();
		} else if (e.getSource() == jbuttonCreate) {
			createDatabase();
		} else if (e.getSource() == jbuttonLoadProp) {
			loadProperties();
		} else if (e.getSource() == jbuttonSaveProp) {
			saveProperties();
		}
	}
	
	
	// ------------------------------------------------------------------------
    public void keyTyped(KeyEvent e) {
    }
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (e.getSource() == jtextfieldDatabaseDirectory
            		|| e.getSource() == jtextfieldFilename
            		|| e.getSource() == jtextfieldServer
            		|| e.getSource() == jtextfieldPort
            		|| e.getSource() == jtextfieldUsername
            		|| e.getSource() == jpasswordfield
            		|| e.getSource() == jbuttonCreate)
                createDatabase();
            else if (e.getSource() == jbuttonDatabaseDirectory)
                selectDirectory();
        }
    }
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == jtextfieldServer) {
        	controlButtonDatabaseFolder();
        }
    }
}
