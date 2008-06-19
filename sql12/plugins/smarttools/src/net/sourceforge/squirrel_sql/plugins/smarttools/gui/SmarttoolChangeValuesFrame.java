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
package net.sourceforge.squirrel_sql.plugins.smarttools.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.smarttools.STDataType;
import net.sourceforge.squirrel_sql.plugins.smarttools.SmarttoolsHelper;
import net.sourceforge.squirrel_sql.plugins.smarttools.comp.STButton;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Mike
 *
 */
public class SmarttoolChangeValuesFrame extends BaseInternalFrame implements
		ISmarttoolFrame, ActionListener {
	private static final long serialVersionUID = 3680564541641320485L;

	private final String WILDCARD = "%";
	private final String INDENT = "   ";
	private final int START_SEARCHING = 0;
	private final int START_CHANGEING = 1;
	private final int STOP_WORKING = 2;

	// variables
	// ========================================================================
	// non visible
	// ------------------------------------------------------------------------
	// Logger for this class
	private final static ILogger log = LoggerController
			.createLogger(SmarttoolFindBadNullValuesFrame.class);
	private final static StringManager stringManager = StringManagerFactory
			.getStringManager(SmarttoolFindBadNullValuesFrame.class);
	
	private final int TABLE_COL_MARKER = 0;
	private final int TABLE_COL_TABLE = 1;
	private final int TABLE_COL_COLUMNNAME = 2;
//	private final int TABLE_COL_DATATYPE = 3; // not used
	private final int TABLE_COL_RECORDS = 4;
	private final int TABLE_COL_STATUS = 5;
	
	private final int[] TABLE_DEFAULT_COL_WIDTH = new int[] {30, 205, 210, 80, 120, 50};
	
	
	private ISession session;
	private Thread threadSearching = null;
	private Thread threadChanging = null;
	private boolean threadSuspended;
	private Vector<String> vecHeader = new Vector<String>();
	private Vector<Vector<Object>> vecData = new Vector<Vector<Object>>();
	private boolean operatorActionListenerDisabled = false;

	// visible (gui)
	// ------------------------------------------------------------------------
	private JPanel panelMain = new JPanel();
	private JLabel lblColumn = new JLabel();
	private JTextField tfColumnName = new JTextField();
	private JLabel lblDatatype = new JLabel();
	private JComboBox cbDataType = new JComboBox();
	private JCheckBox chkDisplayOnlyTablesWithData = new JCheckBox();
	
	private JLabel lblWhere = new JLabel();
	private JLabel lblOperator = new JLabel();
	private JComboBox cbOperator = new JComboBox();
	private JTextField tfOldValue = new JTextField();

	private JLabel lblNewValue = new JLabel();
	private JTextField tfNewValue = new JTextField();
	private JCheckBox chkEnableChangeData = new JCheckBox();

	private STButton btnSearchData = new STButton();
	private STButton btnChangeData = new STButton();
	private STButton btnStop = new STButton();

	private JPanel panelResult = new JPanel();
	private STButton btnSelectAll = new STButton();
	private STButton btnSelectNone = new STButton();
	private JLabel lblTitleResult = new JLabel();
	private JTable tblResult = new JTable();
	private STButton btnPrint = new STButton();
	private JLabel lblFooterTableResult = new JLabel();
	private JProgressBar pbMain = new JProgressBar();

	private ImageIcon iconMarked = SmarttoolsHelper.loadIcon("gridMarked16x16.png");
	private ImageIcon iconDemarked = SmarttoolsHelper.loadIcon("gridDemarked16x16.png");
	
	/**
	 * Constructor
	 * 
	 * @param app
	 * @param rsrc
	 * @param session
	 * @param tab
	 */
	public SmarttoolChangeValuesFrame(ISession session, String title) {
		super("Smarttool - " + title,
				true, true, true, true);
		this.session = session;

		initLayout();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.moveToFront();
		JOptionPane.showMessageDialog(this, i18n.WARNING, i18n.WARNING_TITLE, JOptionPane.WARNING_MESSAGE);
	}

	private void initLayout() {
		this.setLayout(new BorderLayout());
		createTableHeader();
		tblResult = new JTable(vecData, vecHeader);
		this.add(createPanelMain());

		initVisualObjects();
	}

	private void initVisualObjects() {
		lblColumn.setText(i18n.GLOBAL_COLUMN);
		lblDatatype.setText(i18n.GLOBAL_DATATYPE);
		lblNewValue.setText(i18n.LBL_NEW_VALUE);
		lblTitleResult.setText(i18n.LBL_TITLE_RESULT);
		lblOperator.setText(i18n.GLOBAL_OPERATOR);
		lblNewValue.setText(i18n.LBL_NEW_VALUE);
		lblWhere.setText(i18n.LBL_WHERE);

		tfColumnName.setToolTipText(i18n.TOOLTIP_WILDCARD);
		tfOldValue.setToolTipText(i18n.TOOLTIP_TF_OLD_VALUE);

		btnSearchData.setText(i18n.LBL_BTN_SEARCHDATA);
		btnSearchData.setIcon(SmarttoolsHelper.loadIcon("start16x16.png"));
		btnSearchData.addActionListener(this);

		btnChangeData.setText(i18n.LBL_BTN_CHANGEDATA);
		btnChangeData.setIcon(SmarttoolsHelper.loadIcon("change16x16.png"));
		btnChangeData.addActionListener(this);

		btnStop.setText(i18n.GLOBAL_BTN_STOP);
		btnStop.setIcon(SmarttoolsHelper.loadIcon("stop16x16.png"));
		btnStop.addActionListener(this);
		btnStop.setEnabled(false);

		btnPrint.setText(i18n.GLOBAL_BTN_PRINT);
		btnPrint.setIcon(SmarttoolsHelper.loadIcon("printer16x16.png"));
		btnPrint.addActionListener(this);
		btnPrint.setEnabled(false);
		
		btnSelectAll.setText(i18n.LBL_BTN_ALL);
		btnSelectAll.setToolTipText(i18n.TOOLTIP_BTN_ALL);
		btnSelectAll.setIcon(iconMarked);
		btnSelectAll.addActionListener(this);
		
		btnSelectNone.setText(i18n.LBL_BTN_NONE);
		btnSelectNone.setToolTipText(i18n.TOOLTIP_BTN_NONE);
		btnSelectNone.setIcon(iconDemarked);
		btnSelectNone.addActionListener(this);

		fillDataTypes();
		cbDataType.addActionListener(this);
		cbDataType.setSelectedIndex(0);
		cbOperator.addActionListener(this);
		
		chkDisplayOnlyTablesWithData.setText(i18n.LBL_CHK_ONLY_TABLES_WITH_DATA);
		chkEnableChangeData.setText(i18n.LBL_CHK_ENABLE_CHANGE_DATA);
		chkEnableChangeData.addActionListener(this);
		
		tblResult.setDefaultRenderer(Object.class, new SmarttoolChangeValuesTableCellRenderer());
		tblResult.setModel(new SmarttoolChangeValuesTableModel());
		tblResult.setRowSelectionAllowed(true);
		tblResult.setColumnSelectionAllowed(false);
		//tblResult.setAutoCreateRowSorter(true);
		tblResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (tblResult.getSelectedColumn() == TABLE_COL_MARKER) {
					int row = tblResult.getSelectedRow();
					boolean newMarkerValue = !((Boolean)tblResult.getValueAt(row, TABLE_COL_MARKER)).booleanValue(); 
					tblResult.setValueAt(new Boolean(newMarkerValue), row, TABLE_COL_MARKER);
					tblResult.setValueAt(newMarkerValue ? 0 : -1, row, TABLE_COL_STATUS);
				}
			}
			
		});
		tblResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		SmarttoolsHelper.setColumnWidth(tblResult, TABLE_DEFAULT_COL_WIDTH);
		
		pbMain.setValue(0);
		pbMain.setStringPainted(true);

		controlComponents(STOP_WORKING);
	}

	private void createTableHeader() {
		vecHeader.add("");
		vecHeader.add(i18n.GLOBAL_TABLE);
		vecHeader.add(i18n.GLOBAL_COLUMN);
		vecHeader.add(i18n.GLOBAL_DATATYPE);
		vecHeader.add(i18n.GLOBAL_RECORDS);
		vecHeader.add(i18n.GLOBAL_STATUS);
	}

	private void fillDataTypes() {
		cbDataType.removeAllItems();
		List<STDataType> data = SmarttoolsHelper.getListSmarttoolsDataType(false);
		for (int i = 0; i < data.size(); i++) {
			cbDataType.addItem(data.get(i));
		}
		cbDataType.setSelectedIndex(0);
	}
	
	public JPanel createPanelMain() {
		panelMain.setName("panelMain");
		FormLayout formlayout1 = new FormLayout(
				"FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:100DLU:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:80DLU:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:8DLU:GROW(1.0),FILL:DEFAULT:NONE,FILL:4DLU:NONE",
				"CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:4DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE");
		CellConstraints cc = new CellConstraints();
		panelMain.setLayout(formlayout1);

		lblColumn.setName("lblColumn");
		lblColumn.setText("column name");
		panelMain.add(lblColumn, cc.xy(2, 2));

		tfColumnName.setName("tfColumnName");
		panelMain.add(tfColumnName, cc.xy(4, 2));

		lblDatatype.setName("lblDatatype");
		lblDatatype.setText("datatype");
		panelMain.add(lblDatatype, cc.xy(6, 2));

		cbDataType.setName("cbDataType");
		panelMain.add(cbDataType, cc.xy(8, 2));

		btnSearchData.setActionCommand("JButton");
		btnSearchData.setName("btnSearchData");
		btnSearchData.setText("search data");
		panelMain.add(btnSearchData, cc.xy(13, 2));

		btnChangeData.setActionCommand("JButton");
		btnChangeData.setEnabled(false);
		btnChangeData.setName("btnChangeData");
		btnChangeData.setText("change data");
		panelMain.add(btnChangeData, cc.xy(13, 4));

		chkDisplayOnlyTablesWithData
				.setActionCommand("display only rows with data");
		chkDisplayOnlyTablesWithData.setName("chkDisplayOnlyTablesWithData");
		chkDisplayOnlyTablesWithData.setSelected(true);
		chkDisplayOnlyTablesWithData.setText("display only rows with data");
		panelMain.add(chkDisplayOnlyTablesWithData, cc.xy(10, 2));

		btnStop.setActionCommand("stop");
		btnStop.setEnabled(false);
		btnStop.setName("btnStop");
		btnStop.setText("stop");
		panelMain.add(btnStop, cc.xy(13, 6));

		pbMain.setName("pbMain");
		pbMain.setValue(25);
		panelMain.add(pbMain, cc.xywh(2, 12, 12, 1));

		lblOperator.setName("lblOperator");
		lblOperator.setText("operator");
		panelMain.add(lblOperator, cc.xy(6, 4));

		cbOperator.setName("cbOperator");
		panelMain.add(cbOperator, cc.xy(8, 4));

		lblNewValue.setName("lblNewValue");
		lblNewValue.setText("new value");
		panelMain.add(lblNewValue, cc.xy(2, 6));

		tfNewValue.setName("tfNewValue");
		panelMain.add(tfNewValue, cc.xy(4, 6));

		chkEnableChangeData.setActionCommand("enable change data");
		chkEnableChangeData.setName("chkEnableChangeData");
		chkEnableChangeData.setText("enable change button");
		panelMain.add(chkEnableChangeData, cc.xy(10, 6));

		tfOldValue.setName("tfOldValue");
		panelMain.add(tfOldValue, cc.xy(10, 4));

		lblWhere.setName("lblWhere");
		lblWhere.setText("where");
		lblWhere.setHorizontalAlignment(JLabel.RIGHT);
		panelMain.add(lblWhere, cc.xy(4, 4));

		lblFooterTableResult.setBackground(new Color(153, 153, 153));
		lblFooterTableResult.setName("lblFooterTableResult");
		lblFooterTableResult.setOpaque(true);
		lblFooterTableResult.setText("Finished in ...");
		panelMain.add(lblFooterTableResult, cc.xywh(2, 10, 12, 1));

		panelMain.add(createpanelResult(), cc.xywh(2, 8, 12, 1));

		return panelMain;
	}

	public JPanel createpanelResult() {
		panelResult.setName("panelResult");
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE",
				"FILL:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		panelResult.setLayout(formlayout1);

		btnSelectAll.setActionCommand("all");
		btnSelectAll.setName("btnSelectAll");
		btnSelectAll.setText("all");
		btnSelectAll.setToolTipText("select all result entries");
		panelResult.add(btnSelectAll, cc.xy(1, 1));

		btnSelectNone.setActionCommand("none");
		btnSelectNone.setName("btnSelectNone");
		btnSelectNone.setText("none");
		btnSelectNone.setToolTipText("deselect all result entries");
		panelResult.add(btnSelectNone, cc.xy(3, 1));

		tblResult.setName("tblResult");
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(tblResult);
		jscrollpane1
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelResult.add(jscrollpane1, cc.xywh(1, 3, 7, 1));

		lblTitleResult.setBackground(new Color(153, 153, 153));
		lblTitleResult.setName("lblTitleResult");
		lblTitleResult.setOpaque(true);
		lblTitleResult.setText("Found tables and columns:");
		EmptyBorder emptyborder1 = new EmptyBorder(0, 5, 0, 0);
		lblTitleResult.setBorder(emptyborder1);
		panelResult.add(lblTitleResult, cc.xy(5, 1));

		btnPrint.setActionCommand("print");
		btnPrint.setName("btnPrint");
		btnPrint.setText(" print ");
		panelResult.add(btnPrint, cc.xy(7, 1));

		return panelResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.smarttools.gui.ISmarttoolFrame#setFocusToFirstEmptyInputField()
	 */
	public void setFocusToFirstEmptyInputField() {
		tfColumnName.requestFocusInWindow();
	}
	
	private String getHeaderText() {
		return this.getTitle(); 
	}
	
	private String getFooterText() {
		return i18n.GLOBAL_ALIAS + ": " + session.getAlias().getName()
			+ " | " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date())
			+ " | " + i18n.GLOBAL_PAGE + " {0}";
	}

	private int getUsedGroup() {
		return ((STDataType) cbDataType.getSelectedItem()).getGroup();
	}

	private int getUsedDataType() {
		return ((STDataType) cbDataType.getSelectedItem()).getJdbcType();
	}

	private boolean isInputOK(int threadType) {
		if (threadType == START_SEARCHING) {
			if (tfColumnName.getText().trim().length() < 1) {
				JOptionPane.showMessageDialog(this,
						i18n.ERROR_COLUMNNAME_MISSING);
				tfColumnName.requestFocusInWindow();
				return false;
			}
		} else {
			int group = getUsedGroup();
			if (group == STDataType.GROUP_INT) {
				try {
					Integer.parseInt(tfNewValue.getText());
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this,
							i18n.GLOBAL_ERROR_FORMAT_INTEGER);
					tfNewValue.requestFocusInWindow();
					return false;
				}
			} else if (group == STDataType.GROUP_NUMERIC) {
				try {
					Double.parseDouble(tfNewValue.getText());
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this,
							i18n.GLOBAL_ERROR_FORMAT_NUMERIC);
					tfNewValue.requestFocusInWindow();
					return false;
				}
			} else if (group == STDataType.GROUP_DATE) {
				int dataType = getUsedDataType();
				DateFormat df = null;
				if (dataType == Types.DATE) {
					df = DateFormat.getDateInstance();
					try {
						df.parse(tfNewValue.getText());
					} catch (ParseException ex) {
						JOptionPane.showMessageDialog(this,
								i18n.GLOBAL_ERROR_FORMAT_DATE);
						tfNewValue.requestFocusInWindow();
						return false;
					}
				} else if (dataType == Types.TIMESTAMP) {
					df = DateFormat.getDateTimeInstance();
					try {
						df.parse(tfNewValue.getText());
					} catch (ParseException ex) {
						JOptionPane.showMessageDialog(this,
								i18n.GLOBAL_ERROR_FORMAT_DATETIME);
						tfNewValue.requestFocusInWindow();
						return false;
					}
				} else if (dataType == Types.TIME) {
					df = DateFormat.getTimeInstance();
					try {
						df.parse(tfNewValue.getText());
					} catch (ParseException ex) {
						JOptionPane.showMessageDialog(this,
								i18n.GLOBAL_ERROR_FORMAT_TIME);
						tfNewValue.requestFocusInWindow();
						return false;
					}
				}
			} // if (group == STDataType.GROUP_DATE)
			boolean selectedRowExists = false;
			for (int row = 0; row < tblResult.getRowCount(); row++) {
				if (((Boolean)tblResult.getValueAt(row, TABLE_COL_MARKER)).booleanValue()) {
					selectedRowExists = true;
					break;
				}
			}
			if (!selectedRowExists) {
				JOptionPane.showMessageDialog(this, i18n.ERROR_SELECT_ENTRY);
				return false;
			}
		}
		
		if (tfOldValue.isEnabled()
				&& tfOldValue.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(this, i18n.ERROR_MISSING_OLD_VALUE);
			return false;
		}
		return true;
	}
	
	private String getWhereCondition(String columnName, String value) {
		if (cbOperator.getSelectedIndex() > 0) {
			return " where " + columnName + " " + (String)cbOperator.getSelectedItem() + " " + value; 
		} else {
			return "";
		}
	}
	
	private void startSearching() {
		if (isInputOK(START_SEARCHING)) {
			controlComponents(START_SEARCHING);
			chkEnableChangeData.setSelected(false);
			threadSearching = new ThreadSearching();
			threadSearching.start();
		}
	}
	
	private void startChanging() {
		if (isInputOK(START_CHANGEING)
			&& JOptionPane.showConfirmDialog(this, i18n.QUESTION_START_CHANGING,
					i18n.QUESTION_START_CHANGING_TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			controlComponents(START_CHANGEING);
			chkEnableChangeData.setSelected(false);
			threadChanging = new ThreadChanging();
			threadChanging.start();
		}
	}

	private void stopWork() {
		threadSuspended = true;
		if (JOptionPane.showConfirmDialog(this, i18n.QUESTION_CANCEL_WORK,
				i18n.QUESTION_CANCEL_WORK_TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (threadSearching != null) {
				threadSearching = null;
			} else {
				threadChanging = null;
			}
			controlComponents(STOP_WORKING);
		}
		threadSuspended = false;
	}
	
	
	// controlling
	// ------------------------------------------------------------------------
	public void controlComponents(int type) {
		boolean enabled = type == STOP_WORKING;
		
		tfColumnName.setEnabled(enabled);
		cbDataType.setEnabled(enabled);
		cbOperator.setEnabled(enabled);
		controlTfOldValue(enabled);
		chkDisplayOnlyTablesWithData.setEnabled(enabled);
		chkEnableChangeData.setEnabled(enabled
				&& tblResult.getRowCount() > 0);
		tfNewValue.setEnabled(enabled
				&& chkEnableChangeData.isSelected());
		
		btnSearchData.setEnabled(enabled);
		btnChangeData.setEnabled(enabled
				&& chkEnableChangeData.isSelected());
		btnStop.setEnabled(!enabled);
		btnPrint.setEnabled(enabled
				&& tblResult.getRowCount() > 0);
		btnSelectAll.setEnabled(enabled
				&& tblResult.getRowCount() > 0);
		btnSelectNone.setEnabled(enabled
				&& tblResult.getRowCount() > 0);
		tblResult.setEnabled(enabled);
		
		tfColumnName.setBackground(tfColumnName.isEnabled() ? Color.WHITE : Color.LIGHT_GRAY);
		tfNewValue.setBackground(tfNewValue.isEnabled() ? Color.WHITE : Color.LIGHT_GRAY);
	}
	
	private void controlTfOldValue(boolean enabled) {
		tfOldValue.setEnabled(enabled
				&& cbOperator.getSelectedIndex() > 0
				&& !isNullOperator());
		if (isNullOperator()
				|| cbOperator.getSelectedIndex() == 0) {
			tfOldValue.setText("");
		}
		tfOldValue.setBackground(tfOldValue.isEnabled() ? Color.WHITE : Color.LIGHT_GRAY);
	}

	private boolean isNullOperator() {
		return ((String)cbOperator.getSelectedItem()).toLowerCase().indexOf("null") > -1;
	}
	
	private String getOldValue() {
		if (isNullOperator()) {
			return "";
		}
		
		String oldValue = tfOldValue.getText();
		if (getUsedGroup() == STDataType.GROUP_CHAR
				|| getUsedGroup() == STDataType.GROUP_DATE) {
			oldValue = "'" + oldValue + "'";
		}
		return oldValue;
	}
	
	private String getNewValue() {
		String newValue = tfNewValue.getText();
		if (getUsedGroup() == STDataType.GROUP_CHAR
				|| getUsedGroup() == STDataType.GROUP_DATE) {
			newValue = "'" + newValue + "'";
		}
		return newValue;
	}
	
	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSearchData) {
			startSearching();
		} else if (e.getSource() == btnChangeData) {
			startChanging();
		} else if (e.getSource() == btnStop) {
			stopWork();
		} else if (e.getSource() == btnPrint) {
			SmarttoolsHelper.printTable(tblResult, getHeaderText(), getFooterText());
		} else if (e.getSource() == btnSelectAll) {
			SmarttoolsHelper.markAllRows(tblResult, TABLE_COL_MARKER, true);
			((SmarttoolChangeValuesTableModel)tblResult.getModel()).fireTableDataChanged();
		} else if (e.getSource() == btnSelectNone) {
			SmarttoolsHelper.markAllRows(tblResult, TABLE_COL_MARKER, false);
			((SmarttoolChangeValuesTableModel)tblResult.getModel()).fireTableDataChanged();
		} else if (e.getSource() == chkEnableChangeData) {
			controlComponents(STOP_WORKING);
			tfNewValue.requestFocusInWindow();
		} else if (e.getSource() == cbDataType) {
			operatorActionListenerDisabled = true;
			SmarttoolsHelper.fillOperatorTypes(cbOperator, getUsedGroup());
			cbOperator.insertItemAt(i18n.ENTRY_NO_CONDITION, 0);
			operatorActionListenerDisabled = false;
			cbOperator.setSelectedIndex(0);
		} else if (e.getSource() == cbOperator) {
			if (!operatorActionListenerDisabled) {
				controlTfOldValue(true);
			}
		}
	}

	private interface i18n {
		// Global misc
		String GLOBAL_RECORDS = stringManager.getString("global.records");
		String GLOBAL_TABLE = stringManager.getString("global.table");
		String GLOBAL_COLUMN = stringManager.getString("global.column");
		String GLOBAL_DATATYPE = stringManager.getString("global.datatype");
		String GLOBAL_STATUS = stringManager.getString("global.status");
		String GLOBAL_PAGE = stringManager.getString("global.page");
		String GLOBAL_ALIAS = stringManager.getString("global.alias");
		String GLOBAL_OPERATOR = stringManager.getString("global.operator");
		String GLOBAL_VALUE = stringManager.getString("global.value");
		String GLOBAL_BTN_STOP = stringManager.getString("global.lbl.btn.stop");
		String GLOBAL_BTN_PRINT = stringManager.getString("global.lbl.btn.print");
		// Global errors
		String GLOBAL_ERROR_FORMAT_INTEGER = stringManager.getString("global.error.format.integer");
		String GLOBAL_ERROR_FORMAT_NUMERIC = stringManager.getString("global.error.format.numeric");
		String GLOBAL_ERROR_FORMAT_DATE = stringManager.getString("global.error.format.date");
		String GLOBAL_ERROR_FORMAT_DATETIME = stringManager.getString("global.error.format.datetime");
		String GLOBAL_ERROR_FORMAT_TIME = stringManager.getString("global.error.format.time");

		// Misc
		String WARNING_TITLE = stringManager.getString("changevalues.info.warning.title");
		String WARNING = stringManager.getString("changevalues.info.warning");

		// Labels
		String LBL_COLUMNNAME = stringManager.getString("changevalues.lbl.columnname");
		String LBL_NEW_VALUE = stringManager.getString("changevalues.lbl.new.value");
		String LBL_WHERE = stringManager.getString("changevalues.lbl.where");
		String LBL_TITLE_RESULT = stringManager.getString("changevalues.lbl.title.result");
		String LBL_BTN_SEARCHDATA = stringManager.getString("changevalues.btn.searchdata");
		String LBL_BTN_CHANGEDATA = stringManager.getString("changevalues.btn.changedata");
		String LBL_BTN_ALL = stringManager.getString("changevalues.btn.select.all");
		String LBL_BTN_NONE = stringManager.getString("changevalues.btn.select.none");
		String LBL_CHK_ONLY_TABLES_WITH_DATA = stringManager.getString("changevalues.chk.lbl.only.tables.with.data");
		String LBL_CHK_ENABLE_CHANGE_DATA = stringManager.getString("changevalues.chk.lbl.enable.change.data");
		
		// Entries
		String ENTRY_NO_CONDITION = stringManager.getString("changevalues.entry.no.condition");

		// Tooltips
		String TOOLTIP_WILDCARD = stringManager.getString("changevalues.tooltip.wildcard");
		String TOOLTIP_BTN_ALL = stringManager.getString("changevalues.tooltip.btn.select.all");
		String TOOLTIP_BTN_NONE = stringManager.getString("changevalues.tooltip.btn.select.none");
		String TOOLTIP_TF_OLD_VALUE = stringManager.getString("changevalues.tooltip.old.value");
		
		// Questions
		String QUESTION_CANCEL_WORK = stringManager.getString("changevalues.question.cancel.work");
		String QUESTION_CANCEL_WORK_TITLE = stringManager.getString("changevalues.question.cancel.work.title");
		String QUESTION_START_CHANGING = stringManager.getString("changevalues.question.start.changing");
		String QUESTION_START_CHANGING_TITLE = stringManager.getString("changevalues.question.start.changing.title");
		
		// Info
		String INFO_SEARCHING_FINISHED = stringManager.getString("changevalues.info.searching.finished");
		String INFO_CHANGING_FINISHED = stringManager.getString("changevalues.info.changing.finished");
		String INFO_CHANGING_DATA_FOR_COLUMNNAME = stringManager.getString("changevalues.info.changing.data.for.columnname");
		
		// Errors
		String ERROR_COLUMNNAME_MISSING = stringManager.getString("changevalues.error.columnname.missing");
		String ERROR_SEARCHING_DATA = stringManager.getString("changevalues.error.searching.data");
		String ERROR_CHANGING_DATA = stringManager.getString("changevalues.error.changing.data");
		String ERROR_ON_TABLE = stringManager.getString("changevalues.error.on.table");
		String ERROR_CREATING_STATEMENT = stringManager.getString("changevalues.error.creating.statement");
		String ERROR_CLOSING_STATEMENT = stringManager.getString("changevalues.error.closing.statement");
		String ERROR_SELECT_ENTRY = stringManager.getString("changevalues.error.select.entry");
		String ERROR_MISSING_OLD_VALUE = stringManager.getString("changevalues.error.missing.old.value");
	}
	
	
	
	// ------------------------------------------------------------------------
	// Thread SEARCHING
	// ------------------------------------------------------------------------
	class ThreadSearching extends Thread {
		private Thread thisThread = null;

		@Override
		public void run() {
			super.run();
			thisThread = Thread.currentThread();

			startSearching();

			controlComponents(STOP_WORKING);
			threadSearching = null;
		}

		private boolean isThreadInvalid() {
			if (thisThread != threadSearching) {
				return true;
			}
			try {
				synchronized (this) {
					while (threadSuspended) {
						wait(200);
					}
				}
			} catch (InterruptedException e) {
			}
			return false;
		}

		private void startSearching() {
			long startTime = System.currentTimeMillis();

			((DefaultTableModel)tblResult.getModel()).setDataVector(new Vector<Vector<Object>>(), vecHeader);
			
			String tableNamePattern = WILDCARD;
			String oldValue = getOldValue();
			
			try {
				ITableInfo[] tableInfoArray = session.getMetaData().getTables(
						null, null, tableNamePattern, new String[] { "TABLE" },
						null);
				pbMain.setValue(0);
				pbMain.setMaximum(tableInfoArray.length);
			
				for (int i = 0; i < tableInfoArray.length; i++) {
					ITableInfo tableInfo = tableInfoArray[i];
					pbMain.setString(tableInfo.getSimpleName() + " " + (i + 1)
							+ "/" + pbMain.getMaximum());

					checkColumns(tableInfo, oldValue);

					pbMain.setValue(i + 1);
					pbMain.repaint();
					if (isThreadInvalid()) {
						break;
					}
				}
				long diffTime = System.currentTimeMillis() - startTime;
				lblFooterTableResult.setText(" " + i18n.INFO_SEARCHING_FINISHED + " "
						+ diffTime + " ms");
			} catch (SQLException e) {
				log.error(e);
				JOptionPane.showMessageDialog(null,
						i18n.ERROR_SEARCHING_DATA);
			}
		}
	
		private void checkColumns(ITableInfo tableInfo, String oldValue) throws SQLException {
			TableColumnInfo[] columnInfos = session.getMetaData().getColumnInfo(tableInfo);
			Statement stmt = session.getSQLConnection().createStatement();
			int resultFound = 0;
			
			for (int c = 0; c < columnInfos.length; c++) {
				TableColumnInfo tableColumnInfo = columnInfos[c];
				String sql = null;
				
				if (isStringValueCheck(tableColumnInfo)
						|| isIntegerValueCheck(tableColumnInfo)
						|| isNumericValueCheck(tableColumnInfo)
						|| isDateValueCheck(tableColumnInfo)) {
						sql = "SELECT COUNT(*) FROM " + tableInfo.getSimpleName() + " " 
							+ getWhereCondition(tableColumnInfo.getColumnName(), oldValue);
				}

				if (sql != null) {
					try {
						resultFound = SmarttoolsHelper.checkColumnData(stmt, sql);
						if (resultFound > 0
								|| !chkDisplayOnlyTablesWithData.isSelected()) {
							addTableEntry(stmt, tableInfo.getSimpleName(),
									tableColumnInfo.getColumnName(),
									SmarttoolsHelper.getDataTypeForDisplay(tableColumnInfo),
									resultFound);
						}
					} catch (SQLException e) {
						String text = INDENT + i18n.ERROR_ON_TABLE + " ["
								+ tableInfo.getSimpleName() + "] "
								+ i18n.GLOBAL_COLUMN + " ["
								+ tableColumnInfo.getColumnName() + "] :"
								+ e.getLocalizedMessage();
						addTableEntry(stmt, tableInfo.getSimpleName(),
								tableColumnInfo.getColumnName(),
								SmarttoolsHelper.getDataTypeForDisplay(tableColumnInfo),
								-1);
						log.error(text);
					}
				}
			}
			
			stmt.close();
		}		
		
		private void addTableEntry(Statement stmt, String tableName, String columnName,
				String dataType, int recordsFound) throws SQLException {
			if (!chkDisplayOnlyTablesWithData.isSelected()
					|| recordsFound > 0 ) {
				Vector<Object> vecRow = new Vector<Object>();
				vecRow.add(new Boolean(false));
				vecRow.add(tableName);
				vecRow.add(columnName);
				vecRow.add(dataType);
				if (recordsFound > -1) {
					String records = recordsFound + "/" + SmarttoolsHelper.getRowCount(stmt, tableName); 
					vecRow.add(records);
				} else {
					vecRow.add(i18n.ERROR_ON_TABLE);
				}
				vecRow.add(new Integer(0));
				DefaultTableModel tm = (DefaultTableModel) tblResult.getModel();
				tm.addRow(vecRow);
				tm.fireTableDataChanged();
				SmarttoolsHelper.setColumnWidth(tblResult, TABLE_DEFAULT_COL_WIDTH);
			}
		}
		
		private boolean isValidColumnName(String columnName) {
			String columnNameInput = tfColumnName.getText();
			if (columnNameInput.endsWith(WILDCARD)) {
				String cni = columnNameInput.replaceAll("\\" + WILDCARD, "")
						.toLowerCase();
				return columnName.toLowerCase().startsWith(cni);
			} else {
				return columnName.equalsIgnoreCase(columnNameInput);
			}
		}
		
		private boolean isStringValueCheck(TableColumnInfo tableColumnInfo) {
			int dataType = getUsedDataType();
			return (getUsedGroup() == STDataType.GROUP_CHAR
					&& (dataType == tableColumnInfo.getDataType() 
							|| (dataType == STDataType.USE_WHOLE_GROUP 
									&& SmarttoolsHelper.isDataTypeString(tableColumnInfo.getDataType()))) 
					&& isValidColumnName(tableColumnInfo.getColumnName()));
		}
		
		private boolean isIntegerValueCheck(TableColumnInfo tableColumnInfo) {
			int dataType = getUsedDataType();
			return (getUsedGroup() == STDataType.GROUP_INT
					&& (dataType == tableColumnInfo.getDataType() 
							|| (dataType == STDataType.USE_WHOLE_GROUP 
									&& SmarttoolsHelper.isDataTypeInt(tableColumnInfo.getDataType())))
					&& isValidColumnName(tableColumnInfo.getColumnName()));
		}

		private boolean isNumericValueCheck(TableColumnInfo tableColumnInfo) {
			int dataType = getUsedDataType();
			return (getUsedGroup() == STDataType.GROUP_NUMERIC
					&& (dataType == tableColumnInfo.getDataType() 
							|| (dataType == STDataType.USE_WHOLE_GROUP 
									&& SmarttoolsHelper.isDataTypeNumeric(tableColumnInfo.getDataType()))) 
					&& isValidColumnName(tableColumnInfo.getColumnName()));
		}

		private boolean isDateValueCheck(TableColumnInfo tableColumnInfo) {
			int dataType = getUsedDataType();
			return (getUsedGroup() == STDataType.GROUP_DATE
					&& (dataType == tableColumnInfo.getDataType() 
							|| (dataType == STDataType.USE_WHOLE_GROUP 
									&& SmarttoolsHelper.isDataTypeDate(tableColumnInfo.getDataType()))) 
					&& isValidColumnName(tableColumnInfo.getColumnName()));
		}
	} // ThreadSearching
	
	
	// ------------------------------------------------------------------------
	// Thread SEARCHING
	// ------------------------------------------------------------------------
	class ThreadChanging extends Thread {
		private Thread thisThread = null;

		@Override
		public void run() {
			super.run();
			thisThread = Thread.currentThread();

			startChanging();

			controlComponents(STOP_WORKING);
			threadChanging = null;
		}

		private boolean isThreadInvalid() {
			if (thisThread != threadChanging) {
				return true;
			}
			try {
				synchronized (this) {
					while (threadSuspended) {
						wait(200);
					}
				}
			} catch (InterruptedException e) {
			}
			return false;
		}
		
		private void startChanging() {
			long startTime = System.currentTimeMillis();
			String oldValue = getOldValue();
			String newValue = getNewValue();

			pbMain.setValue(0);
			pbMain.setMaximum(0);
			// init progressbar
			for (int r = 0; r < tblResult.getRowCount(); r++) {
				if (((Boolean)tblResult.getValueAt(r, TABLE_COL_MARKER)).booleanValue()) {
					pbMain.setMaximum(pbMain.getMaximum() + 1);
				}
			}

			Statement stmt = null;
			boolean error = false;
			try {
				stmt = session.getSQLConnection().createStatement();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_CREATING_STATEMENT);
				log.error(e);
			}
				
				int i = 0;
				for (int row = 0; row < tblResult.getRowCount(); row++) {
					if (((Boolean) tblResult.getValueAt(row, TABLE_COL_MARKER))
							.booleanValue()) {
						String tableName = (String) tblResult.getValueAt(row,
								TABLE_COL_TABLE);
						String columnName = (String) tblResult.getValueAt(row,
								TABLE_COL_COLUMNNAME);
						i++;
						pbMain.setString(tableName + "." + columnName + " " + i
								+ "/" + pbMain.getMaximum());

						try {
							String sql = "UPDATE " + tableName + " SET " + columnName 
								+ " = " + newValue + " " + getWhereCondition(columnName, oldValue);
							int rowsAffected = stmt.executeUpdate(sql);
							int rowCount = SmarttoolsHelper.getRowCount(stmt, tableName);
							tblResult.setValueAt(new Integer(1), row, TABLE_COL_STATUS);
							tblResult.setValueAt(rowsAffected + "/" + rowCount, row, TABLE_COL_RECORDS);
						} catch (SQLException e) {
							error = true;
							tblResult.setValueAt(new Integer(2), row, TABLE_COL_STATUS);
							log.error(e);
						}

						pbMain.setValue(i);
						pbMain.repaint();
						if (isThreadInvalid()) {
							break;
						}
					}
				}

			try {
				stmt.close();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_CLOSING_STATEMENT);
				log.error(e);
			}
			long diffTime = System.currentTimeMillis() - startTime;
			lblFooterTableResult.setText(" " + i18n.INFO_CHANGING_FINISHED + " "
					+ diffTime + " ms");
			
			if (error) {
				JOptionPane.showMessageDialog(null, i18n.ERROR_CHANGING_DATA);
			}
		}
	}	
	
	
	
	// ------------------------------------------------------------------------
	// Table classes
	// ------------------------------------------------------------------------
	class SmarttoolChangeValuesTableCellRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = -7923233754901241279L;
		private ImageIcon iconMarkedSelected = SmarttoolsHelper.loadIcon("gridMarkedSelected16x16.png");
		private ImageIcon iconDemarkedSelected = SmarttoolsHelper.loadIcon("gridDemarkedSelected16x16.png");
		private ImageIcon iconStatusInit = SmarttoolsHelper.loadIcon("statusInit16x16.png");
		private ImageIcon iconStatusOk = SmarttoolsHelper.loadIcon("statusOk16x16.png");
		private ImageIcon iconStatusError = SmarttoolsHelper.loadIcon("statusError16x16.png");

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JLabel lbl = new JLabel();
			lbl.setOpaque(true);
			
			if (column == TABLE_COL_MARKER
					&& value instanceof Boolean) {
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				if (((Boolean)value).booleanValue()) {
					if (isSelected) {
						lbl.setIcon(iconMarkedSelected);
					} else {
						lbl.setIcon(iconMarked);
					}
				} else {
					if (isSelected) {
						lbl.setIcon(iconDemarkedSelected);
					} else {
						lbl.setIcon(iconDemarked);
					}
				}
			} else if (column == TABLE_COL_STATUS && value instanceof Integer) {
				if (((Boolean) tblResult.getValueAt(row, TABLE_COL_MARKER))
						.booleanValue()) {
					int status = ((Integer) value).intValue();
					lbl.setHorizontalAlignment(SwingConstants.CENTER);
					if (status == 0) {
						lbl.setIcon(iconStatusInit);
					} else if (status == 1) {
						lbl.setIcon(iconStatusOk);
					} else if (status == 2) {
						lbl.setIcon(iconStatusError);
					}
				}
			} else if (value instanceof String) {
				lbl.setText((String) value);
			}
			if (column == TABLE_COL_RECORDS) {
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
			}

			if (isSelected) {
				lbl.setBackground(new Color(0, 0, 100));
				lbl.setForeground(Color.LIGHT_GRAY);
			}
			
			return lbl;
		}
	}
	
	class SmarttoolChangeValuesTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1355771906563987627L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}
