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
package net.sourceforge.squirrel_sql.plugins.smarttools.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.smarttools.SmarttoolsHelper;
import net.sourceforge.squirrel_sql.plugins.smarttools.comp.STButton;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SmarttoolCreateIndexD extends JDialog implements ActionListener {
	private static final long serialVersionUID = 3733610935239923743L;
	
	// variables
	// ========================================================================
	// non visible
	// ------------------------------------------------------------------------
	// Logger for this class
	private final static ILogger log = LoggerController
			.createLogger(SmarttoolMissingIndicesFrame.class);
	private final static StringManager stringManager = StringManagerFactory
			.getStringManager(SmarttoolMissingIndicesFrame.class);
	
	private String tablename = null;
	private ISession session = null;
	private Vector<String> vecColumns = new Vector<String>();
	private Vector<String> vecSource = new Vector<String>();
	private Vector<String> vecTarget = new Vector<String>();
	
	// visible (gui)
	// ------------------------------------------------------------------------
	private JLabel lblCreateType = new JLabel();
	private JRadioButton rbPrimaryKey = new JRadioButton();
	private ButtonGroup buttongroup1 = new ButtonGroup();
	private JRadioButton rbUniqueIndex = new JRadioButton();
	private JLabel lblTitleListSourceColumns = new JLabel();
	private JList listSourceColumns = new JList();
	private JLabel lblDdl = new JLabel();
	private JTextArea taDdl = new JTextArea();
	private STButton btnRemove = new STButton();
	private STButton btnAdd = new STButton();
	private STButton btnAddAll = new STButton();
	private STButton btnRemoveAll = new STButton();
	private JLabel lblIndexname = new JLabel();
	private JTextField tfName = new JTextField();
	private JLabel lblTitleListTargetColumns = new JLabel();
	private JList listTargetColumns = new JList();
	private STButton btnExecuteDdl = new STButton();

	
	
	public SmarttoolCreateIndexD(Frame owner, ISession session, String tablename) {
		super(owner, true);
		this.setTitle(i18n.TITLE + " " + tablename);
		this.session = session;
		this.tablename = tablename;
		
		initializePanel();
		this.setSize(700, 500);
		int x = (int) (this.getToolkit().getScreenSize().getWidth() - this.getWidth()) / 2;
		int y = (int) (this.getToolkit().getScreenSize().getHeight() - this.getHeight()) / 2;
		this.setLocation(x, y);
		this.setVisible(true);
	}

	public JPanel createPanel() {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:GROW(0.6),FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE",
				"CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:2DLU:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		lblCreateType.setName("lblCreateType");
		lblCreateType.setText("create");
		jpanel1.add(lblCreateType, cc.xy(2, 2));

		rbPrimaryKey.setActionCommand("primary key");
		rbPrimaryKey.setName("rbPrimaryKey");
		rbPrimaryKey.setText("primary key");
		rbPrimaryKey.setToolTipText("create ddl statement for primary key");
		buttongroup1.add(rbPrimaryKey);
		jpanel1.add(rbPrimaryKey, cc.xy(4, 2));

		rbUniqueIndex.setActionCommand("unique index");
		rbUniqueIndex.setName("rbUniqueIndex");
		rbUniqueIndex.setText("unique index");
		rbUniqueIndex.setToolTipText("create ddl statement for unique index");
		buttongroup1.add(rbUniqueIndex);
		jpanel1.add(rbUniqueIndex, cc.xy(6, 2));

		lblTitleListSourceColumns.setName("lblTitleListSourceColumns");
		lblTitleListSourceColumns.setText("source columns:");
		jpanel1.add(lblTitleListSourceColumns, cc.xywh(2, 4, 3, 1));

		listSourceColumns.setName("listSourceColumns");
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(listSourceColumns);
		jscrollpane1
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane1, cc.xywh(2, 5, 6, 8));

		lblDdl.setName("lblDdl");
		lblDdl.setText("DDL");
		jpanel1.add(lblDdl, cc.xy(2, 14));

		taDdl.setName("taDdl");
		JScrollPane jscrollpane2 = new JScrollPane();
		jscrollpane2.setViewportView(taDdl);
		jscrollpane2
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane2
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane2, cc.xywh(2, 16, 14, 1));

		btnRemove.setActionCommand("<");
		btnRemove.setName("btnRemove");
		btnRemove.setText("<");
		btnRemove.setToolTipText("remove the selected target columns");
		jpanel1.add(btnRemove, cc.xy(9, 11));

		btnAdd.setActionCommand(">");
		btnAdd.setName("btnAdd");
		btnAdd.setText(">");
		btnAdd
				.setToolTipText("add the selected source column to the target columns");
		jpanel1.add(btnAdd, cc.xy(9, 5));

		btnAddAll.setActionCommand(">>");
		btnAddAll.setName("btnAddAll");
		btnAddAll.setText(">>");
		btnAddAll.setToolTipText("add all columns to the target columns");
		jpanel1.add(btnAddAll, cc.xy(9, 7));

		btnRemoveAll.setActionCommand("<<");
		btnRemoveAll.setName("btnRemoveAll");
		btnRemoveAll.setText("<<");
		btnRemoveAll.setToolTipText("remove all target columns");
		jpanel1.add(btnRemoveAll, cc.xy(9, 9));

		lblIndexname.setName("lblIndexname");
		lblIndexname.setText("index name");
		jpanel1.add(lblIndexname, cc.xy(11, 2));

		tfName.setName("tfName");
		jpanel1.add(tfName, cc.xywh(13, 2, 3, 1));

		lblTitleListTargetColumns.setName("lblTitleListTargetColumns");
		lblTitleListTargetColumns.setText("target columns:");
		jpanel1.add(lblTitleListTargetColumns, cc.xywh(11, 4, 3, 1));

		listTargetColumns.setName("listTargetColumns");
		JScrollPane jscrollpane3 = new JScrollPane();
		jscrollpane3.setViewportView(listTargetColumns);
		jscrollpane3
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane3
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane3, cc.xywh(11, 5, 5, 8));

		btnExecuteDdl.setActionCommand("-> SQL");
		btnExecuteDdl.setName("btnExecuteDdl");
		btnExecuteDdl.setText("execute");
		btnExecuteDdl.setToolTipText("execute the created ddl statement");
		jpanel1.add(btnExecuteDdl, cc.xy(15, 14));

		return jpanel1;
	}

	/**
	 * Initializer
	 */
	protected void initializePanel() {
		setLayout(new BorderLayout());
		add(createPanel(), BorderLayout.CENTER);

		initLabels();

		rbPrimaryKey.addActionListener(this);
		rbUniqueIndex.addActionListener(this);
		rbUniqueIndex.setSelected(true);
		rbUniqueIndex.doClick();
		tfName.addActionListener(this);
		tfName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				createDdl();
			}
		});
		listSourceColumns.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					btnAdd.doClick();
				}
			}
		});
		listTargetColumns.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					btnRemove.doClick();
				}
			}
		});
		btnAdd.addActionListener(this);
		btnAddAll.addActionListener(this);
		btnRemove.addActionListener(this);
		btnRemoveAll.addActionListener(this);
		btnExecuteDdl.addActionListener(this);
		
		ITableInfo[] itableInfo;
		try {
			itableInfo = session.getMetaData().getTables(null, null, tablename, new String[] { "TABLE" }, null);
			if (itableInfo.length == 1) {
				TableColumnInfo[] tci = session.getMetaData().getColumnInfo(itableInfo[0]);
				for (int i = 0; i < tci.length; i++) {
					vecColumns.add(tci[i].getColumnName());
				}
				refreshVecSource();
				displayLists();
			}
		} catch (SQLException e) {
			log.error(e);
			JOptionPane.showMessageDialog(null,
					i18n.ERROR_READ_COLUMNNAMES);		}
	}
	
	private void initLabels() {
	      lblCreateType.setText(i18n.LBL_CREATE_TYPE);
	      
	      rbPrimaryKey.setText(i18n.LBL_RB_PRIMARY_KEY);
	      rbPrimaryKey.setToolTipText(i18n.TOOLTIP_RB_PRIMARY_KEY);
	      rbUniqueIndex.setText(i18n.LBL_RB_UNIQUE_INDEX);
	      rbUniqueIndex.setToolTipText(i18n.TOOLTIP_RB_UNIQUE_INDEX);
	      
	      lblIndexname.setText(i18n.LBL_INDEXNAME);
	      lblTitleListSourceColumns.setText(i18n.LBL_LIST_SOURCE_COLUMNS);
	      lblTitleListTargetColumns.setText(i18n.LBL_LIST_TARGET_COLUMNS);
	      lblDdl.setText(i18n.LBL_DDL);

	      btnRemove.setText("");
	      btnRemove.setToolTipText(i18n.TOOLTIP_BTN_REMOVE);
	      btnAdd.setText("");
	      btnAdd.setToolTipText(i18n.TOOLTIP_BTN_ADD);
	      btnAddAll.setText("");
	      btnAddAll.setToolTipText(i18n.TOOLTIP_BTN_ADD_ALL);
	      btnRemoveAll.setText("");
	      btnRemoveAll.setToolTipText(i18n.TOOLTIP_BTN_REMOVE_ALL);
	      btnExecuteDdl.setText(i18n.LBL_BTN_EXECUTE_DDL);
	      btnExecuteDdl.setToolTipText(i18n.TOOLTIP_BTN_EXECUTE_DDL);
	      
	      btnAdd.setIcon(SmarttoolsHelper.loadIcon("add16x16.png"));
	      btnAddAll.setIcon(SmarttoolsHelper.loadIcon("addAll16x16.png"));
	      btnRemove.setIcon(SmarttoolsHelper.loadIcon("remove16x16.png"));
	      btnRemoveAll.setIcon(SmarttoolsHelper.loadIcon("removeAll16x16.png"));
	      btnExecuteDdl.setIcon(SmarttoolsHelper.loadIcon("change16x16.png"));
	}

	private interface i18n {
		// Labels
		String TITLE = stringManager.getString("createindex.title");
		String LBL_CREATE_TYPE = stringManager.getString("createindex.lbl.createtype");
		String LBL_RB_PRIMARY_KEY = stringManager.getString("createindex.lbl.rb.primarykey");
		String LBL_RB_UNIQUE_INDEX = stringManager.getString("createindex.lbl.rb.uniqueindex");
		String LBL_INDEXNAME = stringManager.getString("createindex.lbl.indexname");
		String LBL_LIST_SOURCE_COLUMNS = stringManager.getString("createindex.lbl.title.listsourcecolumns");
		String LBL_LIST_TARGET_COLUMNS = stringManager.getString("createindex.lbl.title.listtargetcolumns");
		String LBL_DDL = stringManager.getString("createindex.lbl.ddl");
		String LBL_BTN_EXECUTE_DDL = stringManager.getString("createindex.lbl.btn.executeddl");

		// Tooltips
		String TOOLTIP_RB_PRIMARY_KEY = stringManager.getString("createindex.tooltip.rb.primarykey");
		String TOOLTIP_RB_UNIQUE_INDEX = stringManager.getString("createindex.tooltip.rb.uniqueindex");
		String TOOLTIP_BTN_REMOVE = stringManager.getString("createindex.tooltip.btn.remove");
		String TOOLTIP_BTN_ADD = stringManager.getString("createindex.tooltip.btn.add");
		String TOOLTIP_BTN_ADD_ALL = stringManager.getString("createindex.tooltip.btn.addall");
		String TOOLTIP_BTN_REMOVE_ALL = stringManager.getString("createindex.tooltip.btn.removeall");
		String TOOLTIP_BTN_EXECUTE_DDL = stringManager.getString("createindex.tooltip.btn.executeddl");
		
		// Errors
		String ERROR_READ_COLUMNNAMES = stringManager.getString("createindex.error.read.columnnames");
		String ERROR_NO_TARGET_COLUMNS_SELECTED = stringManager.getString("createindex.error.no.targetcolumns.selected");
		String ERROR_NO_INDEX_KEY_NAME = stringManager.getString("createindex.error.no.index.key.name");
		String ERROR_EXECUTE_DDL = stringManager.getString("createindex.error.execute.ddl");
		
		// Questions
		String QUESTION_EXECUTE_DDL = stringManager.getString("createindex.question.execute.ddl");
		
		// Infos
		String INFO_SUCCESS_EXECUTE = stringManager.getString("createindex.info.success.execute");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rbPrimaryKey
				|| e.getSource() == rbUniqueIndex
				|| e.getSource() == tfName
				|| e.getSource() == btnAdd
				|| e.getSource() == btnAddAll
				|| e.getSource() == btnRemove
				|| e.getSource() == btnRemoveAll
				|| e.getSource() == btnExecuteDdl
				) {
			if (e.getSource() == rbPrimaryKey) {
				tfName.setText("PK_" + tablename + "_MAIN");
			} else if (e.getSource() == rbUniqueIndex) {
				tfName.setText(tablename + "_MAIN");
			} else if (e.getSource() == btnAdd) {
				addEntry();
			} else if (e.getSource() == btnAddAll) {
				addEntries();
			} else if (e.getSource() == btnRemove) {
				removeEntry();
			} else if (e.getSource() == btnRemoveAll) {
				removeEntries();
			} else if (e.getSource() == btnExecuteDdl) {
				executeDdl();
			}
			createDdl();
		}
	}
	
	private void addEntry() {
		int index = listSourceColumns.getSelectedIndex();
		if (index > -1) {
			vecTarget.add(vecSource.get(index));
			vecSource.remove(index);
			displayLists();
		}
	}
	
	private void addEntries() {
		for (int i = 0; i < vecSource.size(); i++) {
			vecTarget.add(vecSource.get(i));
		}
		vecSource.removeAllElements();
		displayLists();
	}
	
	private void removeEntry() {
		int index = listTargetColumns.getSelectedIndex();
		if (index > -1) {
			vecTarget.remove(index);
			refreshVecSource();
			displayLists();
		}
	}
	
	private void removeEntries() {
		vecTarget.removeAllElements();
		refreshVecSource();
		displayLists();
	}
	
	private void refreshVecSource() {
		vecSource.removeAllElements();
		for (int col = 0; col < vecColumns.size(); col++) {
			String column = vecColumns.get(col);
			boolean colFound = false;
			for (int target = 0; target < vecTarget.size(); target++) {
				if (vecTarget.get(target).equals(column)) {
					colFound = true;
					break;
				}
			}
			if (!colFound) {
				vecSource.add(column);
			}
		}
	}
	
	private void displayLists() {
		listSourceColumns.setListData(vecSource);
		listTargetColumns.setListData(vecTarget);
		if (listSourceColumns.getModel().getSize() > 0) {
			listSourceColumns.setSelectedIndex(0);
		}
		if (listTargetColumns.getModel().getSize() > 0) {
			listTargetColumns.setSelectedIndex(0);
		}
		createDdl();
	}
	
	private void createDdl() {
		StringBuffer buf = new StringBuffer();
		if (rbPrimaryKey.isSelected()) {
			buf.append("ALTER TABLE " + tablename + " ADD CONSTRAINT " + tfName.getText() + " PRIMARY KEY (");
		} else {
			buf.append("CREATE UNIQUE INDEX " + tfName.getText() + " ON " + tablename + " (");
		}
		
		if (listTargetColumns.getModel().getSize() > 0) {
			for (int i = 0; i < listTargetColumns.getModel().getSize(); i++) {
				if (i > 0) {
					buf.append(", ");
				}
					
				buf.append((String)listTargetColumns.getModel().getElementAt(i));
			}
		}
		
		buf.append(");");
		
		taDdl.setText(buf.toString());
	}

	private void executeDdl() {
		if (listTargetColumns.getModel().getSize() < 1) {
			JOptionPane.showMessageDialog(this, i18n.ERROR_NO_TARGET_COLUMNS_SELECTED);
			listSourceColumns.requestFocusInWindow();
		} if (tfName.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(this, i18n.ERROR_NO_INDEX_KEY_NAME);
			tfName.requestFocusInWindow();
		} else {
			if (JOptionPane.showConfirmDialog(this, i18n.QUESTION_EXECUTE_DDL, "?", 
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				try {
					Statement stmt = session.getSQLConnection().createStatement();
					stmt.executeUpdate(taDdl.getText());
					stmt.close();
					JOptionPane.showMessageDialog(this, i18n.INFO_SUCCESS_EXECUTE);
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(this, i18n.ERROR_EXECUTE_DDL);
					log.error(e.getLocalizedMessage());
				}
			}
		}
	}

}
