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
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.TextAreaOutputStream;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.comp.FBButton;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.FirebirdManagerBackupAndRestorePreferenceBean;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.FirebirdManagerPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.PreferencesManager;
import org.firebirdsql.management.FBBackupManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Internal frame for backup and restore
 * @author Michael Romankiewicz
 */
public class FirebirdManagerBackupRestoreFrame extends DialogWidget
		implements IFirebirdManagerFrame, ActionListener, KeyListener {
	private static final long serialVersionUID = 3260000837769327217L;
	private final String CR = System.getProperty("line.separator", "\n");

	// variables
	// ========================================================================
	// non visible
	// ------------------------------------------------------------------------
	// Logger for this class
	private final static ILogger log = LoggerController
			.createLogger(FirebirdManagerBackupRestoreFrame.class);

	private static final StringManager stringManager = StringManagerFactory
			.getStringManager(FirebirdManagerBackupRestoreFrame.class);
	
	private String databaseFolder = "";
	private FirebirdManagerPreferenceBean globalPreferencesBean;
	// session preferences
	private FirebirdManagerBackupAndRestorePreferenceBean sessionPreferencesBean = null;

	// visible (gui)
	// ------------------------------------------------------------------------
	// -- misc
	private JLabel lblUsername = new JLabel();
	private JLabel lblPW = new JLabel();
	private JTextField jtextfieldUsername = new JTextField();
	private JPasswordField jpasswordfieldPW = new JPasswordField();
	private JCheckBox jcheckboxDisplayProcess = new JCheckBox();
	private JTabbedPane jtabbedpaneMain = new JTabbedPane();
	private JTextArea jtextareaProcess = new JTextArea();
    private JScrollPane jscrollpaneProcess = new JScrollPane();

	// -- backup
	private JLabel lblBckDBHost = new JLabel();
	private JLabel lblBckPort = new JLabel();
	private JLabel lblBckDBFile = new JLabel();
	private JTextField jtextfieldBckDBHost = new JTextField();
	private JTextField jtextfieldBckPort = new JTextField();
	private JTextField jtextfieldBckDBFile = new JTextField();
	private JLabel lblBckBackupFile = new JLabel();
	private JTextField jtextfieldBckBackupFile = new JTextField();
	private JCheckBox jcheckboxBckIgnoreChecksum = new JCheckBox();
	private JCheckBox jcheckboxBckIgnoreLimbo = new JCheckBox();
	private FBButton jbuttonBckDBFile = new FBButton();
	private FBButton jbuttonBckBackupFile = new FBButton();
	private JCheckBox jcheckboxBckMetadataOnly = new JCheckBox();
	private JCheckBox jcheckboxBckGarbageCollection = new JCheckBox();
	private JCheckBox jcheckboxBckOldMetadata = new JCheckBox();
	private JCheckBox jcheckboxBckTransportable = new JCheckBox();
	private JCheckBox jcheckboxBckConvertToTables = new JCheckBox();
	private JCheckBox jcheckboxBckNoDataCompression = new JCheckBox();
	private JButton jbuttonLoadBackupProp = new JButton();
	private JButton jbuttonSaveBackupProp = new JButton();

	private FBButton btnCreateBackupFilename = new FBButton();
	private FBButton jbuttonBackup = new FBButton(true);

	// -- restore
	private JLabel lblResBackupFile = new JLabel();
	private JLabel lblResDBHost = new JLabel();
	private JLabel lblResPort = new JLabel();
	private JLabel lblResDBFile = new JLabel();
	private JTextField jtextfieldResBackupFile = new JTextField();
	private JTextField jtextfieldResDBHost = new JTextField();
	private JTextField jtextfieldResPort = new JTextField();
	private JTextField jtextfieldResDBFile = new JTextField();
	private FBButton jbuttonResBackupFile = new FBButton();
	private FBButton jbuttonResDBFile = new FBButton();
	private JCheckBox jcheckboxResDeactivateIndexes = new JCheckBox();
	private JCheckBox jcheckboxResWithoutShadows = new JCheckBox();
	private JCheckBox jcheckboxResUseAllSpace = new JCheckBox();
	private JCheckBox jcheckboxResOneTableAtTime = new JCheckBox();
	private JCheckBox jcheckboxResWithoutValidity = new JCheckBox();
	private JCheckBox jcheckboxResReplaceDatabase = new JCheckBox();
	private JCheckBox jcheckboxResPageSize = new JCheckBox();
	private JLabel lblPageSize = new JLabel();
	private JTextField jtextfieldPageSize = new JTextField();
	private FBButton btnResCopyBackupFilenameFromBackup = new FBButton();
	private FBButton btnResCopyDatabaseFilenameFromBackup = new FBButton();
	private FBButton jbuttonRestore = new FBButton(true);
	private JButton jbuttonLoadRestoreProp = new JButton();
	private JButton jbuttonSaveRestoreProp = new JButton();
   private IApplication _application;


   // ------------------------------------------------------------------------
	/**
	 * Constructor
    * @param application
    */
	public FirebirdManagerBackupRestoreFrame(IApplication application) {
		super("Firebird manager - "
				+ stringManager.getString("backuprestoremanager.title"), true,
				true, true, true, application);
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
		String LBL_USERNAME = stringManager.getString("backuprestoremanager.label.username");
		String LBL_PASSWORD = stringManager.getString("backuprestoremanager.label.password");
		String LBL_DISPLAY_PROCESS = stringManager.getString("backuprestoremanager.label.display.process");
		String LBL_TABTITLE_BACKUP = stringManager.getString("backuprestoremanager.label.tabtitle.backup");
		String LBL_TABTITLE_RESTORE = stringManager.getString("backuprestoremanager.label.tabtitle.restore");
		String LBL_SERVER = stringManager.getString("backuprestoremanager.label.server");
		String LBL_PORT = stringManager.getString("backuprestoremanager.label.port");
		String LBL_DATABASE_FILE = stringManager.getString("backuprestoremanager.label.database.file");
		String LBL_BACKUP_FILE = stringManager.getString("backuprestoremanager.label.backup.file");
		String LBL_IGNORE_CHECKSUMS = stringManager.getString("backuprestoremanager.label.ignore.checksums");
		String LBL_IGNORE_LIMBO = stringManager.getString("backuprestoremanager.label.ignore.limbo");
		String LBL_BACKUP_METADATA_ONLY = stringManager.getString("backuprestoremanager.label.backup.metadata.only");
		String LBL_GARBAGE_COLLECTION = stringManager.getString("backuprestoremanager.label.garbage.collection");
		String LBL_OLD_STYLE_METADATA = stringManager.getString("backuprestoremanager.label.old.style.metadata");
		String LBL_TRANSPORTABLE_FORMAT = stringManager.getString("backuprestoremanager.label.transportable.format");
		String LBL_BACKUP_AS_TABLES = stringManager.getString("backuprestoremanager.label.backup.as.tables");
		String LBL_START_BACKUP = stringManager.getString("backuprestoremanager.label.start.backup");
		String LBL_DEACTIVATE_INDEXES = stringManager.getString("backuprestoremanager.label.deactivate.indexes");
		String LBL_RESTORE_WITHOUT_SHADOWS = stringManager.getString("backuprestoremanager.label.restore.without.shadows");
		String LBL_USE_ALL_SPACE = stringManager.getString("backuprestoremanager.label.use.all.space");
		String LBL_ONE_TABLE_AT_TIME = stringManager.getString("backuprestoremanager.label.one.table.at.time");
		String LBL_WITHOUT_VALIDITY = stringManager.getString("backuprestoremanager.label.without.validity");
		String LBL_REPLACE_DATABASE = stringManager.getString("backuprestoremanager.label.replace.database");
		String LBL_OVERRIDE_PAGE_SIZE = stringManager.getString("backuprestoremanager.label.override.page.size");
		String LBL_NEW_PAGE_SIZE = stringManager.getString("backuprestoremanager.label.new.page.size");
		String LBL_RESTORE = stringManager.getString("backuprestoremanager.label.restore");
		String TOOLTIP_GENERATE_BACKUPFILENAME = stringManager.getString("backuprestoremanager.tooltip.generate.backupfilename");
		String TOOLTIP_ADOPT_FROM_BACKUP_PAGE = stringManager.getString("backuprestoremanager.tooltip.adopt.from.backup.page");
		String GLOBAL_TOOLTIP_BTN_PROP_LOAD = stringManager.getString("global.tooltip.btn.prop.load");
		String GLOBAL_TOOLTIP_BTN_PROP_SAVE = stringManager.getString("global.tooltip.btn.prop.save");
		String GLOBAL_WARNING_FILE_EXISTS = stringManager.getString("global.warning.file.exists");
		String GLOBAL_TITLE_WARNING = stringManager.getString("global.title.warning");
		
		// errors
		String ERROR_USERNAME_MISSING = stringManager.getString("backuprestoremanager.error.username.missing");
		String ERROR_FILENAME_BACKUP_MISSING = stringManager.getString("backuprestoremanager.error.filename.backupfile.missing");
		String ERROR_FILENAME_DATABASE_MISSING = stringManager.getString("backuprestoremanager.error.filename.databasefile.missing");
		String ERROR_FILENAME_NOT_EXISTS = stringManager.getString("backuprestoremanager.error.filename.doesnotexists");
		String ERROR_SERVER_MISSING = stringManager.getString("backuprestoremanager.error.server.missing");
		String ERROR_PORT_MISSING = stringManager.getString("backuprestoremanager.error.port.missing");
		String ERROR_PORT_NO_INTEGER = stringManager.getString("backuprestoremanager.error.port.nointeger");
		String LOAD_PROP_FAILED = stringManager.getString("global.error.prop.load");
		String SAVE_PROP_FAILED = stringManager.getString("global.error.prop.save");
		
		// info
		String INFO_BACKUP_COMPLETED = stringManager.getString("backuprestoremanager.info.backup.completed");
		String INFO_BACKUP_CANCELED = stringManager.getString("backuprestoremanager.info.backup.canceled");
		String INFO_RESTORE_COMPLETED = stringManager.getString("backuprestoremanager.info.restore.completed");
		String INFO_RESTORE_CANCELED = stringManager.getString("backuprestoremanager.info.restore.canceled");
		String INFO_PROPFILE_BACKUP = stringManager.getString("backuprestoremanager.info.properties.file.backup");
		String INFO_PROPFILE_RESTORE = stringManager.getString("backuprestoremanager.info.properties.file.restore");
	}

	private void initLayout() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(createPanelMain());

		initVisualObjects();
		readPreferences();
	}

	/**
	 * Configure the visual components with texts, icons and listeners
	 */
	private void initVisualObjects() {
		Icon iconFileSelection = FirebirdManagerHelper.loadIcon("fileopen16x16.png");
		Icon iconStartWork = FirebirdManagerHelper.loadIcon("execute16x16.png");
		Icon iconAdopt = FirebirdManagerHelper.loadIcon("import16x16.png");
		Icon iconGenerate = FirebirdManagerHelper.loadIcon("generate16x16.png");
		Icon iconSave = FirebirdManagerHelper.loadIcon("save16x16.png");
		
		jbuttonBckBackupFile.addActionListener(this);
		jbuttonBckDBFile.addActionListener(this);
		jbuttonBackup.addActionListener(this);
		jbuttonResBackupFile.addActionListener(this);
		jbuttonResDBFile.addActionListener(this);
		jbuttonRestore.addActionListener(this);
		btnCreateBackupFilename.addActionListener(this);
		btnResCopyBackupFilenameFromBackup.addActionListener(this);
		btnResCopyDatabaseFilenameFromBackup.addActionListener(this);

		jtextfieldBckDBHost.addKeyListener(this);
		jtextfieldResDBHost.addKeyListener(this);
		
		jcheckboxResPageSize.addActionListener(this);
		jtextfieldPageSize.setEnabled(false);
		jtextfieldPageSize.setBackground(Color.LIGHT_GRAY);
		
		lblUsername.setText(i18n.LBL_USERNAME);
		lblPW.setText(i18n.LBL_PASSWORD);
		jcheckboxDisplayProcess.setText(i18n.LBL_DISPLAY_PROCESS);
		jcheckboxDisplayProcess.setSelected(true);
		
		jtabbedpaneMain.setTitleAt(0, i18n.LBL_TABTITLE_BACKUP);
		jtabbedpaneMain.setTitleAt(1, i18n.LBL_TABTITLE_RESTORE);

		jbuttonLoadBackupProp.setText("");
		jbuttonLoadBackupProp.setToolTipText(i18n.GLOBAL_TOOLTIP_BTN_PROP_LOAD);
		jbuttonLoadBackupProp.setIcon(iconFileSelection);
		jbuttonLoadBackupProp.addActionListener(this);
		jbuttonLoadBackupProp.addKeyListener(this);
		jbuttonSaveBackupProp.setText("");
		jbuttonSaveBackupProp.setToolTipText(i18n.GLOBAL_TOOLTIP_BTN_PROP_SAVE);
		jbuttonSaveBackupProp.setIcon(iconSave);
		jbuttonSaveBackupProp.addActionListener(this);
		jbuttonSaveBackupProp.addKeyListener(this);
		lblBckDBHost.setText(i18n.LBL_SERVER); // "server"
		lblBckPort.setText(i18n.LBL_PORT); // "port"
		lblBckDBFile.setText(i18n.LBL_DATABASE_FILE); // "database file"
		lblBckBackupFile.setText(i18n.LBL_BACKUP_FILE); // "backup file"
		btnCreateBackupFilename.setText("");
		btnCreateBackupFilename.setToolTipText(i18n.TOOLTIP_GENERATE_BACKUPFILENAME);
		btnCreateBackupFilename.setIcon(iconGenerate);
		jcheckboxBckIgnoreChecksum.setText(i18n.LBL_IGNORE_CHECKSUMS); // "ignore bad checksums"
		jcheckboxBckIgnoreLimbo.setText(i18n.LBL_IGNORE_LIMBO); // "ignore transaction in Limbo"
		jbuttonBckDBFile.setText("");
		jbuttonBckDBFile.setIcon(iconFileSelection);
		jbuttonBckBackupFile.setText("");
		jbuttonBckBackupFile.setIcon(iconFileSelection);
		jcheckboxBckMetadataOnly.setText(i18n.LBL_BACKUP_METADATA_ONLY); // "backup metadata only"
		jcheckboxBckGarbageCollection.setText(i18n.LBL_GARBAGE_COLLECTION); // "inhibit garbage collection"
		jcheckboxBckOldMetadata.setText(i18n.LBL_OLD_STYLE_METADATA); // "save old style metadata descriptions"
		jcheckboxBckTransportable.setText(i18n.LBL_TRANSPORTABLE_FORMAT); // "transportable format"
		jcheckboxBckConvertToTables.setText(i18n.LBL_BACKUP_AS_TABLES); // "backup external files as tables"
		jbuttonBackup.setText(i18n.LBL_START_BACKUP); // "start backup"
		jbuttonBackup.setIcon(iconStartWork);

		jbuttonLoadRestoreProp.setText("");
		jbuttonLoadRestoreProp.setToolTipText(i18n.GLOBAL_TOOLTIP_BTN_PROP_LOAD);
		jbuttonLoadRestoreProp.setIcon(iconFileSelection);
		jbuttonLoadRestoreProp.addActionListener(this);
		jbuttonLoadRestoreProp.addKeyListener(this);
		jbuttonSaveRestoreProp.setText("");
		jbuttonSaveRestoreProp.setToolTipText(i18n.GLOBAL_TOOLTIP_BTN_PROP_SAVE);
		jbuttonSaveRestoreProp.setIcon(iconSave);
		jbuttonSaveRestoreProp.addActionListener(this);
		jbuttonSaveRestoreProp.addKeyListener(this);
		lblResBackupFile.setText(i18n.LBL_BACKUP_FILE); // "backup file"
		btnResCopyBackupFilenameFromBackup.setText("");
		btnResCopyBackupFilenameFromBackup.setToolTipText(i18n.TOOLTIP_ADOPT_FROM_BACKUP_PAGE);
		btnResCopyBackupFilenameFromBackup.setIcon(iconAdopt);
		lblResDBHost.setText(i18n.LBL_SERVER); // "server"
		lblResPort.setText(i18n.LBL_PORT); // "port"
		lblResDBFile.setText(i18n.LBL_DATABASE_FILE); // "database file"
		btnResCopyDatabaseFilenameFromBackup.setText("");
		btnResCopyDatabaseFilenameFromBackup.setToolTipText(i18n.TOOLTIP_ADOPT_FROM_BACKUP_PAGE);
		btnResCopyDatabaseFilenameFromBackup.setIcon(iconAdopt);
		jbuttonResBackupFile.setText("");
		jbuttonResBackupFile.setIcon(iconFileSelection);
		jbuttonResDBFile.setText("");
		jbuttonResDBFile.setIcon(iconFileSelection);
		jcheckboxResDeactivateIndexes.setText(i18n.LBL_DEACTIVATE_INDEXES); // "deactivate indexes during restore"
		jcheckboxResWithoutShadows.setText(i18n.LBL_RESTORE_WITHOUT_SHADOWS); // "restore without creating shadows"
		jcheckboxResUseAllSpace.setText(i18n.LBL_USE_ALL_SPACE); // "do not reserve space for record versions"
		jcheckboxResOneTableAtTime.setText(i18n.LBL_ONE_TABLE_AT_TIME); // "restores one table at a time"
		jcheckboxResWithoutValidity.setText(i18n.LBL_WITHOUT_VALIDITY); // "do not restore database validity conditions"
		jcheckboxResReplaceDatabase.setText(i18n.LBL_REPLACE_DATABASE); // "replace database from backup file"
		jcheckboxResPageSize.setText(i18n.LBL_OVERRIDE_PAGE_SIZE); // "override old page size"
		lblPageSize.setText(i18n.LBL_NEW_PAGE_SIZE); // "new page size"
		jbuttonRestore.setText(i18n.LBL_RESTORE); // "restore"
		jbuttonRestore.setIcon(iconStartWork);

	}

	public JPanel createPanelMain() {
	      JPanel jpanelMain = new JPanel();
	      FormLayout formlayout1 = new FormLayout("FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE","CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:5DLU:NONE,FILL:70DLU:NONE,CENTER:5DLU:NONE");
	      CellConstraints cc = new CellConstraints();
	      jpanelMain.setLayout(formlayout1);

	      lblUsername.setName("lblUsername");
	      lblUsername.setText("username");
	      jpanelMain.add(lblUsername,cc.xy(2,2));

	      lblPW.setName("lblPW");
	      lblPW.setText("password");
	      jpanelMain.add(lblPW,cc.xy(2,4));

	      jtextfieldUsername.setName("jtextfieldUsername");
	      jpanelMain.add(jtextfieldUsername,cc.xy(4,2));

	      jpasswordfieldPW.setName("jpasswordfieldPW");
	      jpanelMain.add(jpasswordfieldPW,cc.xy(4,4));

	      jtabbedpaneMain.setName("jtabbedpaneMain");
	      jtabbedpaneMain.addTab("backup",null,createPanelBackup());
	      jtabbedpaneMain.addTab("restore",null,createPanelRestore());
	      jpanelMain.add(jtabbedpaneMain,cc.xywh(2,8,5,1));

	      jtextareaProcess.setName("jtextareaProcess");
//	      JScrollPane jscrollpane1 = new JScrollPane();
	      jscrollpaneProcess.setViewportView(jtextareaProcess);
	      jscrollpaneProcess.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	      jscrollpaneProcess.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	      jpanelMain.add(jscrollpaneProcess,cc.xywh(2,10,5,1));

	      jcheckboxDisplayProcess.setActionCommand("display process");
	      jcheckboxDisplayProcess.setName("jcheckboxDisplayProcess");
	      jcheckboxDisplayProcess.setText("display process");
	      jpanelMain.add(jcheckboxDisplayProcess,cc.xy(4,6));

	      return jpanelMain;
	}

	public JPanel createPanelBackup() {
	      JPanel jpanelBackup = new JPanel();
	      FormLayout formlayout1 = new FormLayout("FILL:3DLU:NONE,LEFT:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:30DLU:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE","CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,FILL:DEFAULT:NONE,FILL:5DLU:NONE");
	      CellConstraints cc = new CellConstraints();
	      jpanelBackup.setLayout(formlayout1);

	      lblBckDBHost.setName("lblBckDBHost");
	      lblBckDBHost.setText("database host");
	      jpanelBackup.add(lblBckDBHost,cc.xy(2,2));

	      lblBckDBFile.setName("lblBckDBFile");
	      lblBckDBFile.setText("database file");
	      jpanelBackup.add(lblBckDBFile,cc.xy(2,4));

	      jtextfieldBckDBHost.setName("jtextfieldBckDBHost");
	      jpanelBackup.add(jtextfieldBckDBHost,cc.xywh(4,2,3,1));

	      jtextfieldBckDBFile.setName("jtextfieldBckDBFile");
	      jpanelBackup.add(jtextfieldBckDBFile,cc.xywh(4,4,7,1));

	      lblBckBackupFile.setName("lblBckBackupFile");
	      lblBckBackupFile.setText("backup file");
	      jpanelBackup.add(lblBckBackupFile,cc.xy(2,6));

	      jtextfieldBckBackupFile.setName("jtextfieldBckBackupFile");
	      jpanelBackup.add(jtextfieldBckBackupFile,cc.xywh(4,6,7,1));

	      jcheckboxBckIgnoreChecksum.setActionCommand("ignore bad checksums");
	      jcheckboxBckIgnoreChecksum.setName("jcheckboxBckIgnoreChecksum");
	      jcheckboxBckIgnoreChecksum.setText("ignore bad checksums");
	      jpanelBackup.add(jcheckboxBckIgnoreChecksum,cc.xywh(2,8,3,1));

	      jcheckboxBckIgnoreLimbo.setActionCommand("ignore transaction in Limbo");
	      jcheckboxBckIgnoreLimbo.setName("jcheckboxBckIgnoreLimbo");
	      jcheckboxBckIgnoreLimbo.setText("ignore transaction in Limbo");
	      jpanelBackup.add(jcheckboxBckIgnoreLimbo,cc.xywh(6,8,9,1));

	      jbuttonBckDBFile.setActionCommand("...");
	      jbuttonBckDBFile.setName("jbuttonBckDBFile");
	      jbuttonBckDBFile.setText("...");
	      jpanelBackup.add(jbuttonBckDBFile,cc.xy(14,4));

	      jbuttonBckBackupFile.setActionCommand("...");
	      jbuttonBckBackupFile.setName("jbuttonBckBackupFile");
	      jbuttonBckBackupFile.setText("...");
	      jpanelBackup.add(jbuttonBckBackupFile,cc.xy(14,6));

	      jcheckboxBckMetadataOnly.setActionCommand("backup metadata only");
	      jcheckboxBckMetadataOnly.setName("jcheckboxBckMetadataOnly");
	      jcheckboxBckMetadataOnly.setText("backup metadata only");
	      jpanelBackup.add(jcheckboxBckMetadataOnly,cc.xywh(2,10,3,1));

	      jcheckboxBckGarbageCollection.setActionCommand("inhibit garbage collection");
	      jcheckboxBckGarbageCollection.setName("jcheckboxBckGarbageCollection");
	      jcheckboxBckGarbageCollection.setText("inhibit garbage collection");
	      jpanelBackup.add(jcheckboxBckGarbageCollection,cc.xywh(6,10,9,1));

	      jcheckboxBckOldMetadata.setActionCommand("save old style metadata descriptions");
	      jcheckboxBckOldMetadata.setName("jcheckboxBckOldMetadata");
	      jcheckboxBckOldMetadata.setText("save old style metadata descriptions");
	      jpanelBackup.add(jcheckboxBckOldMetadata,cc.xywh(2,12,3,1));

	      jcheckboxBckTransportable.setActionCommand("transportable format");
	      jcheckboxBckTransportable.setName("jcheckboxBckTransportable");
	      jcheckboxBckTransportable.setSelected(true);
	      jcheckboxBckTransportable.setText("transportable format");
	      jpanelBackup.add(jcheckboxBckTransportable,cc.xywh(2,14,3,1));

	      jcheckboxBckConvertToTables.setActionCommand("backup external files as tables");
	      jcheckboxBckConvertToTables.setName("jcheckboxBckConvertToTables");
	      jcheckboxBckConvertToTables.setText("backup external files as tables");
	      jpanelBackup.add(jcheckboxBckConvertToTables,cc.xywh(6,12,9,1));

	      jpanelBackup.add(createPanelBackupButton(),new CellConstraints(2,16,13,1,CellConstraints.CENTER,CellConstraints.DEFAULT));
	      lblBckPort.setName("lblBckPort");
	      lblBckPort.setText("Port");
	      jpanelBackup.add(lblBckPort,cc.xy(8,2));

	      jtextfieldBckPort.setName("jtextfieldBckPort");
	      jpanelBackup.add(jtextfieldBckPort,cc.xy(10,2));

	      btnCreateBackupFilename.setActionCommand("...");
	      btnCreateBackupFilename.setName("btnCreateBackupFilename");
	      btnCreateBackupFilename.setText("...");
	      btnCreateBackupFilename.setToolTipText("create date filename");
	      jpanelBackup.add(btnCreateBackupFilename,cc.xy(12,6));

	      jcheckboxBckNoDataCompression.setActionCommand("no data compression");
	      jcheckboxBckNoDataCompression.setName("jcheckboxNoDataCompression");
	      jcheckboxBckNoDataCompression.setText("no data compression");
	      jpanelBackup.add(jcheckboxBckNoDataCompression,cc.xywh(6,14,9,1));

	      jbuttonSaveBackupProp.setActionCommand("...");
	      jbuttonSaveBackupProp.setName("jbuttonSaveBackupProp");
	      jbuttonSaveBackupProp.setText("...");
	      jpanelBackup.add(jbuttonSaveBackupProp,cc.xy(12,2));

	      jbuttonLoadBackupProp.setActionCommand("...");
	      jbuttonLoadBackupProp.setName("jbuttonLoadBackupProp");
	      jbuttonLoadBackupProp.setText("...");
	      jpanelBackup.add(jbuttonLoadBackupProp,cc.xy(14,2));

	      return jpanelBackup;
	}

   public JPanel createPanelBackupButton() {
		JPanel jpanelBackupButton = new JPanel();
		FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE",
				"BOTTOM:DEFAULT:NONE");
		jpanelBackupButton.setLayout(formlayout1);

		jbuttonBackup.setActionCommand("Start Backup");
		jbuttonBackup.setName("jbuttonBackup");
		jbuttonBackup.setText("Start Backup");
		jpanelBackupButton.add(jbuttonBackup, new CellConstraints(1, 1, 1, 1,
				CellConstraints.DEFAULT, CellConstraints.BOTTOM));

		return jpanelBackupButton;
	}

    public JPanel createPanelRestore() {
		JPanel jpanelRestore = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:30DLU:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE",
				"CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,FILL:DEFAULT:NONE,FILL:5DLU:NONE");
		CellConstraints cc = new CellConstraints();
		jpanelRestore.setLayout(formlayout1);

		lblResDBFile.setName("lblResDBFile");
		lblResDBFile.setText("database file");
		jpanelRestore.add(lblResDBFile, cc.xy(2, 6));

		jtextfieldResDBFile.setName("jtextfieldResDBFile");
		jpanelRestore.add(jtextfieldResDBFile, cc.xywh(4, 6, 7, 1));

		jbuttonResDBFile.setActionCommand("...");
		jbuttonResDBFile.setName("jbuttonResDBFile");
		jbuttonResDBFile.setText("...");
		jpanelRestore.add(jbuttonResDBFile, cc.xy(14, 6));

		jcheckboxResDeactivateIndexes
				.setActionCommand("deactivate indexes during restore");
		jcheckboxResDeactivateIndexes.setName("jcheckboxResDeactivateIndexes");
		jcheckboxResDeactivateIndexes
				.setText("deactivate indexes during restore");
		jpanelRestore.add(jcheckboxResDeactivateIndexes, cc.xywh(2, 8, 3, 1));

		jcheckboxResWithoutShadows
				.setActionCommand("restore without creating shadows");
		jcheckboxResWithoutShadows.setName("jcheckboxResWithoutShadows");
		jcheckboxResWithoutShadows.setText("restore without creating shadows");
		jpanelRestore.add(jcheckboxResWithoutShadows, cc.xywh(2, 10, 3, 1));

		jcheckboxResUseAllSpace
				.setActionCommand("do not reserve space for record versions");
		jcheckboxResUseAllSpace.setName("jcheckboxResUseAllSpace");
		jcheckboxResUseAllSpace
				.setText("do not reserve space for record versions");
		jpanelRestore.add(jcheckboxResUseAllSpace, cc.xywh(2, 12, 3, 1));

		jcheckboxResOneTableAtTime
				.setActionCommand("restores one table at a time");
		jcheckboxResOneTableAtTime.setName("jcheckboxResOneTableAtTime");
		jcheckboxResOneTableAtTime.setText("restores one table at a time");
		jpanelRestore.add(jcheckboxResOneTableAtTime, cc.xywh(6, 8, 9, 1));

		jcheckboxResWithoutValidity
				.setActionCommand("do not restore database validity conditions");
		jcheckboxResWithoutValidity.setName("jcheckboxResWithoutValidity");
		jcheckboxResWithoutValidity
				.setText("do not restore database validity conditions");
		jpanelRestore.add(jcheckboxResWithoutValidity, cc.xywh(6, 10, 9, 1));

		jcheckboxResReplaceDatabase
				.setActionCommand("replace database from backup file");
		jcheckboxResReplaceDatabase.setName("jcheckboxResReplaceDatabase");
		jcheckboxResReplaceDatabase
				.setText("replace database from backup file");
		jpanelRestore.add(jcheckboxResReplaceDatabase, cc.xywh(6, 12, 9, 1));

		jpanelRestore.add(createPanelRestoreButton(), new CellConstraints(2,
				16, 13, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
		jcheckboxResPageSize.setActionCommand("override old page size");
		jcheckboxResPageSize.setName("jcheckboxResPageSize");
		jcheckboxResPageSize.setText("override old page size");
		jpanelRestore.add(jcheckboxResPageSize, cc.xywh(2, 14, 3, 1));

		jpanelRestore.add(createPanelRestorePageSize(), cc.xywh(6, 14, 9, 1));
		jtextfieldResPort.setName("jtextfieldResPort");
		jpanelRestore.add(jtextfieldResPort, cc.xy(10, 2));

		lblResPort.setName("lblResPort");
		lblResPort.setText("Port");
		jpanelRestore.add(lblResPort, cc.xy(8, 2));

		jtextfieldResBackupFile.setName("jtextfieldResBackupFile");
		jpanelRestore.add(jtextfieldResBackupFile, cc.xywh(4, 4, 7, 1));

		lblResDBHost.setName("lblResDBHost");
		lblResDBHost.setText("database host");
		jpanelRestore.add(lblResDBHost, cc.xy(2, 2));

		lblResBackupFile.setName("lblResBackupFile");
		lblResBackupFile.setText("backup file");
		jpanelRestore.add(lblResBackupFile, cc.xy(2, 4));

		jtextfieldResDBHost.setName("jtextfieldResDBHost");
		jpanelRestore.add(jtextfieldResDBHost, cc.xywh(4, 2, 3, 1));

		jbuttonResBackupFile.setActionCommand("...");
		jbuttonResBackupFile.setName("jbuttonResBackupFile");
		jbuttonResBackupFile.setText("...");
		jpanelRestore.add(jbuttonResBackupFile, cc.xy(14, 4));

		btnResCopyBackupFilenameFromBackup.setActionCommand("...");
		btnResCopyBackupFilenameFromBackup
				.setName("btnResCopyBackupFilenameFromBackup");
		btnResCopyBackupFilenameFromBackup.setText("...");
		btnResCopyBackupFilenameFromBackup
				.setToolTipText("copy backup filename from the backup page");
		jpanelRestore.add(btnResCopyBackupFilenameFromBackup, cc.xy(12, 4));

		btnResCopyDatabaseFilenameFromBackup.setActionCommand("...");
		btnResCopyDatabaseFilenameFromBackup
				.setName("btnResCopyDatabaseFilenameFromBackup");
		btnResCopyDatabaseFilenameFromBackup.setText("...");
		btnResCopyDatabaseFilenameFromBackup
				.setToolTipText("copy database filename from the backup page");
		jpanelRestore.add(btnResCopyDatabaseFilenameFromBackup, cc.xy(12, 6));

		jbuttonSaveRestoreProp.setActionCommand("...");
		jbuttonSaveRestoreProp.setName("jbuttonSaveRestoreProp");
		jbuttonSaveRestoreProp.setText("...");
		jpanelRestore.add(jbuttonSaveRestoreProp, cc.xy(12, 2));

		jbuttonLoadRestoreProp.setActionCommand("...");
		jbuttonLoadRestoreProp.setName("jbuttonLoadRestoreProp");
		jbuttonLoadRestoreProp.setText("...");
		jpanelRestore.add(jbuttonLoadRestoreProp, cc.xy(14, 2));

		return jpanelRestore;
	}

	public JPanel createPanelRestoreButton() {
		JPanel jpanelRestoreButton = new JPanel();
		FormLayout formlayout1 = new FormLayout("CENTER:DEFAULT:NONE",
				"BOTTOM:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanelRestoreButton.setLayout(formlayout1);

		jbuttonRestore.setActionCommand("JButton");
		jbuttonRestore.setName("jbuttonRestore");
		jbuttonRestore.setText("JButton");
		jpanelRestoreButton.add(jbuttonRestore, cc.xy(1, 1));

		return jpanelRestoreButton;
	}

	public JPanel createPanelRestorePageSize() {
		JPanel jpanelRestorePageSize = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:30DLU:NONE",
				"CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanelRestorePageSize.setLayout(formlayout1);

		lblPageSize.setName("lblPageSize");
		lblPageSize.setText("new page size");
		jpanelRestorePageSize.add(lblPageSize, cc.xy(1, 1));

		jtextfieldPageSize.setEnabled(false);
		jtextfieldPageSize.setName("jtextfieldPageSize");
		jpanelRestorePageSize.add(jtextfieldPageSize, cc.xy(3, 1));

		return jpanelRestorePageSize;
	}
	
	public void setFocusToFirstEmptyInputField() {
		if (jtextfieldUsername.getText().length() == 0) {
			jtextfieldUsername.requestFocusInWindow();
		} else {
			jpasswordfieldPW.requestFocusInWindow();
		}
	}


	/**
	 * Set input fields with the last saved session preferences when exists
	 * <br>or otherwise with the global preferences
	 */
	private void readPreferences() {
		// global preferences
		globalPreferencesBean = PreferencesManager.getGlobalPreferences();
		databaseFolder = globalPreferencesBean.getDatabaseFolder();
		if (databaseFolder == null) {
			databaseFolder = "";
		}
		jtextfieldUsername.setText(globalPreferencesBean.getUser());
		jtextfieldBckDBHost.setText(globalPreferencesBean.getServer());
		jtextfieldBckPort.setText(globalPreferencesBean.getPort());
		jtextfieldResDBHost.setText(globalPreferencesBean.getServer());
		jtextfieldResPort.setText(globalPreferencesBean.getPort());
		
		// preferences from the last session
		sessionPreferencesBean = (FirebirdManagerBackupAndRestorePreferenceBean)PreferencesManager.loadPreferences(PreferencesManager.PREFERENCES_BEAN_BEACKUP_AND_RESTORE);
		if (sessionPreferencesBean.getUser().length() > 0)
			jtextfieldUsername.setText(sessionPreferencesBean.getUser());
		if (sessionPreferencesBean.getServer().length() > 0)
			jtextfieldResDBHost.setText(sessionPreferencesBean.getServer());
		if (sessionPreferencesBean.getPort().length() > 0)
			jtextfieldBckPort.setText(sessionPreferencesBean.getPort());
		if (sessionPreferencesBean.getBckDatabaseFilename().length() > 0) 
			jtextfieldBckDBFile.setText(sessionPreferencesBean.getBckDatabaseFilename());
		if (sessionPreferencesBean.getBckBackupFilename().length() > 0)
			jtextfieldBckBackupFile.setText(sessionPreferencesBean.getBckBackupFilename());
		jcheckboxDisplayProcess.setSelected(sessionPreferencesBean.isDisplayProcess());
	}
	
	/**
	 * Save the session preferences (only backup entries, restore filenames must be selected manually)
	 */
	private void saveSessionPreferences() {
		sessionPreferencesBean.setUser(jtextfieldUsername.getText());
		sessionPreferencesBean.setServer(jtextfieldResDBHost.getText());
		sessionPreferencesBean.setPort(jtextfieldBckPort.getText());
		sessionPreferencesBean.setBckDatabaseFilename(jtextfieldBckDBFile.getText()); 
		sessionPreferencesBean.setBckBackupFilename(jtextfieldBckBackupFile.getText());
		sessionPreferencesBean.setDisplayProcess(jcheckboxDisplayProcess.isSelected());
		
		PreferencesManager.savePreferences(sessionPreferencesBean, PreferencesManager.PREFERENCES_BEAN_BEACKUP_AND_RESTORE);
	}
	
	/**
	 * Select a filename for the given textfield
	 * @param textField textfield to fill with the selected filename
	 */
	private void selectFile(JTextField textField) {
		String oldName = textField.getText().trim().length() == 0 ? databaseFolder : textField.getText();
		String newFilename = FirebirdManagerHelper.getFileOrDir(oldName, true);
		if (!newFilename.equals(""))
			textField.setText(newFilename);
	}

	/**
	 * Load backup properties from file
	 */
	private void loadBackupProperties() {
		File file = FirebirdManagerHelper.getPropertiesFile(false, globalPreferencesBean.getPropertiesFolder(), "fbp", i18n.INFO_PROPFILE_BACKUP);
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
			sessionPreferencesBean = (FirebirdManagerBackupAndRestorePreferenceBean)PreferencesManager.loadPreferences(PreferencesManager.PREFERENCES_BEAN_BEACKUP_AND_RESTORE);
			jtextfieldUsername.setText(prop.getProperty("user", sessionPreferencesBean.getUser()));
			jcheckboxDisplayProcess.setSelected(prop.getProperty("display.process", "true").equals("true"));
			jtextfieldBckDBHost.setText(prop.getProperty("backup.server", sessionPreferencesBean.getServer()));
			jtextfieldBckPort.setText(prop.getProperty("backup.port", sessionPreferencesBean.getPort()));
			jtextfieldBckDBFile.setText(prop.getProperty("backup.dbfile", ""));

			jcheckboxBckIgnoreChecksum.setSelected(prop.getProperty("backup.checksums", "true").equals("true"));
			jcheckboxBckMetadataOnly.setSelected(prop.getProperty("backup.metadata", "true").equals("true"));
			jcheckboxBckOldMetadata.setSelected(prop.getProperty("backup.oldstyle", "true").equals("true"));
			jcheckboxBckTransportable.setSelected(prop.getProperty("backup.transportable", "true").equals("true"));
			jcheckboxBckIgnoreLimbo.setSelected(prop.getProperty("backup.limbo", "true").equals("true"));
			jcheckboxBckGarbageCollection.setSelected(prop.getProperty("backup.garbarge", "true").equals("true"));
			jcheckboxBckConvertToTables.setSelected(prop.getProperty("backup.totables", "true").equals("true"));
			jcheckboxBckNoDataCompression.setSelected(prop.getProperty("backup.nocompression", "true").equals("true"));
		}
	}

	/**
	 * Save backup properties from file
	 */
	private void saveBackupProperties() {
		File file = FirebirdManagerHelper.getPropertiesFile(true, globalPreferencesBean.getPropertiesFolder(), "fbp", i18n.INFO_PROPFILE_BACKUP);
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
				prop.put("user", jtextfieldUsername.getText());
				prop.put("display.process", jcheckboxDisplayProcess.isSelected() ? "true" : "false");
				prop.put("backup.server", jtextfieldBckDBHost.getText());
				prop.put("backup.port", jtextfieldBckPort.getText());
				prop.put("backup.dbfile", jtextfieldBckDBFile.getText());
				prop.put("backup.checksums", jcheckboxBckIgnoreChecksum.isSelected() ? "true" : "false");
				prop.put("backup.metadata", jcheckboxBckMetadataOnly.isSelected() ? "true" : "false");
				prop.put("backup.oldstyle", jcheckboxBckOldMetadata.isSelected() ? "true" : "false");
				prop.put("backup.transportable", jcheckboxBckTransportable.isSelected() ? "true" : "false");
				prop.put("backup.limbo", jcheckboxBckIgnoreLimbo.isSelected() ? "true" : "false");
				prop.put("backup.garbarge", jcheckboxBckGarbageCollection.isSelected() ? "true" : "false");
				prop.put("backup.totables", jcheckboxBckConvertToTables.isSelected() ? "true" : "false");
				prop.put("backup.nocompression", jcheckboxBckNoDataCompression.isSelected() ? "true" : "false");

				try {
					prop.store(new FileOutputStream(file),
							"FirebirdManager - Backup properties");
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
	 * Load restore properties from file
	 */
	private void loadRestoreProperties() {
		File file = FirebirdManagerHelper.getPropertiesFile(false, globalPreferencesBean.getPropertiesFolder(), "frp", i18n.INFO_PROPFILE_RESTORE);
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
			sessionPreferencesBean = (FirebirdManagerBackupAndRestorePreferenceBean)PreferencesManager.loadPreferences(PreferencesManager.PREFERENCES_BEAN_BEACKUP_AND_RESTORE);
			jtextfieldUsername.setText(prop.getProperty("user", sessionPreferencesBean.getUser()));
			jcheckboxDisplayProcess.setSelected(prop.getProperty("display.process", "true").equals("true"));
			jtextfieldResDBHost.setText(prop.getProperty("restore.server", sessionPreferencesBean.getServer()));
			jtextfieldResPort.setText(prop.getProperty("restore.port", sessionPreferencesBean.getPort()));
			jtextfieldResDBFile.setText(prop.getProperty("restore.dbfile", ""));

			jcheckboxResDeactivateIndexes.setSelected(prop.getProperty("restore.indexes", "true").equals("true"));
			jcheckboxResWithoutShadows.setSelected(prop.getProperty("restore.shadows", "true").equals("true"));
			jcheckboxResUseAllSpace.setSelected(prop.getProperty("restore.allspace", "true").equals("true"));
			jcheckboxResOneTableAtTime.setSelected(prop.getProperty("restore.onetable", "true").equals("true"));
			jcheckboxResWithoutValidity.setSelected(prop.getProperty("restore.validity", "true").equals("true"));
			jcheckboxResReplaceDatabase.setSelected(prop.getProperty("restore.replace", "true").equals("true"));
			jcheckboxResPageSize.setSelected(prop.getProperty("restore.setpagesize", "true").equals("true"));
			jtextfieldPageSize.setText(prop.getProperty("restore.newpagesize", ""));
		}
	}

	/**
	 * Save restore properties from file
	 */
	private void saveRestoreProperties() {
		File file = FirebirdManagerHelper.getPropertiesFile(true, globalPreferencesBean.getPropertiesFolder(), "frp", i18n.INFO_PROPFILE_RESTORE);
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
				prop.put("user", jtextfieldUsername.getText());
				prop.put("display.process", jcheckboxDisplayProcess.isSelected() ? "true" : "false");
				prop.put("restore.server", jtextfieldResDBHost.getText());
				prop.put("restore.port", jtextfieldResPort.getText());
				prop.put("restore.dbfile", jtextfieldResDBFile.getText());
				prop.put("restore.indexes", jcheckboxResDeactivateIndexes.isSelected() ? "true" : "false");
				prop.put("restore.shadows", jcheckboxResWithoutShadows.isSelected() ? "true" : "false");
				prop.put("restore.allspace", jcheckboxResUseAllSpace.isSelected() ? "true" : "false");
				prop.put("restore.onetable", jcheckboxResOneTableAtTime.isSelected() ? "true" : "false");
				prop.put("restore.validity", jcheckboxResWithoutValidity.isSelected() ? "true" : "false");
				prop.put("restore.replace", jcheckboxResReplaceDatabase.isSelected() ? "true" : "false");
				prop.put("restore.setpagesize", jcheckboxResPageSize.isSelected() ? "true" : "false");
				prop.put("restore.newpagesize", jtextfieldPageSize.getText());

				try {
					prop.store(new FileOutputStream(file),
							"FirebirdManager - Restore properties");
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
	 * Check for correct/missing input
	 * @return true = allright, false = error
	 */
	private boolean isBackupInputOK() {
		StringBuffer bufError = new StringBuffer();
		boolean focusSet = false;

		if (jtextfieldUsername.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_USERNAME_MISSING + CR);
			jtextfieldUsername.requestFocusInWindow();
			focusSet = true;
		}
		if (jtextfieldBckDBHost.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_SERVER_MISSING + CR);
			if (!focusSet) {
				jtextfieldBckDBHost.requestFocusInWindow();
				focusSet = true;
			}
		}
		if (jtextfieldBckPort.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_PORT_MISSING + CR);
			if (!focusSet) {
				jtextfieldBckPort.requestFocusInWindow();
				focusSet = true;
			}
		} else {
			try {
				Integer.parseInt(jtextfieldBckPort.getText());
			} catch (NumberFormatException e) {
				bufError.append(i18n.ERROR_PORT_NO_INTEGER + CR);
				if (!focusSet) {
					jtextfieldBckPort.requestFocusInWindow();
					focusSet = true;
				}
			}
		}
		if (jtextfieldBckDBFile.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_FILENAME_DATABASE_MISSING + CR);
			if (!focusSet) {
				jtextfieldBckDBFile.requestFocusInWindow();
				focusSet = true;
			}
		} else if (jtextfieldBckDBHost.getText().trim().equalsIgnoreCase("localhost")) {
			if (!FirebirdManagerHelper.fileExists(jtextfieldBckDBFile.getText())) {
				bufError.append(i18n.ERROR_FILENAME_NOT_EXISTS + " " 
						+ jtextfieldBckDBFile.getText() + " " + CR);
				if (!focusSet) {
					jtextfieldBckDBFile.requestFocusInWindow();
					focusSet = true;
				}
			}
		}
		if (jtextfieldBckBackupFile.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_FILENAME_BACKUP_MISSING + CR);
			if (!focusSet) {
				jtextfieldBckBackupFile.requestFocusInWindow();
				focusSet = true;
			}
		}

		
		if (bufError.length() != 0) {
			JOptionPane.showMessageDialog(_application.getMainFrame(), bufError.toString());
		}

		return bufError.length() == 0;
	}

	/**
	 * The backup working method
	 */
	private void backupDB() {
	    if (!isBackupInputOK())
	        return;
	    
	    int options = 0;
	    if (jcheckboxBckIgnoreChecksum.isSelected())
	    	options += FBBackupManager.BACKUP_IGNORE_CHECKSUMS;
	    if (jcheckboxBckMetadataOnly.isSelected())
	    	options += FBBackupManager.BACKUP_METADATA_ONLY;
	    if (jcheckboxBckOldMetadata.isSelected())
	    	options += FBBackupManager.BACKUP_OLD_DESCRIPTIONS;
	    if (jcheckboxBckIgnoreLimbo.isSelected())
	    	options += FBBackupManager.BACKUP_IGNORE_LIMBO;
	    if (jcheckboxBckGarbageCollection.isSelected())
	    	options += FBBackupManager.BACKUP_NO_GARBAGE_COLLECT;
	    if (jcheckboxBckConvertToTables.isSelected())
	    	options += FBBackupManager.BACKUP_CONVERT;
	    if (!jcheckboxBckTransportable.isSelected())
	    	options += FBBackupManager.BACKUP_NON_TRANSPORTABLE;
	    if (!jcheckboxBckNoDataCompression.isSelected())
	    	options += FBBackupManager.BACKUP_EXPAND;

	    FBBackupManager bckManager = new FBBackupManager();
	    bckManager.setBackupPath(jtextfieldBckBackupFile.getText());
	    bckManager.setDatabase(jtextfieldBckDBFile.getText());
	    bckManager.setHost(jtextfieldBckDBHost.getText());
	    bckManager.setPort(Integer.parseInt(jtextfieldBckPort.getText()));
	    bckManager.setUser(jtextfieldUsername.getText());
	    bckManager.setPassword(new String(jpasswordfieldPW.getPassword()));
	    
	    TextAreaOutputStream textOutputStream = new TextAreaOutputStream(jtextareaProcess, jscrollpaneProcess);
		bckManager.setLogger(textOutputStream);
		bckManager.setVerbose(jcheckboxDisplayProcess.isSelected());

		try {
			jtextareaProcess.setText("");
			if (jcheckboxBckMetadataOnly.isSelected()) {
				bckManager.backupMetadata();
			} else {
				bckManager.backupDatabase(options);
			}
			jtextareaProcess.append(i18n.INFO_BACKUP_COMPLETED);
			saveSessionPreferences();
		} catch (SQLException e) {
			jtextareaProcess.append(e.getLocalizedMessage()+ CR + i18n.INFO_BACKUP_CANCELED);
		}

		try {
			textOutputStream.close();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
	} 

	/**
	 * Check for correct/missing input
	 * @return true = allright, false = error
	 */
	private boolean isRestoreInputOK() {
		StringBuffer bufError = new StringBuffer();
		boolean focusSet = false;

		if (jtextfieldUsername.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_USERNAME_MISSING + CR);
			jtextfieldUsername.requestFocusInWindow();
			focusSet = true;
		}
		if (jtextfieldResDBHost.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_SERVER_MISSING + CR);
			if (!focusSet) {
				jtextfieldResDBHost.requestFocusInWindow();
				focusSet = true;
			}
		}
		if (jtextfieldResPort.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_PORT_MISSING + CR);
			if (!focusSet) {
				jtextfieldResPort.requestFocusInWindow();
				focusSet = true;
			}
		} else {
			try {
				Integer.parseInt(jtextfieldResPort.getText());
			} catch (NumberFormatException e) {
				bufError.append(i18n.ERROR_PORT_NO_INTEGER + CR);
				if (!focusSet) {
					jtextfieldResPort.requestFocusInWindow();
					focusSet = true;
				}
			}
		}
		if (jtextfieldResBackupFile.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_FILENAME_BACKUP_MISSING + CR);
			if (!focusSet) {
				jtextfieldResBackupFile.requestFocusInWindow();
				focusSet = true;
			}
		} else if (jtextfieldResDBHost.getText().trim().equalsIgnoreCase("localhost")) {
			if (!FirebirdManagerHelper.fileExists(jtextfieldResBackupFile.getText())) {
				bufError.append(i18n.ERROR_FILENAME_NOT_EXISTS + " " 
						+ jtextfieldResBackupFile.getText() + " " + CR);
				if (!focusSet) {
					jtextfieldResBackupFile.requestFocusInWindow();
					focusSet = true;
				}
			}
		}
		if (jtextfieldResDBFile.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_FILENAME_DATABASE_MISSING + CR);
			if (!focusSet) {
				jtextfieldResDBFile.requestFocusInWindow();
				focusSet = true;
			}
		} 

		if (bufError.length() != 0) {
			JOptionPane.showMessageDialog(_application.getMainFrame(), bufError.toString());
		}

		return bufError.length() == 0;
	}
	
	/**
	 * The restore working method
	 */
	private void restoreDB() {
	    if (!isRestoreInputOK())
	        return;
	    
	    int options = 0;
	    if (jcheckboxResDeactivateIndexes.isSelected())
	    	options += FBBackupManager.RESTORE_DEACTIVATE_INDEX;
	    if (jcheckboxResWithoutShadows.isSelected())
	    	options += FBBackupManager.RESTORE_NO_SHADOW;
	    if (jcheckboxResWithoutValidity.isSelected())
	    	options += FBBackupManager.RESTORE_NO_VALIDITY;
	    if (jcheckboxResOneTableAtTime.isSelected())
	    	options += FBBackupManager.RESTORE_ONE_AT_A_TIME;
	    if (jcheckboxResUseAllSpace.isSelected())
	    	options += FBBackupManager.RESTORE_USE_ALL_SPACE;
	    
	    FBBackupManager bckManager = new FBBackupManager();
	    if (jcheckboxResPageSize.isSelected())
	    	bckManager.setRestorePageSize(Integer.parseInt(jtextfieldPageSize.getText()));
    	bckManager.setRestoreReplace(jcheckboxResReplaceDatabase.isSelected());
    	
	    bckManager.setBackupPath(jtextfieldResBackupFile.getText());
	    bckManager.setDatabase(jtextfieldResDBFile.getText());
	    bckManager.setHost(jtextfieldResDBHost.getText());
	    bckManager.setPort(Integer.parseInt(jtextfieldResPort.getText()));
	    bckManager.setUser(jtextfieldUsername.getText());
	    bckManager.setPassword(new String(jpasswordfieldPW.getPassword()));

	    
	    TextAreaOutputStream textOutputStream = new TextAreaOutputStream(jtextareaProcess, jscrollpaneProcess);
		bckManager.setLogger(textOutputStream);
		bckManager.setVerbose(jcheckboxDisplayProcess.isSelected());

		try {
			jtextareaProcess.setText("");
			if (options == 0) {
				bckManager.restoreDatabase();
			} else {
				bckManager.restoreDatabase(options);
			}
			jtextareaProcess.append(i18n.INFO_RESTORE_COMPLETED);
		} catch (SQLException e) {
			jtextareaProcess.append(e.getLocalizedMessage()+ CR + i18n.INFO_RESTORE_CANCELED);
		}
		
		try {
			textOutputStream.close();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
	} 
	
	/**
	 * Create a backup filename from the database filename for the current timestamp
	 * <br>Old database filenames with extension <c>.gdb</c> gets the backup old extension <c>.gbk</c>
	 * <br>all other ones gets the new extension <c>.fbk</c>
	 */
	private void createBackupFilenameForDate() {
		StringBuffer bufFilename = new StringBuffer();
		String extension = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm");
		
		StringTokenizer st = new StringTokenizer(jtextfieldBckDBFile.getText(), ".");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (st.hasMoreTokens()) {
				bufFilename.append(token + "_" + df.format(new Date()) + ".");
			} else {
				extension = token;
			}
		}
		
		if (extension.equalsIgnoreCase("gdb")) {
			bufFilename.append("gbk");
		} else {
			bufFilename.append("fbk");
		}
			
		jtextfieldBckBackupFile.setText(bufFilename.toString());
	}

	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbuttonBckBackupFile) {
			selectFile(jtextfieldBckBackupFile);
		} else if (e.getSource() == jbuttonLoadBackupProp) {
			loadBackupProperties();
		} else if (e.getSource() == jbuttonSaveBackupProp) {
			saveBackupProperties();
		} else if (e.getSource() == jbuttonLoadRestoreProp) {
			loadRestoreProperties();
		} else if (e.getSource() == jbuttonSaveRestoreProp) {
			saveRestoreProperties();
		} else if (e.getSource() == jbuttonBckDBFile) {
			selectFile(jtextfieldBckDBFile);
		} else if (e.getSource() == jbuttonBackup) {
			backupDB();
		} else if (e.getSource() == btnCreateBackupFilename) {
			createBackupFilenameForDate();
		} else if (e.getSource() == jbuttonResBackupFile) {
			selectFile(jtextfieldResBackupFile);
		} else if (e.getSource() == jbuttonResDBFile) {
			selectFile(jtextfieldResDBFile);
		} else if (e.getSource() == btnResCopyBackupFilenameFromBackup) {
			jtextfieldResBackupFile.setText(jtextfieldBckBackupFile.getText());
		} else if (e.getSource() == btnResCopyDatabaseFilenameFromBackup) {
			jtextfieldResDBFile.setText(jtextfieldBckDBFile.getText());
		} else if (e.getSource() == jbuttonRestore) {
			restoreDB();
		} else if (e.getSource() == jcheckboxResPageSize) {
			jtextfieldPageSize.setEnabled(jcheckboxResPageSize.isSelected());
			jtextfieldPageSize.setBackground(jtextfieldPageSize.isEnabled() ? Color.WHITE : Color.LIGHT_GRAY);
		}
	}

	
	// ------------------------------------------------------------------------
	public void keyPressed(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == jtextfieldBckDBHost) {
			jbuttonBckDBFile.setEnabled(jtextfieldBckDBHost.getText().equalsIgnoreCase("localhost"));
			jbuttonBckBackupFile.setEnabled(jbuttonBckDBFile.isEnabled());
		} else if (e.getSource() == jtextfieldResDBHost) {
			jbuttonResDBFile.setEnabled(jtextfieldResDBHost.getText().equalsIgnoreCase("localhost"));
			jbuttonResBackupFile.setEnabled(jbuttonResDBFile.isEnabled());
		}
	}
	public void keyTyped(KeyEvent e) {
	}
}
