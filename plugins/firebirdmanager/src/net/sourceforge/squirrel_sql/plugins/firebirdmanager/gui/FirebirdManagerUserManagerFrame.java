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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.FirebirdManagerHelper;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.FirebirdManagerPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.FirebirdManagerUsersPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.PreferencesManager;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.IscSvcHandle;
import org.firebirdsql.management.FBUser;
import org.firebirdsql.management.FBUserManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class FirebirdManagerUserManagerFrame extends BaseInternalFrame 
implements IFirebirdManagerFrame, ActionListener, KeyListener, ListSelectionListener {
	private static final long serialVersionUID = 7592543018317340170L;

	private final String CR = System.getProperty("line.separator", "\n");
    private final int TABLE_USERS_COL_USERNAME = 0;
    private final int TABLE_USERS_COL_USERID = 1;
    private final int TABLE_USERS_COL_GROUPID = 2;
    private final int TABLE_USERS_COL_FIRSTNAME = 3;
    private final int TABLE_USERS_COL_MIDDLENAME = 4;
    private final int TABLE_USERS_COL_LASTNAME = 5;
    private final int UNIQUE_USER = 0;
    private final int USERNAME_EXISTS = 1;
    private final int USERID_EXISTS = 2;
	
	// variables
	// ========================================================================
	// non visible
	// ------------------------------------------------------------------------
	// Logger for this class
    private final static ILogger log = LoggerController.createLogger(FirebirdManagerUserManagerFrame.class);

	private static final StringManager stringManager = StringManagerFactory
			.getStringManager(FirebirdManagerUserManagerFrame.class);
	// global references
	private FirebirdManagerPreferenceBean firebirdPreferencesBean;
	// session preferences
	private FirebirdManagerUsersPreferenceBean sessionPreferencesBean = null;
	// display mode
	private int mode = FirebirdManagerHelper.DISPLAY_MODE;
	// read map with users
	private Map<String,FBUser> mapUsers = null;
	// usermanager of jaybird
	private FBUserManager fbUserManager = new FBUserManager();
	// handle of connected server
	private IscSvcHandle iscSvcHandle = null;
	// table data objects
    private Vector<Vector<Object>> vecTableUsers = null;
    private Vector<String> vecTableUsersHeader = null;


	// visible (gui)
	// ------------------------------------------------------------------------
	private JButton btnNew = new JButton();
	private JButton btnEdit = new JButton();
	private JButton btnSave = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnDelete = new JButton();
	private JTextField jtextfieldManagerUsername = new JTextField();
	private JLabel lblManagerUsername = new JLabel();
	private JLabel lblManagerPassword = new JLabel();
	private JLabel lblManagerTitle = new JLabel();
	private JLabel lblServer = new JLabel();
	private JTextField jtextfieldServer = new JTextField();
	private JLabel lblPort = new JLabel();
	private JCheckBox jcheckboxConnectToServer = new JCheckBox();
	private JTextField jtextfieldPort = new JTextField();
	private JPasswordField jpasswordfieldManager = new JPasswordField();
	private JTable jtableUsers = new JTable(new DefaultTableModel());
	private JLabel lblUsername = new JLabel();
	private JLabel lblPassword1 = new JLabel();
	private JLabel lblPassword2 = new JLabel();
	private JLabel lblFirstName = new JLabel();
	private JTextField jtextfieldUsername = new JTextField();
	private JLabel lblUserId = new JLabel();
	private JLabel lblGroupId = new JLabel();
	private JTextField jtextfieldUserId = new JTextField();
	private JTextField jtextfieldGroupId = new JTextField();
	private JTextField jtextfieldFirstName = new JTextField();
	private JPasswordField jpasswordField1 = new JPasswordField();
	private JPasswordField jpasswordField2 = new JPasswordField();
	private JLabel lblMiddleName = new JLabel();
	private JTextField jtextfieldMiddleName = new JTextField();
	private JLabel lblLastName = new JLabel();
	private JTextField jtextfieldLastName = new JTextField();
	

    /**
     * Constructor
     * @param app
     * @param rsrc
     * @param session
     * @param tab
     */
	public FirebirdManagerUserManagerFrame() {
		super("Firebird manager - " + stringManager.getString("usermanager.title"), true, true, true, true);
		
		initLayout();
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.moveToFront();
	}

	private interface i18n {
		// Labels
		String LBL_BTN_NEW = stringManager.getString("global.lbl.btn.new");
		String LBL_BTN_EDIT = stringManager.getString("global.lbl.btn.edit");
		String LBL_BTN_SAVE = stringManager.getString("global.lbl.btn.save");
		String LBL_BTN_CANCEL = stringManager.getString("global.lbl.btn.cancel");
		String LBL_BTN_DELETE = stringManager.getString("global.lbl.btn.delete");
		
		String LBL_MANAGER_TITLE = stringManager.getString("usermanager.lbl.manager.title");
		String LBL_MANAGER_SERVER = stringManager.getString("usermanager.lbl.manager.server");
		String LBL_MANAGER_PORT = stringManager.getString("usermanager.lbl.manager.port");
		String LBL_MANAGER_CONECTED = stringManager.getString("usermanager.lbl.manager.connected");
		String LBL_MANAGER_USERNAME = stringManager.getString("usermanager.lbl.manager.username");
		String LBL_MANAGER_PASSWORD = stringManager.getString("usermanager.lbl.manager.password");
		
		String LBL_USERS_USERNAME = stringManager.getString("usermanager.lbl.username");
		String LBL_USERS_USERID = stringManager.getString("usermanager.lbl.userid");
		String LBL_USERS_GROUPID = stringManager.getString("usermanager.lbl.groupid");
		String LBL_USERS_PASSWORD = stringManager.getString("usermanager.lbl.password");
		String LBL_USERS_PASSWORD_CONFIRM = stringManager.getString("usermanager.lbl.password.confirm");
		String LBL_USERS_FIRSTNAME = stringManager.getString("usermanager.lbl.firstname");
		String LBL_USERS_MIDDLENAME = stringManager.getString("usermanager.lbl.middlename");
		String LBL_USERS_LASTNAME = stringManager.getString("usermanager.lbl.lastname");
		
		// Tooltips and questions
		String TOOLTIP_USERNAME = stringManager.getString("usermanager.tooltip.username");
		String TOOLTIP_PASSWORD = stringManager.getString("usermanager.tooltip.password");

		// Questions
		String QUESTION_TITLE_DELETE_USER = stringManager.getString("rolemanager.question.title.delete.user");
		String QUESTION_DELETE_USER = stringManager.getString("usermanager.question.delete.user");

		// Errors
		String ERROR_CANNOT_CONNECT_SERVER = stringManager.getString("usermanager.error.cannot.connect.server");
		String ERROR_CANNOT_DISCONNECT_SERVER = stringManager.getString("usermanager.error.cannot.disconnect.server");
		String ERROR_USERNAME_MISSING = stringManager.getString("usermanager.error.username.missing");
		String ERROR_USERNAME_MAXLENGTH = stringManager.getString("usermanager.error.username.maxlength");
		String ERROR_PASSWORD_MISSING = stringManager.getString("usermanager.error.password.missing");
		String ERROR_PASSWORD_DIFFERENCES = stringManager.getString("usermanager.error.passwords.differences");
		String ERROR_PASSWORD_MAXLENGTH = stringManager.getString("usermanager.error.password.maxlength");
		String ERROR_NO_NUMBER = stringManager.getString("usermanager.error.no.number");
		
		String ERROR_USERNAME_EXISTS = stringManager.getString("usermanager.error.username.exists");
		String ERROR_USERID_EXISTS = stringManager.getString("usermanager.error.userid.exists");
		String ERROR_SAVE_USER = stringManager.getString("usermanager.error.save.user");
		String ERROR_DELETE_USER = stringManager.getString("usermanager.error.delete.user");
	}

	private void initLayout() {
		this.setLayout(new BorderLayout());
		this.add(createPanel(), BorderLayout.CENTER);
		
		initVisualObjects();
		readPreferences();
	}
	
	public void setFocusToFirstEmptyInputField() {
		if (jtextfieldServer.getText().length() == 0) {
			jtextfieldServer.requestFocusInWindow();
		} else if (jtextfieldPort.getText().length() == 0) {
			jtextfieldPort.requestFocusInWindow();
		} else if (jtextfieldManagerUsername.getText().length() == 0) {
			jtextfieldManagerUsername.requestFocusInWindow();
		} else {
			jpasswordfieldManager.requestFocusInWindow();
		}
	}
	
	private JPanel createPanel() {
	      JPanel jpanel1 = new JPanel();
	      FormLayout formlayout1 = new FormLayout("FILL:5DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE","CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:12DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE");
	      CellConstraints cc = new CellConstraints();
	      jpanel1.setLayout(formlayout1);

	      btnNew.setActionCommand("new");
	      btnNew.setName("btnNew");
	      btnNew.setText("new");
	      jpanel1.add(btnNew,cc.xy(2,4));

	      btnEdit.setActionCommand("edit");
	      btnEdit.setName("btnEdit");
	      btnEdit.setText("edit");
	      jpanel1.add(btnEdit,cc.xy(4,4));

	      btnSave.setActionCommand("save");
	      btnSave.setName("btnSave");
	      btnSave.setText("save");
	      jpanel1.add(btnSave,cc.xy(6,4));

	      btnCancel.setActionCommand("cancel");
	      btnCancel.setName("btnCancel");
	      btnCancel.setText("cancel");
	      jpanel1.add(btnCancel,cc.xy(8,4));

	      btnDelete.setActionCommand("delete");
	      btnDelete.setName("btnDelete");
	      btnDelete.setText("delete");
	      jpanel1.add(btnDelete,cc.xy(10,4));

	      jpanel1.add(createPanelConnectionData(),cc.xywh(2,2,11,1));
	      jpanel1.add(createPanelUserEdit(),cc.xywh(2,6,11,1));
	      return jpanel1;
	}
	
	public JPanel createPanelConnectionData() {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:30DLU:GROW(1.0),FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:30DLU:NONE,FILL:3DLU:GROW(1.0)",
				"CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		jtextfieldManagerUsername.setName("jtextfieldManagerUsername");
		jpanel1.add(jtextfieldManagerUsername, cc.xy(3, 5));

		lblManagerUsername.setName("lblManagerUsername");
		lblManagerUsername.setText("username");
		jpanel1.add(lblManagerUsername, cc.xy(1, 5));

		lblManagerPassword.setName("lblManagerPassword");
		lblManagerPassword.setText("password");
		jpanel1.add(lblManagerPassword, cc.xy(5, 5));

		lblManagerTitle.setName("lblManagerTitle");
		lblManagerTitle.setText("Connection parameter:");
		jpanel1.add(lblManagerTitle, cc.xywh(1, 1, 8, 1));

		lblServer.setName("lblServer");
		lblServer.setText("server");
		jpanel1.add(lblServer, cc.xy(1, 3));

		jtextfieldServer.setName("jtextfieldServer");
		jpanel1.add(jtextfieldServer, cc.xy(3, 3));

		lblPort.setName("lblPort");
		lblPort.setText("port");
		jpanel1.add(lblPort, cc.xy(5, 3));

		jtextfieldPort.setName("jtextfieldPort");
		jpanel1.add(jtextfieldPort, cc.xy(7, 3));

		jpasswordfieldManager.setName("jpasswordfieldManager");
		jpanel1.add(jpasswordfieldManager, cc.xywh(7, 5, 2, 1));

		jcheckboxConnectToServer.setActionCommand("connected to server");
		jcheckboxConnectToServer.setName("jcheckboxConnectToServer");
		jcheckboxConnectToServer.setText("connected to server");
		jcheckboxConnectToServer.setHorizontalAlignment(JCheckBox.RIGHT);
		jpanel1.add(jcheckboxConnectToServer, cc.xy(8, 3));

		return jpanel1;
	}
	

	public JPanel createPanelUserEdit() {
	      JPanel jpanel1 = new JPanel();
	      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:30DLU:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:30DLU:NONE,FILL:DEFAULT:GROW(1.0)","CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE");
	      CellConstraints cc = new CellConstraints();
	      jpanel1.setLayout(formlayout1);

	      jtableUsers.setName("jtableUsers");
	      JScrollPane jscrollpane1 = new JScrollPane();
	      jscrollpane1.setViewportView(jtableUsers);
	      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	      jpanel1.add(jscrollpane1,cc.xywh(1,1,8,1));

	      lblUsername.setName("lblUsername");
	      lblUsername.setText("Username");
	      jpanel1.add(lblUsername,cc.xy(1,3));

	      lblPassword1.setName("lblPassword1");
	      lblPassword1.setText("Password");
	      jpanel1.add(lblPassword1,cc.xy(1,7));

	      lblPassword2.setName("lblPassword2");
	      lblPassword2.setText("Confirm password");
	      jpanel1.add(lblPassword2,cc.xy(1,9));

	      lblFirstName.setName("lblFirstName");
	      lblFirstName.setText("Firstname");
	      jpanel1.add(lblFirstName,cc.xy(1,11));

	      lblMiddleName.setName("lblMiddleName");
	      lblMiddleName.setText("Middle name");
	      jpanel1.add(lblMiddleName,cc.xy(1,13));

	      lblLastName.setName("lblLastName");
	      lblLastName.setText("Last name");
	      jpanel1.add(lblLastName,cc.xy(1,15));

	      jtextfieldUsername.setName("jtextfieldUsername");
	      jpanel1.add(jtextfieldUsername,cc.xywh(3,3,6,1));

	      jpasswordField1.setName("jpasswordField1");
	      jpanel1.add(jpasswordField1,cc.xywh(3,7,6,1));

	      jpasswordField2.setName("jpasswordField2");
	      jpanel1.add(jpasswordField2,cc.xywh(3,9,6,1));

	      jtextfieldFirstName.setName("jtextfieldFirstName");
	      jpanel1.add(jtextfieldFirstName,cc.xywh(3,11,6,1));

	      jtextfieldMiddleName.setName("jtextfieldMiddleName");
	      jpanel1.add(jtextfieldMiddleName,cc.xywh(3,13,6,1));

	      jtextfieldLastName.setName("jtextfieldLastName");
	      jpanel1.add(jtextfieldLastName,cc.xywh(3,15,6,1));

	      lblUserId.setName("lblUserId");
	      lblUserId.setText("user id");
	      jpanel1.add(lblUserId,cc.xy(1,5));

	      lblGroupId.setName("lblGroupId");
	      lblGroupId.setText("group id");
	      jpanel1.add(lblGroupId,cc.xy(5,5));

	      jtextfieldUserId.setName("jtextfieldUserId");
	      jpanel1.add(jtextfieldUserId,cc.xy(3,5));

	      jtextfieldGroupId.setName("jtextfieldGroupId");
	      jpanel1.add(jtextfieldGroupId,cc.xy(7,5));

	      return jpanel1;
	}

	
	
	
	
	private void initVisualObjects() {
		btnNew.setText(i18n.LBL_BTN_NEW);
		btnNew.setIcon(FirebirdManagerHelper.loadIcon("new22x22.png"));
		btnNew.addActionListener(this);
		btnEdit.setText(i18n.LBL_BTN_EDIT);
		btnEdit.setIcon(FirebirdManagerHelper.loadIcon("edit22x22.png"));
		btnEdit.addActionListener(this);
		btnSave.setText(i18n.LBL_BTN_SAVE);
		btnSave.setIcon(FirebirdManagerHelper.loadIcon("save22x22.png"));
		btnSave.addActionListener(this);
		btnCancel.setText(i18n.LBL_BTN_CANCEL);
		btnCancel.setIcon(FirebirdManagerHelper.loadIcon("cancel22x22.png"));
		btnCancel.addActionListener(this);
		btnDelete.setText(i18n.LBL_BTN_DELETE);
		btnDelete.setIcon(FirebirdManagerHelper.loadIcon("delete22x22.png"));
		btnDelete.addActionListener(this);
		
		lblManagerTitle.setText(i18n.LBL_MANAGER_TITLE);
		lblServer.setText(i18n.LBL_MANAGER_SERVER);
		lblPort.setText(i18n.LBL_MANAGER_PORT);
		jcheckboxConnectToServer.setText(i18n.LBL_MANAGER_CONECTED);
		jcheckboxConnectToServer.addActionListener(this);
		lblManagerUsername.setText(i18n.LBL_MANAGER_USERNAME);
		lblManagerPassword.setText(i18n.LBL_MANAGER_PASSWORD);
		jpasswordfieldManager.addKeyListener(this);

		jtableUsers.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jtableUsers.getSelectionModel().addListSelectionListener(this);
		
		lblUsername.setText(i18n.LBL_USERS_USERNAME);
		jtextfieldUsername.setToolTipText(i18n.TOOLTIP_USERNAME);
		lblUserId.setText(i18n.LBL_USERS_USERID);
		lblGroupId.setText(i18n.LBL_USERS_GROUPID);
		lblPassword1.setText(i18n.LBL_USERS_PASSWORD);
		jpasswordField1.setToolTipText(i18n.TOOLTIP_PASSWORD);
		lblPassword2.setText(i18n.LBL_USERS_PASSWORD_CONFIRM);
		jpasswordField2.setToolTipText(i18n.TOOLTIP_PASSWORD);
		lblFirstName.setText(i18n.LBL_USERS_FIRSTNAME);
		lblMiddleName.setText(i18n.LBL_USERS_MIDDLENAME);
		lblLastName.setText(i18n.LBL_USERS_LASTNAME);
		
		controlComponents(FirebirdManagerHelper.DISPLAY_MODE);
	}  
	
	/**
	 * Set input fields with the last saved session preferences when exists
	 * <br>or otherwise with the global preferences
	 */
	private void readPreferences() {
		firebirdPreferencesBean = PreferencesManager.getGlobalPreferences();
		jtextfieldServer.setText(firebirdPreferencesBean.getServer());
		jtextfieldPort.setText(firebirdPreferencesBean.getPort());
		jtextfieldManagerUsername.setText(firebirdPreferencesBean.getUser());
		
		// preferences from the last session
		sessionPreferencesBean = (FirebirdManagerUsersPreferenceBean)PreferencesManager.loadPreferences(PreferencesManager.PREFERENCES_BEAN_USERS);
		if (sessionPreferencesBean.getUser().length() > 0)
			jtextfieldManagerUsername.setText(sessionPreferencesBean.getUser());
		if (sessionPreferencesBean.getServer().length() > 0)
			jtextfieldServer.setText(sessionPreferencesBean.getServer());
		if (sessionPreferencesBean.getPort().length() > 0)
			jtextfieldPort.setText(sessionPreferencesBean.getPort());
	}
	
	/**
	 * Save the session preferences
	 */
	private void saveSessionPreferences() {
		sessionPreferencesBean.setUser(jtextfieldManagerUsername.getText());
		sessionPreferencesBean.setServer(jtextfieldServer.getText());
		sessionPreferencesBean.setPort(jtextfieldPort.getText());
		
		PreferencesManager.savePreferences(sessionPreferencesBean, PreferencesManager.PREFERENCES_BEAN_USERS);
	}

	@SuppressWarnings("unchecked")
	private void readUsers() {
		
		try {
			mapUsers = (Map<String,FBUser>)fbUserManager.getUsers();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}

		Collection<FBUser> listUsers = (Collection<FBUser>)mapUsers.values();
		vecTableUsers = new Vector<Vector<Object>>();
		Vector<Object> vecRow = null;
		for (Iterator iter = listUsers.iterator(); iter.hasNext();) {
			FBUser fbUser = (FBUser) iter.next();
			vecRow = new Vector<Object>();
			vecRow.add(fbUser.getUserName());
			vecRow.add(fbUser.getUserId());
			vecRow.add(fbUser.getGroupId());
			vecRow.add(fbUser.getFirstName());
			vecRow.add(fbUser.getMiddleName());
			vecRow.add(fbUser.getLastName());
			vecTableUsers.add(vecRow);
		}
		
		if (vecTableUsersHeader == null) {
			vecTableUsersHeader = new Vector<String>();
			vecTableUsersHeader.add(i18n.LBL_USERS_USERNAME);
			vecTableUsersHeader.add(i18n.LBL_USERS_USERID);
			vecTableUsersHeader.add(i18n.LBL_USERS_GROUPID);
			vecTableUsersHeader.add(i18n.LBL_USERS_FIRSTNAME);
			vecTableUsersHeader.add(i18n.LBL_USERS_MIDDLENAME);
			vecTableUsersHeader.add(i18n.LBL_USERS_LASTNAME);
		}

		((DefaultTableModel)jtableUsers.getModel()).setDataVector(vecTableUsers, vecTableUsersHeader);
	}
	
	
	private void selectUser() {
		int row = jtableUsers.getSelectedRow();
		
		if (row > -1) {
			controlComponents(mode);
			jtextfieldUsername.setText((String)jtableUsers.getValueAt(row, TABLE_USERS_COL_USERNAME));
			jtextfieldUserId.setText("" + ((Integer)jtableUsers.getValueAt(row, TABLE_USERS_COL_USERID)).intValue());
			jtextfieldGroupId.setText("" + ((Integer)jtableUsers.getValueAt(row, TABLE_USERS_COL_GROUPID)).intValue());
			jtextfieldFirstName.setText((String)jtableUsers.getValueAt(row, TABLE_USERS_COL_FIRSTNAME));
			jtextfieldMiddleName.setText((String)jtableUsers.getValueAt(row, TABLE_USERS_COL_MIDDLENAME));
			jtextfieldLastName.setText((String)jtableUsers.getValueAt(row, TABLE_USERS_COL_LASTNAME));
		} else {
			initUserFields();
		}
	}
	
	private void initUserFields() {
		jtextfieldUsername.setText("");
		jtextfieldUserId.setText("0");
		jtextfieldGroupId.setText("0");
		jtextfieldFirstName.setText("");
		jtextfieldMiddleName.setText("");
		jtextfieldLastName.setText("");
		jpasswordField1.setText("");
		jpasswordField2.setText("");
	}
	

	// controlling
	// ------------------------------------------------------------------------
	private void controlComponents(int mode) {
		controlButton(mode);
		controlFields(mode);
		
		this.mode = mode;
	}

	private void controlButton(int mode) {
		int rowUser = jtableUsers.getSelectedRow();
		boolean sysdba = rowUser > -1
			&& ((String)jtableUsers.getValueAt(rowUser, TABLE_USERS_COL_USERNAME)).equalsIgnoreCase("SYSDBA");

		btnNew.setEnabled(jcheckboxConnectToServer.isSelected()
				&& mode == FirebirdManagerHelper.DISPLAY_MODE);
		btnEdit.setEnabled(jcheckboxConnectToServer.isSelected()
				&& mode == FirebirdManagerHelper.DISPLAY_MODE
				&& rowUser > -1);
		btnSave.setEnabled(jcheckboxConnectToServer.isSelected()
				&& mode != FirebirdManagerHelper.DISPLAY_MODE);
		btnCancel.setEnabled(btnSave.isEnabled());
		btnDelete.setEnabled(btnEdit.isEnabled()
				&& !sysdba);
	}

	private void controlFields(int mode) {
		boolean disconnected = iscSvcHandle == null;
		jtextfieldServer.setEnabled(disconnected);
		jtextfieldPort.setEnabled(disconnected);
		jtextfieldManagerUsername.setEnabled(disconnected);
		jpasswordfieldManager.setEnabled(disconnected);

		jtableUsers.setEnabled(mode == FirebirdManagerHelper.DISPLAY_MODE);

		jtextfieldUsername.setEnabled(mode == FirebirdManagerHelper.NEW_MODE);
		jtextfieldUserId.setEnabled(jtextfieldUsername.isEnabled());
		jtextfieldGroupId
				.setEnabled(mode != FirebirdManagerHelper.DISPLAY_MODE);
		jtextfieldFirstName.setEnabled(jtextfieldGroupId.isEnabled());
		jtextfieldMiddleName.setEnabled(jtextfieldGroupId.isEnabled());
		jtextfieldLastName.setEnabled(jtextfieldGroupId.isEnabled());
		jpasswordField1.setEnabled(jtextfieldGroupId.isEnabled());
		jpasswordField2.setEnabled(jtextfieldGroupId.isEnabled());
	}


	private void connectToServer() {
		if (iscSvcHandle == null) {
			fbUserManager.setHost(jtextfieldServer.getText());
			fbUserManager.setPort(FirebirdManagerHelper.convertStringToIntDef(jtextfieldPort.getText(), 3050));
			fbUserManager.setUser(jtextfieldManagerUsername.getText());
			fbUserManager.setPassword(new String(jpasswordfieldManager.getPassword()));
			
			try {
				iscSvcHandle = fbUserManager.attachServiceManager(fbUserManager.getGds());
				readUsers();
				controlComponents(mode);
				saveSessionPreferences();
			} catch (GDSException e) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_CANNOT_CONNECT_SERVER);
				log.error(i18n.ERROR_CANNOT_CONNECT_SERVER);
				jcheckboxConnectToServer.setSelected(false);
			}
		}
	}
	
	
	private void disconnectFromServer() {
		if (iscSvcHandle != null) {
			try {
				fbUserManager.detachServiceManager(fbUserManager.getGds(), iscSvcHandle);
				iscSvcHandle = null;
				// init user list
				vecTableUsers = new Vector<Vector<Object>>();
				((DefaultTableModel)jtableUsers.getModel()).setDataVector(vecTableUsers, vecTableUsersHeader);
				controlComponents(mode);
			} catch (GDSException e) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_CANNOT_DISCONNECT_SERVER);
				log.error(i18n.ERROR_CANNOT_DISCONNECT_SERVER);
				jcheckboxConnectToServer.setSelected(true);
			}
		}
	}

	
	// user checks
	// ------------------------------------------------------------------------
	private boolean isInputOK() {
		StringBuffer bufError = new StringBuffer();
		boolean focusSet = false;
		
		if (jtextfieldUsername.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_USERNAME_MISSING + CR);
			jtextfieldUsername.requestFocusInWindow();
			focusSet = true;
		} else if (jtextfieldUsername.getText().trim().length() > 31) {
			bufError.append(i18n.ERROR_USERNAME_MAXLENGTH + CR);
			if (!focusSet) {
				jtextfieldUsername.requestFocusInWindow();
				focusSet = true;
			}
		}
		if (jtextfieldUserId.getText().length() > 0) {
			try {
				Integer.parseInt(jtextfieldUserId.getText());
			} catch (NumberFormatException e) {
				bufError.append(i18n.ERROR_NO_NUMBER + CR);
				if (!focusSet) {
					jtextfieldUserId.requestFocusInWindow();
					focusSet = true;
				}
			}
		}
		if (jtextfieldGroupId.getText().length() > 0) {
			try {
				Integer.parseInt(jtextfieldGroupId.getText());
			} catch (NumberFormatException e) {
				bufError.append(i18n.ERROR_NO_NUMBER + CR);
				if (!focusSet) {
					jtextfieldGroupId.requestFocusInWindow();
					focusSet = true;
				}
			}
		}
		boolean pwRequired = mode == FirebirdManagerHelper.NEW_MODE
			|| jpasswordField1.getPassword().length > 0
			|| jpasswordField2.getPassword().length > 0;
			
		if (pwRequired) {
			if (jpasswordField1.getPassword().length == 0) {
				bufError.append(i18n.ERROR_PASSWORD_MISSING + CR);
				if (!focusSet) {
					jpasswordField1.requestFocusInWindow();
					focusSet = true;
				}
			} else {
				if (jpasswordField2.getPassword().length == 0) {
					bufError.append(i18n.ERROR_PASSWORD_MISSING + CR);
					if (!focusSet) {
						jpasswordField2.requestFocusInWindow();
						focusSet = true;
					}
				} else {
					if (!new String(jpasswordField1.getPassword()).equals(new String(jpasswordField2.getPassword()))) {
						bufError.append(i18n.ERROR_PASSWORD_DIFFERENCES + CR);
						if (!focusSet) {
							jpasswordField1.requestFocusInWindow();
							focusSet = true;
						}
					} else if (new String(jpasswordField1.getPassword()).length() > 31) {
						bufError.append(i18n.ERROR_PASSWORD_MAXLENGTH + CR);
						if (!focusSet) {
							jpasswordField1.requestFocusInWindow();
							focusSet = true;
						}
					}
				}
			}
		}
		
		if (mode == FirebirdManagerHelper.NEW_MODE) {
			int uniqueUser = uniqueUser(); 
			if (uniqueUser > UNIQUE_USER) {
				if (uniqueUser == USERNAME_EXISTS) {
					bufError.append(i18n.ERROR_USERNAME_EXISTS + CR);
				} else if (uniqueUser == USERID_EXISTS) {
					bufError.append(i18n.ERROR_USERID_EXISTS + CR);
				}
				if (!focusSet) {
					jtextfieldUsername.requestFocusInWindow();
					focusSet = true;
				}
			}
		}
		
		if (bufError.length() != 0) {
			JOptionPane.showMessageDialog(this, bufError.toString());
		}
		
		return bufError.length() == 0;
	}

	
	private int uniqueUser() {
		int userId = FirebirdManagerHelper.convertStringToIntDef(jtextfieldUserId.getText(), -1);
		for (int i = 0; i < jtableUsers.getRowCount(); i++) {
			if (jtextfieldUsername.getText().equalsIgnoreCase((String)jtableUsers.getValueAt(i, TABLE_USERS_COL_USERNAME))) {
				return USERNAME_EXISTS;
			} else if (userId > 0
					&& userId == ((Integer)jtableUsers.getValueAt(i, TABLE_USERS_COL_USERID)).intValue()) {
				return USERID_EXISTS;
			}
		}
		return UNIQUE_USER;
	}
	

	private FBUser fillUser() {
		FBUser fbUser = new FBUser();
		fbUser.setUserName(jtextfieldUsername.getText().trim());
		fbUser.setUserId(FirebirdManagerHelper.convertStringToIntDef(jtextfieldUserId.getText(), 0));
		fbUser.setGroupId(FirebirdManagerHelper.convertStringToIntDef(jtextfieldGroupId.getText(), 0));
		if (mode == FirebirdManagerHelper.NEW_MODE
				|| new String(jpasswordField1.getPassword()).trim().length() > 0) {
			fbUser.setPassword(new String(jpasswordField1.getPassword()).trim());
		}
		fbUser.setFirstName(jtextfieldFirstName.getText());
		fbUser.setMiddleName(jtextfieldMiddleName.getText());
		fbUser.setLastName(jtextfieldLastName.getText());
		
		return fbUser;
	}
	
	private boolean saveData() {
		if (isInputOK()) {
			try {
				if (mode == FirebirdManagerHelper.NEW_MODE) {
					fbUserManager.add(fillUser());
				} else {
					fbUserManager.update(fillUser());
				}
				readUsers();
				return true;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_SAVE_USER);
				log.error(i18n.ERROR_SAVE_USER + CR + e.getLocalizedMessage());
			}
		}
		
		return false;
	}
	
	private void deleteUser() {
		int row = jtableUsers.getSelectedRow();
		if (row > -1
				&& JOptionPane.showConfirmDialog(null, i18n.QUESTION_DELETE_USER, i18n.QUESTION_TITLE_DELETE_USER, 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
			FBUser fbUser = mapUsers.get(jtextfieldUsername.getText());
			try {
				fbUserManager.delete(fbUser);
				readUsers();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_DELETE_USER);
				log.error(i18n.ERROR_DELETE_USER + CR + e.getLocalizedMessage());
			}
		}
	}
	
	
	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnNew) {
			initUserFields();
			controlComponents(FirebirdManagerHelper.NEW_MODE);
			jtextfieldUsername.requestFocusInWindow();
		} else if (e.getSource() == btnEdit) {
			controlComponents(FirebirdManagerHelper.EDIT_MODE);
			jtextfieldFirstName.requestFocusInWindow();
		} else if (e.getSource() == btnSave) {
			if (saveData()) {
				controlComponents(FirebirdManagerHelper.DISPLAY_MODE);
			}
		} else if (e.getSource() == btnCancel) {
			selectUser();
			controlComponents(FirebirdManagerHelper.DISPLAY_MODE);
		} else if (e.getSource() == btnDelete) {
			deleteUser();
		} else if (e.getSource() == jcheckboxConnectToServer) {
			if (jcheckboxConnectToServer.isSelected()) {
				connectToServer();
			} else {
				disconnectFromServer();
			}
		}
	}
	
	
	// ------------------------------------------------------------------------
    public void keyTyped(KeyEvent e) {
    }
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        	if (e.getSource() == jpasswordfieldManager
        			&& !jcheckboxConnectToServer.isSelected()) {
        		jcheckboxConnectToServer.doClick();
        	}
        }
    }
    public void keyReleased(KeyEvent e) {
    }


	// ------------------------------------------------------------------------
    public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()
				&& e.getSource() == jtableUsers.getSelectionModel()) {
			selectUser();
		}
	}

}
