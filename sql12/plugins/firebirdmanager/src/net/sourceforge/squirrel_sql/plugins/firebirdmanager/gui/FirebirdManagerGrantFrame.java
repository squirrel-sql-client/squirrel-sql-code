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
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.FirebirdManagerDataAccess;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.FirebirdManagerHelper;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FirebirdManagerGrantFrame extends DialogWidget implements IFirebirdManagerFrame,
	ActionListener, KeyListener, MouseListener
{
	private static final long serialVersionUID = 7592543018317340170L;

	private final String CR = System.getProperty("line.separator", "\n");

	private final int DISPLAY_TYPE_TABLE = 0;

	private final int DISPLAY_TYPE_VIEW = 1;

	private final int DISPLAY_TYPE_PROCEDURE = 2;

	private final int DISPLAY_TYPE_ROLE = 3;

	private final int TABLES_COL_SELECT = 2;

	private final int TABLES_COL_INSERT = 3;

	private final int TABLES_COL_UPDATE = 4;

	private final int TABLES_COL_DELETE = 5;

	private final int TABLES_COL_REFERENCE = 6;

	public final static int TREENODE_TYPE_ROOT = 0;

	public final static int TREENODE_TYPE_USER_GROUP = 1;

	public final static int TREENODE_TYPE_PROCEDURE_GROUP = 2;

	public final static int TREENODE_TYPE_VIEW_GROUP = 3;

	public final static int TREENODE_TYPE_TRIGGER_GROUP = 4;

	public final static int TREENODE_TYPE_ROLE_GROUP = 5;

	public final static int TREENODE_TYPE_USER = 6;

	public final static int TREENODE_TYPE_TRIGGER = 7;

	public final static int TREENODE_TYPE_PROCEDURE = 8;

	public final static int TREENODE_TYPE_VIEW = 9;

	public final static int TREENODE_TYPE_ROLE = 10;

	// variables
	// ========================================================================
	// non visible
	// ------------------------------------------------------------------------
	// Logger for this class
	private final static ILogger log = LoggerController.createLogger(FirebirdManagerGrantFrame.class);

	private static final StringManager stringManager =
		StringManagerFactory.getStringManager(FirebirdManagerGrantFrame.class);

	private ISession session;

	private String gsTreenodeName = "";

	private int gTreenodeType = -1;

	// data objects
	private FirebirdManagerGrantTreeNode rootNode;

	private Vector<Vector<Object>> vecGrant = null;

	private List<String> listUserNames = null;

	private List<String> listTriggerNames = null;

	private List<String> listRoleNames = null;

	private List<FirebirdManagerGrantDbObject> listTableNames = null;

	private List<String> listViewNames = null;

	private List<String> listProcedureNames = null;

	// visible (gui)
	// ------------------------------------------------------------------------
	// -- main
	private JSplitPane jsplitpaneMain = new JSplitPane();

	// -- edit
	private JCheckBox jcheckboxSelect = new JCheckBox();

	private JCheckBox jcheckboxInsert = new JCheckBox();

	private JCheckBox jcheckboxDelete = new JCheckBox();

	private JCheckBox jcheckboxUpdate = new JCheckBox();

	private JButton btnSetPermissions = new JButton();

	private JCheckBox jcheckboxReference = new JCheckBox();

	private JCheckBox jcheckboxAll = new JCheckBox();

	private JCheckBox jcheckboxExecute = new JCheckBox();

	private JCheckBox jcheckboxMember = new JCheckBox();

	private JProgressBar jprogressbarSetPermissions = new JProgressBar();

	// tree
	private JTree jtreeReceiver;

	// view
	private JPanel panelTable = null;

	private JTable jtableGrant;

	// select
	private ButtonGroup buttongroupSelect = new ButtonGroup();

	private JRadioButton radioButtonTables = new JRadioButton();

	private JRadioButton radioButtonViews = new JRadioButton();

	private JRadioButton radioButtonProcedures = new JRadioButton();

	private JRadioButton radioButtonRoles = new JRadioButton();

	private JProgressBar jprogressbarReadPermissions = new JProgressBar();

	/**
	 * Constructor
	 * 
	 * @param app
	 * @param rsrc
	 * @param session
	 * @param tab
	 */
	public FirebirdManagerGrantFrame(ISession session)
	{
		super("Firebird manager - " + stringManager.getString("grantmanager.title"), true, true, true, true, session.getApplication());
		this.session = session;

		initDataObjects();
		initLayout();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.moveToFront();
	}

	private interface i18n
	{
		// Labels
		String GRANTMANAGER_LBL_SELECT = stringManager.getString("grantmanager.label.select");

		String GRANTMANAGER_LBL_INSERT = stringManager.getString("grantmanager.label.insert");

		String GRANTMANAGER_LBL_UPDATE = stringManager.getString("grantmanager.label.update");

		String GRANTMANAGER_LBL_DELETE = stringManager.getString("grantmanager.label.delete");

		String GRANTMANAGER_LBL_PERMISSIONS = stringManager.getString("grantmanager.label.permissions");

		String GRANTMANAGER_LBL_REFERENCE = stringManager.getString("grantmanager.label.reference");

		String GRANTMANAGER_LBL_ALL = stringManager.getString("grantmanager.label.all");

		String GRANTMANAGER_LBL_EXECUTE = stringManager.getString("grantmanager.label.execute");

		String GRANTMANAGER_LBL_MEMBER = stringManager.getString("grantmanager.label.member");

		String GRANTMANAGER_LBL_TABLES = stringManager.getString("grantmanager.label.tables");

		String GRANTMANAGER_LBL_VIEWS = stringManager.getString("grantmanager.label.views");

		String GRANTMANAGER_LBL_PROCEDURES = stringManager.getString("grantmanager.label.procedures");

		String GRANTMANAGER_LBL_ROLES = stringManager.getString("grantmanager.label.roles");

		String GRANTMANAGER_TOOLTIP_PERMISSIONS = stringManager.getString("grantmanager.tooltip.permissions");

		String GRANTMANAGER_LBL_TABLECOLUMN_OWNER =
			stringManager.getString("grantmanager.label.tablecolumn.owner");

		String GRANTMANAGER_LBL_TABLECOLUMN_NAME =
			stringManager.getString("grantmanager.label.tablecolumn.name");

		String GRANTMANAGER_LBL_TABLECOLUMN_SELECT =
			stringManager.getString("grantmanager.label.tablecolumn.select");

		String GRANTMANAGER_LBL_TABLECOLUMN_INSERT =
			stringManager.getString("grantmanager.label.tablecolumn.insert");

		String GRANTMANAGER_LBL_TABLECOLUMN_UPDATE =
			stringManager.getString("grantmanager.label.tablecolumn.update");

		String GRANTMANAGER_LBL_TABLECOLUMN_DELETE =
			stringManager.getString("grantmanager.label.tablecolumn.delete");

		String GRANTMANAGER_LBL_TABLECOLUMN_REFERENCE =
			stringManager.getString("grantmanager.label.tablecolumn.reference");

		String GRANTMANAGER_LBL_TABLECOLUMN_EXECUTE =
			stringManager.getString("grantmanager.label.tablecolumn.execute");

		String GRANTMANAGER_LBL_TABLECOLUMN_MEMBER =
			stringManager.getString("grantmanager.label.tablecolumn.member");

		String GRANTMANAGER_LBL_TREENODE_ROOT = stringManager.getString("grantmanager.label.treenode.root");

		String GRANTMANAGER_LBL_TREENODE_USERS = stringManager.getString("grantmanager.label.treenode.users");

		String GRANTMANAGER_LBL_TREENODE_STORED_PROCEDURES =
			stringManager.getString("grantmanager.label.treenode.stored.procedures");

		String GRANTMANAGER_LBL_TREENODE_TRIGGERS =
			stringManager.getString("grantmanager.label.treenode.triggers");

		String GRANTMANAGER_LBL_TREENODE_VIEWS = stringManager.getString("grantmanager.label.treenode.views");

		String GRANTMANAGER_LBL_TREENODE_ROLES = stringManager.getString("grantmanager.label.treenode.roles");

		String GRANTMANAGER_INFO_SELECT_PERMISSION_RECEIVER =
			stringManager.getString("grantmanager.info.select.permission.receiver");

		String GRANTMANAGER_INFO_PRIVILEGES_COUNT =
			stringManager.getString("grantmanager.info.privileges.count");

		String GRANTMANAGER_QUEST_PERMISSION_FOR_GROUP =
			stringManager.getString("grantmanager.question.permission.for.group");

		String GRANTMANAGER_ERROR_SET_PRIVILEGES = stringManager.getString("grantmanager.error.set.privileges");
	}

	// layout
	// ------------------------------------------------------------------------
	private void initLayout()
	{
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(createMain());
	}

	private JPanel createMain()
	{
		initVisualObjects();

		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 =
			new FormLayout("FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE",
				"CENTER:5DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:5DLU:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		jsplitpaneMain.setDividerLocation(180);
		jsplitpaneMain.setLastDividerLocation(-1);
		jsplitpaneMain.setName("jsplitpaneMain");
		jpanel1.add(jsplitpaneMain, cc.xy(2, 2));

		jpanel1.add(createPanelEdit(), cc.xy(2, 4));
		return jpanel1;
	}

	public JPanel createPanelEdit()
	{
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 =
			new FormLayout(
				"FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:GROW(1.0),FILL:30DLU:NONE,FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		jcheckboxSelect.setName("jcheckboxSelect");
		jcheckboxSelect.setText(i18n.GRANTMANAGER_LBL_SELECT);
		jpanel1.add(jcheckboxSelect, cc.xy(1, 1));

		jcheckboxInsert.setName("jcheckboxInsert");
		jcheckboxInsert.setText(i18n.GRANTMANAGER_LBL_INSERT);
		jpanel1.add(jcheckboxInsert, cc.xy(3, 1));

		jcheckboxDelete.setName("jcheckboxDelete");
		jcheckboxDelete.setText(i18n.GRANTMANAGER_LBL_DELETE);
		jpanel1.add(jcheckboxDelete, cc.xy(7, 1));

		jcheckboxUpdate.setName("jcheckboxUpdate");
		jcheckboxUpdate.setText(i18n.GRANTMANAGER_LBL_UPDATE);
		jpanel1.add(jcheckboxUpdate, cc.xy(5, 1));

		btnSetPermissions.setName("btnSetPermissions");
		btnSetPermissions.setText(i18n.GRANTMANAGER_LBL_PERMISSIONS);
		jpanel1.add(btnSetPermissions, cc.xy(10, 1));

		jcheckboxReference.setName("jcheckboxReference");
		jcheckboxReference.setText(i18n.GRANTMANAGER_LBL_REFERENCE);
		jpanel1.add(jcheckboxReference, cc.xy(1, 3));

		jcheckboxAll.setName("jcheckboxAll");
		jcheckboxAll.setText(i18n.GRANTMANAGER_LBL_ALL);
		jpanel1.add(jcheckboxAll, cc.xy(3, 3));

		jcheckboxExecute.setName("jcheckboxExecute");
		jcheckboxExecute.setText(i18n.GRANTMANAGER_LBL_EXECUTE);
		jpanel1.add(jcheckboxExecute, cc.xy(5, 3));

		jcheckboxMember.setName("jcheckboxMember");
		jcheckboxMember.setText(i18n.GRANTMANAGER_LBL_MEMBER);
		jpanel1.add(jcheckboxMember, cc.xy(7, 3));

		jprogressbarSetPermissions.setName("jprogressBarSetting");
		jprogressbarSetPermissions.setValue(25);
		jpanel1.add(jprogressbarSetPermissions, cc.xywh(9, 3, 2, 1));

		return jpanel1;
	}

	public JPanel createPanelTree()
	{
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:GROW(1.0)");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		jtreeReceiver.setName("jtreeReceiver");
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(jtreeReceiver);
		jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane1, cc.xy(1, 1));

		return jpanel1;
	}

	public JPanel createPanelTable()
	{
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 =
			new FormLayout("FILL:DEFAULT:GROW(1.0)", "CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		jtableGrant.setName("jtableView");
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(jtableGrant);
		jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane1, cc.xy(1, 2));

		jpanel1.add(createPanelSelect(), cc.xy(1, 1));

		return jpanel1;
	}

	public JPanel createPanelSelect()
	{
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 =
			new FormLayout(
				"FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:80DLU:NONE",
				"CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		radioButtonTables.setName("radioButtonTables");
		radioButtonTables.setText(i18n.GRANTMANAGER_LBL_TABLES);
		buttongroupSelect.add(radioButtonTables);
		jpanel1.add(radioButtonTables, cc.xy(2, 1));

		radioButtonViews.setName("radioButtonViews");
		radioButtonViews.setText(i18n.GRANTMANAGER_LBL_VIEWS);
		buttongroupSelect.add(radioButtonViews);
		jpanel1.add(radioButtonViews, cc.xy(4, 1));

		radioButtonProcedures.setName("radioButtonProcedures");
		radioButtonProcedures.setText(i18n.GRANTMANAGER_LBL_PROCEDURES);
		buttongroupSelect.add(radioButtonProcedures);
		jpanel1.add(radioButtonProcedures, cc.xy(6, 1));

		radioButtonRoles.setName("radioButtonRoles");
		radioButtonRoles.setText(i18n.GRANTMANAGER_LBL_ROLES);
		buttongroupSelect.add(radioButtonRoles);
		jpanel1.add(radioButtonRoles, cc.xy(8, 1));

		jprogressbarReadPermissions.setName("jprogressBarReading");
		jprogressbarReadPermissions.setValue(25);
		jpanel1.add(jprogressbarReadPermissions, cc.xy(10, 1));

		return jpanel1;
	}

	public void setFocusToFirstEmptyInputField()
	{
		// not used
	}

	private void initVisualObjects()
	{
		createTargetTree();

		jtableGrant =
			new JTable(new FirebirdManagerGrantTableModel(vecGrant, getTableHeader(DISPLAY_TYPE_TABLE)));
		jtableGrant.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jtableGrant.setDefaultRenderer(Object.class, new FirebirdManagerGrantTableRenderer());
		jtableGrant.addKeyListener(this);
		jtableGrant.addMouseListener(this);

		jsplitpaneMain.setLeftComponent(createPanelTree());
		panelTable = createPanelTable();
		jsplitpaneMain.setRightComponent(panelTable);

		radioButtonTables.addActionListener(this);
		radioButtonViews.addActionListener(this);
		radioButtonProcedures.addActionListener(this);
		radioButtonRoles.addActionListener(this);
		ButtonGroup bg = new ButtonGroup();
		bg.add(radioButtonTables);
		bg.add(radioButtonViews);
		bg.add(radioButtonProcedures);
		bg.add(radioButtonRoles);
		// jradiobuttonRoles.setSelected(true);

		jprogressbarReadPermissions.setMinimum(0);
		jprogressbarReadPermissions.setForeground(Color.BLUE);
		jprogressbarReadPermissions.setBackground(Color.LIGHT_GRAY);
		jprogressbarReadPermissions.setStringPainted(true);
		jprogressbarReadPermissions.setValue(0);

		btnSetPermissions.addActionListener(this);
		btnSetPermissions.setToolTipText(i18n.GRANTMANAGER_TOOLTIP_PERMISSIONS);
		// btnSetPermissions.setEnabled(false);

		jprogressbarSetPermissions.setMinimum(0);
		jprogressbarSetPermissions.setForeground(Color.BLUE);
		jprogressbarSetPermissions.setBackground(Color.LIGHT_GRAY);
		jprogressbarSetPermissions.setStringPainted(true);
		jprogressbarSetPermissions.setValue(0);

		readData();
	}

	// misc
	// ------------------------------------------------------------------------
	private void appendPrivilegeToBuffer(StringBuffer pBuffer, String privilege)
	{
		if (pBuffer.length() > 0) pBuffer.append("," + privilege);
		else pBuffer.append(privilege);
	} // private void appendPrivelegeToBuffer(StringBuffer pBuffer, String psPrivilege)

	private boolean isTreeNodeAGroup()
	{
		return gTreenodeType == TREENODE_TYPE_USER_GROUP || gTreenodeType == TREENODE_TYPE_PROCEDURE_GROUP
			|| gTreenodeType == TREENODE_TYPE_VIEW_GROUP || gTreenodeType == TREENODE_TYPE_TRIGGER_GROUP
			|| gTreenodeType == TREENODE_TYPE_ROLE_GROUP;
	}

	// data
	// ------------------------------------------------------------------------
	private void createTargetTree()
	{
		rootNode = new FirebirdManagerGrantTreeNode(i18n.GRANTMANAGER_LBL_TREENODE_ROOT, TREENODE_TYPE_ROOT);
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		jtreeReceiver = new JTree(treeModel);
		jtreeReceiver.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				FirebirdManagerGrantTreeNode node =
					(FirebirdManagerGrantTreeNode) e.getPath().getLastPathComponent();
				gsTreenodeName = node.getUserObject().toString();
				gTreenodeType = node.getTreenodeType();
				readData();
			}
		});
		TreeSelectionModel tsm = new DefaultTreeSelectionModel();
		tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jtreeReceiver.setBackground(Color.LIGHT_GRAY);
		jtreeReceiver.setSelectionModel(tsm);
		jtreeReceiver.setEnabled(true);
		jtreeReceiver.setRootVisible(true); // false);
		FirebirdManagerGrantTreeRenderer treeRenderer = new FirebirdManagerGrantTreeRenderer();
		jtreeReceiver.setCellRenderer(treeRenderer);

		setTreeData();
	}

	// ------------------------------------------------------------------------
	private void getPrivileges()
	{
		if (radioButtonTables.isSelected() || radioButtonViews.isSelected())
		{
			jcheckboxSelect.setSelected(((Boolean) jtableGrant.getValueAt(jtableGrant.getSelectedRow(),
				TABLES_COL_SELECT)).booleanValue());
			jcheckboxInsert.setSelected(((Boolean) jtableGrant.getValueAt(jtableGrant.getSelectedRow(),
				TABLES_COL_INSERT)).booleanValue());
			jcheckboxUpdate.setSelected(((Boolean) jtableGrant.getValueAt(jtableGrant.getSelectedRow(),
				TABLES_COL_UPDATE)).booleanValue());
			jcheckboxDelete.setSelected(((Boolean) jtableGrant.getValueAt(jtableGrant.getSelectedRow(),
				TABLES_COL_DELETE)).booleanValue());
			jcheckboxReference.setSelected(((Boolean) jtableGrant.getValueAt(jtableGrant.getSelectedRow(),
				TABLES_COL_REFERENCE)).booleanValue());
			jcheckboxAll.setSelected(jcheckboxSelect.isSelected() && jcheckboxInsert.isSelected()
				&& jcheckboxUpdate.isSelected() && jcheckboxDelete.isSelected()
				&& jcheckboxReference.isSelected());
		}
		else if (radioButtonProcedures.isSelected())
		{
			jcheckboxExecute.setSelected(((Boolean) jtableGrant.getValueAt(jtableGrant.getSelectedRow(), 1)).booleanValue());
		}
		else if (radioButtonRoles.isSelected())
		{
			jcheckboxMember.setSelected(((Boolean) jtableGrant.getValueAt(jtableGrant.getSelectedRow(), 1)).booleanValue());
		}
	}

	// ------------------------------------------------------------------------
	private String getSelectedPrivileges()
	{
		StringBuffer bufPrivileges = new StringBuffer();
		if (radioButtonTables.isSelected() || radioButtonViews.isSelected())
		{
			if (jcheckboxAll.isSelected()) appendPrivilegeToBuffer(bufPrivileges, "ALL");
			else
			{
				if (jcheckboxSelect.isSelected()) appendPrivilegeToBuffer(bufPrivileges, "SELECT");
				if (jcheckboxInsert.isSelected()) appendPrivilegeToBuffer(bufPrivileges, "INSERT");
				if (jcheckboxUpdate.isSelected()) appendPrivilegeToBuffer(bufPrivileges, "UPDATE");
				if (jcheckboxDelete.isSelected()) appendPrivilegeToBuffer(bufPrivileges, "DELETE");
				if (jcheckboxReference.isSelected()) appendPrivilegeToBuffer(bufPrivileges, "REFERENCES");
			}
		}
		else if (radioButtonProcedures.isSelected()) if (jcheckboxExecute.isSelected()) appendPrivilegeToBuffer(
			bufPrivileges, "EXECUTE");

		return bufPrivileges.toString();
	}

	// ------------------------------------------------------------------------
	private void setOptimalColumnWidth(int displayType)
	{
		int markerColWidth = 50;
		int decOffset = 3;
		if (displayType == DISPLAY_TYPE_PROCEDURE || displayType == DISPLAY_TYPE_ROLE)
		{
			markerColWidth = 70;
		}
		int maxWidth = panelTable.getWidth();

		TableColumnModel cm = jtableGrant.getColumnModel();

		int textColumnWidth = maxWidth - ((cm.getColumnCount() - 1) * markerColWidth) - decOffset;
		if (textColumnWidth < 50)
		{
			markerColWidth = (maxWidth - 50) / (cm.getColumnCount() - 1);
		}

		cm.getColumn(0).setPreferredWidth(textColumnWidth);
		for (int i = 1; i < cm.getColumnCount(); i++)
		{
			cm.getColumn(i).setPreferredWidth(markerColWidth);
		}
	}

	// ------------------------------------------------------------------------
	private Vector<Object> getTableHeader(int piType)
	{
		Vector<Object> vecTableHeader = new Vector<Object>();
		switch (piType)
		{
		case DISPLAY_TYPE_TABLE:
		case DISPLAY_TYPE_VIEW:
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_NAME);
			if (piType == DISPLAY_TYPE_TABLE)
			{
				vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_OWNER);
			}
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_SELECT);
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_INSERT);
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_UPDATE);
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_DELETE);
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_REFERENCE);
			break;
		case DISPLAY_TYPE_PROCEDURE:
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_NAME);
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_EXECUTE);
			break;
		case DISPLAY_TYPE_ROLE:
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_NAME);
			vecTableHeader.add(i18n.GRANTMANAGER_LBL_TABLECOLUMN_MEMBER);
			break;
		default:
			break;
		}
		return vecTableHeader;
	}

	// ------------------------------------------------------------------------
	private void initDataObjects()
	{
		listUserNames = FirebirdManagerDataAccess.getUsernameList(session);
		listTriggerNames = FirebirdManagerDataAccess.getTriggerList(session);
		listRoleNames = FirebirdManagerDataAccess.getRoleList(session);
		listTableNames = FirebirdManagerDataAccess.getTableList(session);
		listViewNames = FirebirdManagerDataAccess.getViewList(session);
		listProcedureNames = FirebirdManagerDataAccess.getProcedureList(session);
	}

	private void readData()
	{
		if (radioButtonTables.isSelected())
		{
			readDataTables();
			setCheckBoxStatus();
		}
		else if (radioButtonViews.isSelected())
		{
			readDataViews();
			setCheckBoxStatus();
		}
		else if (radioButtonProcedures.isSelected())
		{
			readDataProcedures();
			setCheckBoxStatus();
		}
		else if (radioButtonRoles.isSelected())
		{
			readDataRoles();
			setCheckBoxStatus();
		}
		jprogressbarSetPermissions.setMinimum(0);
		jprogressbarSetPermissions.setMaximum(0);
		refreshProgressBar(jprogressbarSetPermissions, 0, 0);
		btnSetPermissions.setEnabled(jtableGrant.getSelectedRow() > -1);
	}

	// ------------------------------------------------------------------------
	private void readDataProcedures()
	{
		int iCount = listProcedureNames.size();
		jprogressbarReadPermissions.setValue(0);
		jprogressbarReadPermissions.setMaximum(iCount);
		refreshProgressBar(jprogressbarReadPermissions, 0, iCount);
		boolean groupHeader = isTreeNodeAGroup();
		FirebirdManagerPrivilege fbPrivileges = null;
		vecGrant = new Vector<Vector<Object>>();
		for (int i = 0; i < iCount; i++)
		{
			String relationName = (String) listProcedureNames.get(i);
			Vector<Object> vecRow = new Vector<Object>();
			vecRow.add(relationName);
			fbPrivileges =
				FirebirdManagerDataAccess.readPrivileges(session, gsTreenodeName, relationName, groupHeader);
			vecRow.add(Boolean.valueOf(fbPrivileges.isExecute()));
			vecGrant.add(vecRow);
			refreshProgressBar(jprogressbarReadPermissions, i + 1, iCount);
		}

		refreshTable(DISPLAY_TYPE_PROCEDURE);
	}

	// ------------------------------------------------------------------------
	private void readDataRoles()
	{
		int iCount = listRoleNames.size();
		jprogressbarReadPermissions.setValue(0);
		jprogressbarReadPermissions.setMaximum(iCount);
		refreshProgressBar(jprogressbarReadPermissions, 0, iCount);
		boolean groupHeader = isTreeNodeAGroup();
		FirebirdManagerPrivilege fbPrivileges = null;
		vecGrant = new Vector<Vector<Object>>();
		for (int i = 0; i < iCount; i++)
		{
			String relationName = (String) listRoleNames.get(i);
			Vector<Object> vecRow = new Vector<Object>();
			vecRow.add(relationName);
			fbPrivileges =
				FirebirdManagerDataAccess.readPrivileges(session, gsTreenodeName, relationName, groupHeader);
			vecRow.add(Boolean.valueOf(fbPrivileges.isMember()));
			vecGrant.add(vecRow);
			refreshProgressBar(jprogressbarReadPermissions, i + 1, iCount);
		}
		refreshTable(DISPLAY_TYPE_ROLE);
	}

	// ------------------------------------------------------------------------
	private void readDataTables()
	{
		int iCount = listTableNames.size();
		jprogressbarReadPermissions.setValue(0);
		jprogressbarReadPermissions.setMaximum(iCount);
		refreshProgressBar(jprogressbarReadPermissions, 0, iCount);
		boolean groupHeader = isTreeNodeAGroup();
		FirebirdManagerPrivilege fbPrivileges = null;
		vecGrant = new Vector<Vector<Object>>();
		for (int i = 0; i < iCount; i++)
		{
			String relationName = listTableNames.get(i).getName();
			Vector<Object> vecRow = new Vector<Object>();
			vecRow.add(relationName);
			fbPrivileges =
				FirebirdManagerDataAccess.readPrivileges(session, gsTreenodeName, relationName, groupHeader);
			vecRow.add(listTableNames.get(i).getOwner());
			vecRow.add(Boolean.valueOf(fbPrivileges.isSelect()));
			vecRow.add(Boolean.valueOf(fbPrivileges.isInsert()));
			vecRow.add(Boolean.valueOf(fbPrivileges.isUpdate()));
			vecRow.add(Boolean.valueOf(fbPrivileges.isDelete()));
			vecRow.add(Boolean.valueOf(fbPrivileges.isReference()));
			vecGrant.add(vecRow);
			refreshProgressBar(jprogressbarReadPermissions, i + 1, iCount);
		}
		refreshTable(DISPLAY_TYPE_TABLE);
	}

	// ------------------------------------------------------------------------
	private void readDataViews()
	{
		int iCount = listViewNames.size();
		jprogressbarReadPermissions.setValue(0);
		jprogressbarReadPermissions.setMaximum(iCount);
		refreshProgressBar(jprogressbarReadPermissions, 0, iCount);
		boolean groupHeader = isTreeNodeAGroup();
		FirebirdManagerPrivilege fbPrivileges = new FirebirdManagerPrivilege();
		vecGrant = new Vector<Vector<Object>>();
		for (int i = 0; i < iCount; i++)
		{
			String relationName = (String) listViewNames.get(i);
			Vector<Object> vecRow = new Vector<Object>();
			vecRow.add(relationName);
			fbPrivileges =
				FirebirdManagerDataAccess.readPrivileges(session, gsTreenodeName, relationName, groupHeader);
			vecRow.add(Boolean.valueOf(fbPrivileges.isSelect()));
			vecRow.add(Boolean.valueOf(fbPrivileges.isInsert()));
			vecRow.add(Boolean.valueOf(fbPrivileges.isUpdate()));
			vecRow.add(Boolean.valueOf(fbPrivileges.isDelete()));
			vecRow.add(Boolean.valueOf(fbPrivileges.isReference()));
			vecGrant.add(vecRow);
			refreshProgressBar(jprogressbarReadPermissions, i + 1, iCount);
		}
		refreshTable(DISPLAY_TYPE_VIEW);
	}

	private void refreshProgressBar(JProgressBar pProgressBar, int piValue, int piCount)
	{
		pProgressBar.setString(piValue + "/" + piCount);
		pProgressBar.setValue(piValue);
		pProgressBar.paintImmediately(0, 0, pProgressBar.getWidth(), pProgressBar.getHeight());
	}

	// ------------------------------------------------------------------------
	private void refreshTable(int displayType)
	{
		DefaultTableModel tm = (DefaultTableModel) jtableGrant.getModel();
		tm.setDataVector(vecGrant, getTableHeader(displayType));
		tm.fireTableStructureChanged();
		setOptimalColumnWidth(displayType);
	}

	// ------------------------------------------------------------------------
	private void setCheckBoxStatus()
	{
		// Tables and Views
		jcheckboxAll.setEnabled(radioButtonTables.isSelected() || radioButtonViews.isSelected());
		jcheckboxSelect.setEnabled(jcheckboxAll.isEnabled());
		jcheckboxUpdate.setEnabled(jcheckboxAll.isEnabled());
		jcheckboxInsert.setEnabled(jcheckboxAll.isEnabled());
		jcheckboxDelete.setEnabled(jcheckboxAll.isEnabled());
		jcheckboxReference.setEnabled(jcheckboxAll.isEnabled());
		jcheckboxAll.setSelected(false);
		jcheckboxSelect.setSelected(false);
		jcheckboxUpdate.setSelected(false);
		jcheckboxInsert.setSelected(false);
		jcheckboxDelete.setSelected(false);
		jcheckboxReference.setSelected(false);
		// Procedures
		jcheckboxExecute.setEnabled(radioButtonProcedures.isSelected());
		jcheckboxExecute.setSelected(false);
		// Roles
		jcheckboxMember.setEnabled(radioButtonRoles.isSelected());
		jcheckboxMember.setSelected(false);
	}

	// ------------------------------------------------------------------------
	private void setPrivileges()
	{
		boolean bSetPermissions = true;
		if (gTreenodeType == TREENODE_TYPE_ROOT)
		{
			JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), i18n.GRANTMANAGER_INFO_SELECT_PERMISSION_RECEIVER);
			jtreeReceiver.requestFocus();
			bSetPermissions = false;
		}
		else if (isTreeNodeAGroup())
		{
			bSetPermissions =
				JOptionPane.showConfirmDialog(session.getApplication().getMainFrame(), i18n.GRANTMANAGER_QUEST_PERMISSION_FOR_GROUP + " "
					+ gsTreenodeName + "?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		}

		if (bSetPermissions)
		{
			int[] iSelRows = jtableGrant.getSelectedRows();
			boolean bOK = true;

			// get grant target
			List<String> listGrantTarget = new ArrayList<String>();
			if (isTreeNodeAGroup())
			{
				if (gTreenodeType == TREENODE_TYPE_USER_GROUP) listGrantTarget = listUserNames;
				else if (gTreenodeType == TREENODE_TYPE_PROCEDURE_GROUP) listGrantTarget = listProcedureNames;
				else if (gTreenodeType == TREENODE_TYPE_VIEW_GROUP) listGrantTarget = listViewNames;
				else if (gTreenodeType == TREENODE_TYPE_TRIGGER_GROUP) listGrantTarget = listTriggerNames;
				else if (gTreenodeType == TREENODE_TYPE_ROLE_GROUP) listGrantTarget = listRoleNames;
			}
			else listGrantTarget.add(gsTreenodeName);

			int iCountSource = iSelRows.length;
			int iCountTarget = listGrantTarget.size();
			int iCount = iCountSource * iCountTarget;

			bSetPermissions = iCount < 10000;
			if (!bSetPermissions) bSetPermissions =
				JOptionPane.showConfirmDialog(session.getApplication().getMainFrame(), i18n.GRANTMANAGER_INFO_PRIVILEGES_COUNT.replaceFirst("{1}",
					iCount + "")
					+ CR + "Do you want to do this?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION;

			if (bSetPermissions)
			{
				int iValue = 0;
				jprogressbarSetPermissions.setMaximum(0);
				jprogressbarSetPermissions.setMaximum(iCount);
				refreshProgressBar(jprogressbarSetPermissions, iValue, iCount);
				for (int i = 0; i < iCountSource; i++)
				{
					if (!bOK) break;
					String sGrantSource = (String) jtableGrant.getValueAt(iSelRows[i], 0);

					for (int g = 0; g < iCountTarget; g++)
					{
						String sGrantTarget = (String) listGrantTarget.get(g);
						if (radioButtonTables.isSelected() || radioButtonViews.isSelected()) bOK =
							setPrivilegesForTableAndView(sGrantSource, sGrantTarget, iSelRows[i]);
						else if (radioButtonProcedures.isSelected()) bOK =
							setPrivilegesForProcedure(sGrantSource, sGrantTarget, iSelRows[i]);
						else if (radioButtonRoles.isSelected()) bOK =
							setPrivilegesForRole(sGrantSource, sGrantTarget, iSelRows[i]);
						iValue = i * iCountTarget + g + 1;
						refreshProgressBar(jprogressbarSetPermissions, iValue, iCount);
					} // for (int g=0; g<vecGrantTarget.size(); g++)
				}
			}
		}
	}

	// ------------------------------------------------------------------------
	private boolean setPrivilegesForTableAndView(String psGrantOn, String psGrantTo, int piRow)
	{
		String sSQLRevoke = "Revoke all on " + psGrantOn + " from " + psGrantTo;
		String sPrivileges = getSelectedPrivileges();
		String sSQLGrant = "Grant " + sPrivileges + " on " + psGrantOn + " to " + psGrantTo;
		if (sPrivileges.trim().equals("")) sSQLGrant = "";

		if (!setFirebirdPrivileges(sSQLRevoke, sSQLGrant))
		{
			JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), i18n.GRANTMANAGER_ERROR_SET_PRIVILEGES);
			return false;
		}
		else
		{
			jtableGrant.setValueAt(Boolean.valueOf(jcheckboxSelect.isSelected() || jcheckboxAll.isSelected()),
				piRow, 2);
			jtableGrant.setValueAt(Boolean.valueOf(jcheckboxInsert.isSelected() || jcheckboxAll.isSelected()),
				piRow, 3);
			jtableGrant.setValueAt(Boolean.valueOf(jcheckboxUpdate.isSelected() || jcheckboxAll.isSelected()),
				piRow, 4);
			jtableGrant.setValueAt(Boolean.valueOf(jcheckboxDelete.isSelected() || jcheckboxAll.isSelected()),
				piRow, 5);
			jtableGrant.setValueAt(Boolean.valueOf(jcheckboxReference.isSelected() || jcheckboxAll.isSelected()),
				piRow, 6);
			return true;
		}
	}

	// ------------------------------------------------------------------------
	private boolean setPrivilegesForProcedure(String psGrantOn, String psGrantTo, int piRow)
	{
		String sSQLRevoke = "Revoke all on " + psGrantOn + " from " + psGrantTo;
		String sPrivileges = getSelectedPrivileges();
		String sSQLGrant = "Grant " + sPrivileges + " on Procedure " + psGrantOn + " to " + psGrantTo;
		if (sPrivileges.trim().equals("")) sSQLGrant = "";

		if (!setFirebirdPrivileges(sSQLRevoke, sSQLGrant))
		{
			JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), i18n.GRANTMANAGER_ERROR_SET_PRIVILEGES);
			return false;
		}
		else
		{
			jtableGrant.setValueAt(Boolean.valueOf(jcheckboxExecute.isSelected()), piRow, 1);
			return true;
		}
	}

	// ------------------------------------------------------------------------
	private boolean setPrivilegesForRole(String psGrantOn, String psGrantTo, int piRow)
	{
		String sSQLRevoke = "Revoke " + psGrantOn + " from " + psGrantTo;
		String sSQLGrant = "";
		if (jcheckboxMember.isSelected()) sSQLGrant = "Grant " + psGrantOn + " to " + psGrantTo; // " WITH ADMIN OPTION"

		if (!setFirebirdPrivileges(sSQLRevoke, sSQLGrant))
		{
			JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), i18n.GRANTMANAGER_ERROR_SET_PRIVILEGES);
			return false;
		}
		else
		{
			jtableGrant.setValueAt(Boolean.valueOf(jcheckboxMember.isSelected()), piRow, 1);
			return true;
		}
	}

	// ------------------------------------------------------------------------
	private void setTreeData()
	{
		DefaultTreeModel treeModel = (DefaultTreeModel) jtreeReceiver.getModel();

		FirebirdManagerGrantTreeNode nodeUsers =
			new FirebirdManagerGrantTreeNode(i18n.GRANTMANAGER_LBL_TREENODE_USERS + " (" + listUserNames.size()
				+ ")", TREENODE_TYPE_USER_GROUP);
		treeModel.insertNodeInto(nodeUsers, rootNode, rootNode.getChildCount());
		if (listUserNames != null)
		{
			for (int i = 0; i < listUserNames.size(); i++)
			{
				FirebirdManagerGrantTreeNode node =
					new FirebirdManagerGrantTreeNode((String) listUserNames.get(i), TREENODE_TYPE_USER);
				treeModel.insertNodeInto(node, nodeUsers, nodeUsers.getChildCount());
			}
		}

		FirebirdManagerGrantTreeNode nodeProcedures =
			new FirebirdManagerGrantTreeNode(i18n.GRANTMANAGER_LBL_TREENODE_STORED_PROCEDURES + " ("
				+ listProcedureNames.size() + ")", TREENODE_TYPE_PROCEDURE_GROUP);
		treeModel.insertNodeInto(nodeProcedures, rootNode, rootNode.getChildCount());
		if (listProcedureNames != null)
		{
			for (int iV = 0; iV < listProcedureNames.size(); iV++)
			{
				FirebirdManagerGrantTreeNode node =
					new FirebirdManagerGrantTreeNode((String) listProcedureNames.get(iV), TREENODE_TYPE_PROCEDURE);
				treeModel.insertNodeInto(node, nodeProcedures, nodeProcedures.getChildCount());
			}
		}

		FirebirdManagerGrantTreeNode nodeTriggers =
			new FirebirdManagerGrantTreeNode(i18n.GRANTMANAGER_LBL_TREENODE_TRIGGERS + " ("
				+ listTriggerNames.size() + ")", TREENODE_TYPE_TRIGGER_GROUP);
		treeModel.insertNodeInto(nodeTriggers, rootNode, rootNode.getChildCount());
		if (listTriggerNames != null)
		{
			for (int iV = 0; iV < listTriggerNames.size(); iV++)
			{
				FirebirdManagerGrantTreeNode node =
					new FirebirdManagerGrantTreeNode((String) listTriggerNames.get(iV), TREENODE_TYPE_TRIGGER);
				treeModel.insertNodeInto(node, nodeTriggers, nodeTriggers.getChildCount());
			}
		}

		FirebirdManagerGrantTreeNode nodeViews =
			new FirebirdManagerGrantTreeNode(i18n.GRANTMANAGER_LBL_TREENODE_VIEWS + " (" + listViewNames.size()
				+ ")", TREENODE_TYPE_VIEW_GROUP);
		treeModel.insertNodeInto(nodeViews, rootNode, rootNode.getChildCount());
		if (listViewNames != null)
		{
			for (int iV = 0; iV < listViewNames.size(); iV++)
			{
				FirebirdManagerGrantTreeNode node =
					new FirebirdManagerGrantTreeNode((String) listViewNames.get(iV), TREENODE_TYPE_VIEW);
				treeModel.insertNodeInto(node, nodeViews, nodeViews.getChildCount());
			}
		}

		FirebirdManagerGrantTreeNode nodeRoles =
			new FirebirdManagerGrantTreeNode(i18n.GRANTMANAGER_LBL_TREENODE_ROLES + " (" + listRoleNames.size()
				+ ")", TREENODE_TYPE_ROLE_GROUP);
		treeModel.insertNodeInto(nodeRoles, rootNode, rootNode.getChildCount());
		if (listRoleNames != null)
		{
			for (int iV = 0; iV < listRoleNames.size(); iV++)
			{
				FirebirdManagerGrantTreeNode node =
					new FirebirdManagerGrantTreeNode((String) listRoleNames.get(iV), TREENODE_TYPE_ROLE);
				treeModel.insertNodeInto(node, nodeRoles, nodeRoles.getChildCount());
			}
		}

		treeModel.reload();
		jtreeReceiver.expandPath(new TreePath(nodeUsers.getPath()));
	}

	// ########################################################################
	public boolean setFirebirdPrivileges(String psSQLRevoke, String psSQLGrant)
	{

		Statement stmt = null;
		try
		{
			stmt = session.getSQLConnection().createStatement();
			stmt.execute(psSQLRevoke);
		}
		catch (SQLException e)
		{
			try
			{
				stmt.close();
			}
			catch (SQLException e1)
			{
			}
			log.error(e.getLocalizedMessage() + CR + "Statement:" + CR + psSQLRevoke);
			return false;
		}

		try
		{
			if (!psSQLGrant.trim().equals(""))
			{
				stmt = session.getSQLConnection().createStatement();
				stmt.execute(psSQLGrant);
			}
			stmt.close();
		}
		catch (SQLException e)
		{
			try
			{
				stmt.close();
			}
			catch (SQLException e1)
			{
			}
			log.error(e.getLocalizedMessage() + CR + "Statement:" + CR + psSQLGrant + CR);
			return false;
		}

		return true;
	}

	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == btnSetPermissions) setPrivileges();
		else if (e.getSource() == radioButtonTables || e.getSource() == radioButtonViews
			|| e.getSource() == radioButtonProcedures || e.getSource() == radioButtonRoles) readData();
	}

	// ------------------------------------------------------------------------
	public void keyPressed(KeyEvent e)
	{
	}

	public void keyReleased(KeyEvent e)
	{
		if (e.getSource() == jtableGrant) getPrivileges();
	}

	public void keyTyped(KeyEvent e)
	{
	}

	// ------------------------------------------------------------------------
	public void mouseClicked(MouseEvent e)
	{
		if (e.getSource() == jtableGrant)
		{
			getPrivileges();
			btnSetPermissions.setEnabled(jtableGrant.getSelectedRow() > -1);
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	// ------------------------------------------------------------------------
	public void windowActivated(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}
}

class FirebirdManagerGrantTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 3799375215782358670L;

	@SuppressWarnings("all")
	public FirebirdManagerGrantTableModel(Vector data, Vector columnNames)
	{
		super(data, columnNames);
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false; // super.isCellEditable(row, column);
	}

}

