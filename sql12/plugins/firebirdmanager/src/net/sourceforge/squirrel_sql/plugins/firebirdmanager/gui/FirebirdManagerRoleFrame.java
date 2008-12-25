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
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.FirebirdManagerHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class FirebirdManagerRoleFrame extends DialogWidget
implements IFirebirdManagerFrame, ActionListener, KeyListener, ListSelectionListener {
	private static final long serialVersionUID = 6138993610321442456L;

	private final String CR = System.getProperty("line.separator", "\n");
	private final int TABLE_COL_ROLENAME = 0;
	
	// variables
	// ========================================================================
	// non visible
	// ------------------------------------------------------------------------
	// Logger for this class
    private final static ILogger log = LoggerController.createLogger(FirebirdManagerRoleFrame.class);

	private final static StringManager stringManager = StringManagerFactory
			.getStringManager(FirebirdManagerRoleFrame.class);
	private ISession session;
    private final int UNIQUE_ROLE = 0;
    private final int ROLENAME_EXISTS = 1;
	// display mode
	private int mode = FirebirdManagerHelper.DISPLAY_MODE;
	// table data objects
    private Vector<Vector<Object>> vecTableRoles = null;
    private Vector<String> vecTableRolesHeader = null;

	// visible (gui)
	// ------------------------------------------------------------------------
	private JButton btnNew = new JButton();
	private JButton btnSave = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnDelete = new JButton();
	private JTable jtableRoles = new JTable();
	private JLabel lblRolename = new JLabel();
	private JTextField jtextfieldRolename = new JTextField();


    /**
     * Constructor
     * @param app
     * @param rsrc
     * @param session
     * @param tab
     */
	public FirebirdManagerRoleFrame(ISession session) {
		super("Firebird manager - " + stringManager.getString("rolemanager.title"), true, true, true, true, session.getApplication());
		this.session = session;
		
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

		String LBL_ROLENAME = stringManager.getString("rolemanager.lbl.rolename");
		
		String LBL_OWNERNAME = stringManager.getString("rolemanager.lbl.ownername");
		
		// Tooltips and questions
		String TOOLTIP_ROLENAME = stringManager.getString("rolemanager.tooltip.rolename");

		// Questions
		String QUESTION_TITLE_DELETE_ROLE = stringManager.getString("rolemanager.question.title.delete.role");
		String QUESTION_DELETE_ROLE = stringManager.getString("rolemanager.question.delete.role");

		// Errors
		String ERROR_GLOBAL_CLOSE_STATEMENT = stringManager.getString("global.error.close.statement");
		
		String ERROR_ROLENAME_MISSING = stringManager.getString("rolemanager.error.rolename.missing");
		String ERROR_ROLENAME_MAXLENGTH = stringManager.getString("rolemanager.error.rolename.maxlength");
		String ERROR_ROLENAME_EXISTS = stringManager.getString("rolemanager.error.rolename.exists");
		String ERROR_READ_ROLES = stringManager.getString("rolemanager.error.read.roles");
		String ERROR_SAVE_ROLE = stringManager.getString("rolemanager.error.save.role");
		String ERROR_DELETE_ROLE = stringManager.getString("rolemanager.error.delete.role");
	}

	private void initLayout() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(createPanel());

		initVisualObjects();
		readRoles();
	}

	public JPanel createPanel() {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:5DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE",
				"CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:5DLU:NONE,FILL:DEFAULT:NONE,CENTER:5DLU:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		btnNew.setActionCommand("new");
		btnNew.setName("btnNew");
		btnNew.setText("new");
		jpanel1.add(btnNew, cc.xy(2, 2));

		btnSave.setActionCommand("save");
		btnSave.setName("btnSave");
		btnSave.setText("save");
		jpanel1.add(btnSave, cc.xy(4, 2));

		btnCancel.setActionCommand("cancel");
		btnCancel.setName("btnCancel");
		btnCancel.setText("cancel");
		jpanel1.add(btnCancel, cc.xy(6, 2));

		btnDelete.setActionCommand("delete");
		btnDelete.setName("btnDelete");
		btnDelete.setText("delete");
		jpanel1.add(btnDelete, cc.xy(8, 2));

		jtableRoles.setName("jtableRoles");
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(jtableRoles);
		jscrollpane1
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane1, cc.xywh(2, 4, 11, 1));

		lblRolename.setName("lblRolename");
		lblRolename.setText("rolename");
		jpanel1.add(lblRolename, cc.xy(2, 6));

		jtextfieldRolename.setName("jtextfieldRolename");
		jpanel1.add(jtextfieldRolename, cc.xywh(4, 6, 9, 1));

		return jpanel1;
	}

	public void setFocusToFirstEmptyInputField() {
		// nothing to do
	}
	
	private void initVisualObjects() {
		btnNew.setText(i18n.LBL_BTN_NEW);
		btnNew.setIcon(FirebirdManagerHelper.loadIcon("new22x22.png"));
		btnNew.addActionListener(this);
		btnSave.setText(i18n.LBL_BTN_SAVE);
		btnSave.setIcon(FirebirdManagerHelper.loadIcon("save22x22.png"));
		btnSave.addActionListener(this);
		btnCancel.setText(i18n.LBL_BTN_CANCEL);
		btnCancel.setIcon(FirebirdManagerHelper.loadIcon("cancel22x22.png"));
		btnCancel.addActionListener(this);
		btnDelete.setText(i18n.LBL_BTN_DELETE);
		btnDelete.setIcon(FirebirdManagerHelper.loadIcon("delete22x22.png"));
		btnDelete.addActionListener(this);
		
		jtableRoles.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jtableRoles.getSelectionModel().addListSelectionListener(this);
		
		jtextfieldRolename.setToolTipText(i18n.TOOLTIP_ROLENAME);
		jtextfieldRolename.addKeyListener(this);
		
		controlComponents(FirebirdManagerHelper.DISPLAY_MODE);
	}  

	@SuppressWarnings("all")
	private void readRoles() {
		Statement stmt = null;
		vecTableRoles = new Vector<Vector<Object>>();
		Vector<Object> vecRow = null;
		
		try {
			String sql = "Select RDB$ROLE_NAME, RDB$OWNER_NAME From RDB$ROLES Order By RDB$ROLE_NAME";
			stmt = session.getSQLConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				vecRow = new Vector<Object>();
				vecRow.add(rs.getString("RDB$ROLE_NAME"));
				vecRow.add(rs.getString("RDB$OWNER_NAME"));
				vecTableRoles.add(vecRow);
			}
			
			rs.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, i18n.ERROR_READ_ROLES);
			log.error(i18n.ERROR_READ_ROLES + CR + e.getLocalizedMessage());
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error(i18n.ERROR_GLOBAL_CLOSE_STATEMENT + CR + e.getLocalizedMessage());
				}
			}
		}
		
		if (vecTableRolesHeader == null) {
			vecTableRolesHeader = new Vector<String>();
			vecTableRolesHeader.add(i18n.LBL_ROLENAME);
			vecTableRolesHeader.add(i18n.LBL_OWNERNAME);
		}

		((DefaultTableModel)jtableRoles.getModel()).setDataVector(vecTableRoles, vecTableRolesHeader);
	}
	
	private void selectRole() {
		int row = jtableRoles.getSelectedRow();
		
		if (row > -1) {
			controlComponents(mode);
			jtextfieldRolename.setText((String)jtableRoles.getValueAt(row, TABLE_COL_ROLENAME));
		} else {
			jtextfieldRolename.setText("");
		}
	}

	// controlling
	// ------------------------------------------------------------------------
	private void controlComponents(int mode) {
		int rowUser = jtableRoles.getSelectedRow();
		btnNew.setEnabled(mode == FirebirdManagerHelper.DISPLAY_MODE);
		btnSave.setEnabled(mode != FirebirdManagerHelper.DISPLAY_MODE);
		btnCancel.setEnabled(btnSave.isEnabled());
		btnDelete.setEnabled(mode == FirebirdManagerHelper.DISPLAY_MODE
				&& rowUser > -1);

		jtextfieldRolename.setEnabled(btnSave.isEnabled());
		
		this.mode = mode;
	}

	// user checks
	// ------------------------------------------------------------------------
	private boolean isInputOK() {
		StringBuffer bufError = new StringBuffer();
		
		if (jtextfieldRolename.getText().trim().length() == 0) {
			bufError.append(i18n.ERROR_ROLENAME_MISSING + CR);
		} else if (jtextfieldRolename.getText().trim().length() > 31) {
			bufError.append(i18n.ERROR_ROLENAME_MAXLENGTH + CR);
		}
		
		if (mode == FirebirdManagerHelper.NEW_MODE) {
			int uniqueRole = uniqueRole(); 
			if (uniqueRole > UNIQUE_ROLE) {
				if (uniqueRole == ROLENAME_EXISTS) {
					bufError.append(i18n.ERROR_ROLENAME_EXISTS + CR);
				}
			}
		}
		
		if (bufError.length() != 0) {
			jtextfieldRolename.requestFocusInWindow();
			JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), bufError.toString());
		}
		
		return bufError.length() == 0;
	}
	
	private int uniqueRole() {
		String rolename = jtextfieldRolename.getText().trim();
		for (int i = 0; i < jtableRoles.getRowCount(); i++) {
			if (rolename.equalsIgnoreCase(((String)jtableRoles.getValueAt(i, TABLE_COL_ROLENAME)).trim())) {
				return ROLENAME_EXISTS;
			}
		}
		return UNIQUE_ROLE;
	}
	

	private boolean saveData() {
		if (isInputOK()) {
			Statement stmt = null;
			try {
				String sql = "Create ROLE " + jtextfieldRolename.getText().trim();
				stmt = session.getSQLConnection().createStatement();
				stmt.execute(sql);
				readRoles();
				return true;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_SAVE_ROLE);
				log.error(i18n.ERROR_SAVE_ROLE + CR + e.getLocalizedMessage());
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						log.error(i18n.ERROR_GLOBAL_CLOSE_STATEMENT + CR + e.getLocalizedMessage());
					}
				}
			}
		}
		
		return false;
	}
	
	private void deleteRole() {
		int row = jtableRoles.getSelectedRow();
		if (row > -1
				&& JOptionPane.showConfirmDialog(null, i18n.QUESTION_DELETE_ROLE, i18n.QUESTION_TITLE_DELETE_ROLE, 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
			Statement stmt = null;
			try {
				String sql = "Drop ROLE " + jtextfieldRolename.getText().trim();
				stmt = session.getSQLConnection().createStatement();
				stmt.execute(sql);
				readRoles();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_DELETE_ROLE);
				log.error(i18n.ERROR_DELETE_ROLE + CR + e.getLocalizedMessage());
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						log.error(i18n.ERROR_GLOBAL_CLOSE_STATEMENT + CR + e.getLocalizedMessage());
					}
				}
			}
		}
	}
	
	
	
	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnNew) {
			jtextfieldRolename.setText("");
			controlComponents(FirebirdManagerHelper.NEW_MODE);
			jtextfieldRolename.requestFocusInWindow();
		} else if (e.getSource() == btnSave) {
			if (saveData()) {
				controlComponents(FirebirdManagerHelper.DISPLAY_MODE);
			}
		} else if (e.getSource() == btnCancel) {
			selectRole();
			controlComponents(FirebirdManagerHelper.DISPLAY_MODE);
		} else if (e.getSource() == btnDelete) {
			deleteRole();
			controlComponents(FirebirdManagerHelper.DISPLAY_MODE);
		}
	}
	
	// ------------------------------------------------------------------------
    public void keyTyped(KeyEvent e) {
    }
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        	if (e.getSource() == jtextfieldRolename) {
        		btnSave.doClick();
        	}
        }
    }
    public void keyReleased(KeyEvent e) {
    }
	
	// ------------------------------------------------------------------------
    public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()
				&& e.getSource() == jtableRoles.getSelectionModel()) {
			selectRole();
		}
	}
}