class FirebirdManagerGrantTreeRenderer extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = 1472117273650521629L;

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
		boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		// set default colors
		setBackground(Color.LIGHT_GRAY);
		setBackgroundNonSelectionColor(Color.LIGHT_GRAY);
		setBackgroundSelectionColor(Color.BLUE);

		if (value instanceof FirebirdManagerGrantTreeNode)
		{
			switch (((FirebirdManagerGrantTreeNode) value).getTreenodeType())
			{
			// root
			case FirebirdManagerGrantFrame.TREENODE_TYPE_ROOT:
				setFont(new Font(getFont().getFamily(), Font.BOLD, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("grant_receiver16x16.png"));
				break;
			// groups
			case FirebirdManagerGrantFrame.TREENODE_TYPE_USER_GROUP:
				setFont(new Font(getFont().getFamily(), Font.BOLD, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("user16x16.png"));
				break;
			case FirebirdManagerGrantFrame.TREENODE_TYPE_PROCEDURE_GROUP:
				setFont(new Font(getFont().getFamily(), Font.BOLD, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("procedure16x16.png"));
				break;
			case FirebirdManagerGrantFrame.TREENODE_TYPE_TRIGGER_GROUP:
				setFont(new Font(getFont().getFamily(), Font.BOLD, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("trigger16x16.png"));
				break;
			case FirebirdManagerGrantFrame.TREENODE_TYPE_VIEW_GROUP:
				setFont(new Font(getFont().getFamily(), Font.BOLD, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("view16x16.png"));
				break;
			case FirebirdManagerGrantFrame.TREENODE_TYPE_ROLE_GROUP:
				setFont(new Font(getFont().getFamily(), Font.BOLD, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("role16x16.png"));
				break;

			// childs
			case FirebirdManagerGrantFrame.TREENODE_TYPE_USER:
				setFont(new Font(getFont().getFamily(), Font.PLAIN, getFont().getSize()));
				if (((FirebirdManagerGrantTreeNode) value).getUserObject()
					.toString()
					.trim()
					.toUpperCase()
					.equals("SYSDBA")) setIcon(FirebirdManagerHelper.loadIcon("sysdba16x16.png"));
				else setIcon(FirebirdManagerHelper.loadIcon("user16x16.png"));
				break;
			case FirebirdManagerGrantFrame.TREENODE_TYPE_PROCEDURE:
				setFont(new Font(getFont().getFamily(), Font.PLAIN, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("procedure16x16.png"));
				break;
			case FirebirdManagerGrantFrame.TREENODE_TYPE_TRIGGER:
				setFont(new Font(getFont().getFamily(), Font.PLAIN, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("trigger16x16.png"));
				break;
			case FirebirdManagerGrantFrame.TREENODE_TYPE_VIEW:
				setFont(new Font(getFont().getFamily(), Font.PLAIN, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("view16x16.png"));
				break;
			case FirebirdManagerGrantFrame.TREENODE_TYPE_ROLE:
				setFont(new Font(getFont().getFamily(), Font.PLAIN, getFont().getSize()));
				setIcon(FirebirdManagerHelper.loadIcon("role16x16.png"));
				break;
			default:
				break;
			}
		}

		return this;
	}
}

class FirebirdManagerGrantTableRenderer extends JComponent implements TableCellRenderer
{
	private static final long serialVersionUID = 8399845562910155925L;

	private Border borderLabel = BorderFactory.createEmptyBorder(0, 3, 0, 3);

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		boolean hasFocus, int row, int column)
	{
		JComponent comp = null;
		if (value instanceof String)
		{
			comp = new JLabel(value.toString());
			((JLabel) comp).setHorizontalAlignment(SwingConstants.LEFT);
			((JLabel) comp).setBorder(borderLabel);
			((JLabel) comp).setOpaque(true);
		}
		else if (value instanceof Boolean)
		{
			comp = new JCheckBox("");
			((JCheckBox) comp).setHorizontalAlignment(SwingConstants.CENTER);
		}

		// setting color
		if (isSelected)
		{
			comp.setBackground(table.getSelectionBackground());
			comp.setForeground(table.getSelectionForeground());
		}
		else
		{
			comp.setBackground(table.getBackground());
			comp.setForeground(table.getForeground());
		}

		// setting value
		if (value != null && value instanceof Boolean)
		{
			((JCheckBox) comp).setSelected(((Boolean) value).booleanValue());
		}

		return comp;
	}
}
