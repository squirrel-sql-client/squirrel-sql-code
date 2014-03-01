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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.smarttools.SmarttoolsHelper;
import net.sourceforge.squirrel_sql.plugins.smarttools.comp.STButton;

import javax.swing.*;
import javax.swing.JTable.PrintMode;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class SmarttoolMissingIndicesFrame extends DialogWidget implements ISmarttoolFrame, ActionListener
{
	private static final long serialVersionUID = 3680564513241320485L;

	private final int START_WORKING = 1;

	private final int STOP_WORKING = 2;

	private final int COL_TABLENAME = 0;

	private final int COL_RECORDS = 1;

	private final int COL_PRIMARY = 2;

	private final int COL_UNIQUE = 3;

	private final int COL_SUMMARY = 4;

	// variables
	// ========================================================================
	// non visible
	// ------------------------------------------------------------------------
	// Logger for this class
	private final static ILogger log = LoggerController.createLogger(SmarttoolMissingIndicesFrame.class);

	private final static StringManager stringManager = StringManagerFactory.getStringManager(SmarttoolMissingIndicesFrame.class);

	private ISession session;

	private Thread threadWork = null;

	private boolean threadSuspended;

	private Vector<String> vecHeader = new Vector<String>();

	private Vector<Vector<Object>> vecData = new Vector<Vector<Object>>();

	// visible (gui)
	// ------------------------------------------------------------------------
	private JLabel lblTitleTable = new JLabel();

	private JLabel lblTablename = new JLabel();

	private JTextField tfTablename = new JTextField();

	private JRadioButton rbDisplayTypeAll = new JRadioButton();

	private ButtonGroup buttongroup1 = new ButtonGroup();

	private JRadioButton rbDisplayTypePK = new JRadioButton();

	private JRadioButton rbDisplayTypeUI = new JRadioButton();

	private JLabel lblDisplayType = new JLabel();

	private STButton btnStart = new STButton();

	private STButton btnStop = new STButton();

	private JLabel lblTitleTableResult = new JLabel();

	private JLabel lblFooterTableResult = new JLabel();

	private STButton btnPrint = new STButton();

	private STButton btnDdl = new STButton();

	private STButton btnRecordCount = new STButton();

	private JTable tblResult = null;

	private JProgressBar pbMain = new JProgressBar();

	/**
	 * Constructor
	 * 
	 * @param app
	 * @param rsrc
	 * @param session
	 * @param tab
	 */
	public SmarttoolMissingIndicesFrame(ISession session, String title) {
		super("Smarttool - " + title, true, true, true, true, session.getApplication());
		this.session = session;

		initLayout();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.moveToFront();
	}

	private interface i18n
	{
		// Labels
		String LBL_TITLE_USED_TABLES = stringManager.getString("missingindices.title.tables");

		String LBL_TABLENAME = stringManager.getString("missingindices.lbl.table.name");

		String LBL_DISPLAYTYPE = stringManager.getString("missingindices.lbl.display.type");

		String LBL_RB_DISPLAYTYPE_ALL = stringManager.getString("missingindices.lbl.radiobutton.display.type.all");

		String LBL_RB_DISPLAYTYPE_PK = stringManager.getString("missingindices.lbl.radiobutton.display.type.primarykey");

		String LBL_RB_DISPLAYTYPE_UI = stringManager.getString("missingindices.lbl.radiobutton.display.type.uniqueindex");

		String TABLECOLUMN_PRIMARY_KEY_COUNT = stringManager.getString("missingindices.lbl.tablecolumn.primarykey");

		String TABLECOLUMN_UNIQUE_INDEX_COUNT = stringManager.getString("missingindices.lbl.tablecolumn.uniqueindices");

		String TABLECOLUMN_INDEX_COUNT = stringManager.getString("missingindices.lbl.tablecolumn.indices");

		String TABLECOLUMN_RECORD_COUNT = stringManager.getString("missingindices.lbl.tablecolumn.records");

		String LBL_BTN_START = stringManager.getString("global.lbl.btn.start");

		String LBL_BTN_STOP = stringManager.getString("global.lbl.btn.stop");

		String LBL_BTN_PRINT = stringManager.getString("global.lbl.btn.print");

		String LBL_BTN_DDL = stringManager.getString("missingindices.lbl.btn.ddl");

		String TOOLTIP_BTN_DDL = stringManager.getString("missingindices.tooltip.btn.ddl");

		String LBL_BTN_RECORDS = stringManager.getString("missingindices.lbl.btn.records");

		String TOOLTIP_BTN_RECORDS = stringManager.getString("missingindices.tooltip.btn.records");

		// Tooltips and questions
		String TOOLTIP_WILDCARD = stringManager.getString("missingindices.tooltip.wildcard");

		String TOOLTIP_RB_DISPLAYTYPE_ALL = stringManager.getString("missingindices.tooltip.radiobutton.display.type.all");

		String TOOLTIP_RB_DISPLAYTYPE_PK = stringManager.getString("missingindices.tooltip.radiobutton.display.type.primarykey");

		String TOOLTIP_RB_DISPLAYTYPE_UI = stringManager.getString("missingindices.tooltip.radiobutton.display.type.uniqueindex");

		String TOOLTIP_BTN_PRINT = stringManager.getString("global.tooltip.btn.print");

		// Global misc
		String GLOBAL_RECORDS = stringManager.getString("global.records");

		String GLOBAL_TABLE = stringManager.getString("global.table");

		String GLOBAL_COLUMN = stringManager.getString("global.column");

		String GLOBAL_DATATYPE = stringManager.getString("global.datatype");

		String GLOBAL_PAGE = stringManager.getString("global.page");

		String GLOBAL_ALIAS = stringManager.getString("global.alias");

		// Questions
		String QUESTION_CANCEL_WORK = stringManager.getString("missingindices.question.cancel.work");

		String QUESTION_CANCEL_WORK_TITLE = stringManager.getString("missingindices.question.cancel.work.title");

		// Errors
		String ERROR_READ_CHECKING_DATA = stringManager.getString("missingindices.error.read.checking.data");

		String ERROR_NO_ROW_SELECTED = stringManager.getString("missingindices.error.no.row.selected");

		String ERROR_RECORD_COUNT = stringManager.getString("missingindices.error.reading.recordcount");

		// Infos
		String INFO_FINISHED = stringManager.getString("missingindices.info.finished");

		String INFO_REPORT = stringManager.getString("missingindices.info.report");
	}

	private void initLayout()
	{
		this.getContentPane().setLayout(new BorderLayout());
		createTableHeader();
		tblResult = new JTable(vecData, vecHeader);
		this.getContentPane().add(createPanel());

		initVisualObjects();
	}

	public JPanel createPanel()
	{
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
		   "FILL:4DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE",
		   "CENTER:2DLU:NONE,FILL:DEFAULT:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		pbMain.setName("pbMain");
		pbMain.setValue(25);
		jpanel1.add(pbMain, cc.xywh(2, 15, 3, 1));

		jpanel1.add(createpanelTableAndColumn(), cc.xy(2, 2));
		jpanel1.add(createpanelButton(), cc.xy(4, 2));
		lblTitleTableResult.setBackground(new Color(102, 102, 102));
		lblTitleTableResult.setName("lblTitleTableResult");
		lblTitleTableResult.setOpaque(true);
		lblTitleTableResult.setText(" Searching for ...");
		jpanel1.add(lblTitleTableResult, cc.xy(2, 4));

		lblFooterTableResult.setBackground(new Color(102, 102, 102));
		lblFooterTableResult.setName("lblFooterTableResult");
		lblFooterTableResult.setOpaque(true);
		lblFooterTableResult.setText(" Finshed in ...");
		jpanel1.add(lblFooterTableResult, cc.xywh(2, 13, 3, 1));

		btnPrint.setActionCommand("Print");
		btnPrint.setName("btnPrint");
		btnPrint.setText("Print");
		jpanel1.add(btnPrint, cc.xy(4, 4));

		tblResult.setName("tblResult");
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(tblResult);
		jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane1, cc.xywh(2, 7, 1, 5));

		btnDdl.setActionCommand("ddl");
		btnDdl.setName("btnDdl");
		btnDdl.setText("ddl");
		btnDdl.setToolTipText("create ddl statement for primary key or unique index");
		jpanel1.add(btnDdl, cc.xy(4, 7));

		btnRecordCount.setActionCommand("records");
		btnRecordCount.setName("btnRecordCount");
		btnRecordCount.setText("records");
		btnRecordCount.setToolTipText("determine the record count of the displayed tables");
		jpanel1.add(btnRecordCount, cc.xy(4, 9));

		return jpanel1;
	}

	public JPanel createpanelTableAndColumn()
	{
		JPanel panelTableAndColumn = new JPanel();
		panelTableAndColumn.setName("panelTableAndColumn");
		EtchedBorder etchedborder1 = new EtchedBorder(EtchedBorder.RAISED, null, null);
		panelTableAndColumn.setBorder(etchedborder1);
		FormLayout formlayout1 = new FormLayout(
		   "FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE",
		   "CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE");
		CellConstraints cc = new CellConstraints();
		panelTableAndColumn.setLayout(formlayout1);

		lblTitleTable.setBackground(new Color(102, 102, 102));
		lblTitleTable.setName("lblTitleTable");
		lblTitleTable.setOpaque(true);
		lblTitleTable.setText(" Used tables");
		panelTableAndColumn.add(lblTitleTable, cc.xywh(1, 1, 5, 1));

		lblTablename.setName("lblTablename");
		lblTablename.setText("table name");
		panelTableAndColumn.add(lblTablename, cc.xy(2, 3));

		tfTablename.setName("tfTablename");
		panelTableAndColumn.add(tfTablename, cc.xy(4, 3));

		panelTableAndColumn.add(createpanelDisplayType(), cc.xy(4, 5));
		lblDisplayType.setName("lblDisplayType");
		lblDisplayType.setText("display");
		panelTableAndColumn.add(lblDisplayType, cc.xy(2, 5));

		return panelTableAndColumn;
	}

	public JPanel createpanelDisplayType()
	{
		JPanel panelDisplayType = new JPanel();
		panelDisplayType.setName("panelDisplayType");
		FormLayout formlayout1 = new FormLayout(
		   "FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE",
		   "CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		panelDisplayType.setLayout(formlayout1);

		rbDisplayTypeAll.setActionCommand("all entries");
		rbDisplayTypeAll.setName("rbDisplayTypeAll");
		rbDisplayTypeAll.setText("all entries");
		buttongroup1.add(rbDisplayTypeAll);
		panelDisplayType.add(rbDisplayTypeAll, cc.xy(1, 1));

		rbDisplayTypePK.setActionCommand("missing primary key");
		rbDisplayTypePK.setName("rbDisplayTypePK");
		rbDisplayTypePK.setText("missing primary key");
		buttongroup1.add(rbDisplayTypePK);
		panelDisplayType.add(rbDisplayTypePK, cc.xy(3, 1));

		rbDisplayTypeUI.setActionCommand("missing unique index");
		rbDisplayTypeUI.setName("rbDisplayTypeUI");
		rbDisplayTypeUI.setText("missing unique index");
		buttongroup1.add(rbDisplayTypeUI);
		panelDisplayType.add(rbDisplayTypeUI, cc.xy(5, 1));

		return panelDisplayType;
	}

	public JPanel createpanelButton()
	{
		JPanel panelButton = new JPanel();
		panelButton.setName("panelButton");
		FormLayout formlayout1 = new FormLayout(
		   "FILL:DEFAULT:NONE", "CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		panelButton.setLayout(formlayout1);

		btnStart.setActionCommand("Start");
		btnStart.setName("btnStart");
		btnStart.setText("Start");
		panelButton.add(btnStart, cc.xy(1, 1));

		btnStop.setActionCommand("Stop");
		btnStop.setName("btnStop");
		btnStop.setText("Stop");
		panelButton.add(btnStop, cc.xy(1, 3));

		return panelButton;
	}

	public void setFocusToFirstEmptyInputField()
	{
		// nothing to do
	}

	private void initVisualObjects()
	{
		lblTitleTable.setText(this.getTitle()); // i18n.LBL_TITLE_USED_TABLES);
		lblTablename.setText(i18n.LBL_TABLENAME);
		lblTitleTableResult.setText(" " + i18n.INFO_REPORT);
		lblFooterTableResult.setText("");

		lblDisplayType.setText(i18n.LBL_DISPLAYTYPE);

		tfTablename.setToolTipText(i18n.TOOLTIP_WILDCARD);

		rbDisplayTypeAll.setText(i18n.LBL_RB_DISPLAYTYPE_ALL);
		rbDisplayTypeAll.setToolTipText(i18n.TOOLTIP_RB_DISPLAYTYPE_ALL);
		rbDisplayTypePK.setText(i18n.LBL_RB_DISPLAYTYPE_PK);
		rbDisplayTypePK.setToolTipText(i18n.TOOLTIP_RB_DISPLAYTYPE_PK);
		rbDisplayTypeUI.setText(i18n.LBL_RB_DISPLAYTYPE_UI);
		rbDisplayTypeUI.setToolTipText(i18n.TOOLTIP_RB_DISPLAYTYPE_UI);
		rbDisplayTypeUI.setSelected(true);

		btnStart.setText(i18n.LBL_BTN_START);
		btnStart.setIcon(SmarttoolsHelper.loadIcon("start16x16.png"));
		btnStart.addActionListener(this);

		btnStop.setText(i18n.LBL_BTN_STOP);
		btnStop.setIcon(SmarttoolsHelper.loadIcon("stop16x16.png"));
		btnStop.addActionListener(this);
		btnStop.setEnabled(false);

		btnPrint.setText(i18n.LBL_BTN_PRINT);
		btnPrint.setIcon(SmarttoolsHelper.loadIcon("printer16x16.png"));
		btnPrint.addActionListener(this);
		btnPrint.setEnabled(false);

		btnDdl.setText(i18n.LBL_BTN_DDL);
		btnDdl.setToolTipText(i18n.TOOLTIP_BTN_DDL);
		btnDdl.setIcon(SmarttoolsHelper.loadIcon("change16x16.png"));
		btnDdl.addActionListener(this);
		btnDdl.setEnabled(false);

		btnRecordCount.setText(i18n.LBL_BTN_RECORDS);
		btnRecordCount.setToolTipText(i18n.TOOLTIP_BTN_RECORDS);
		btnRecordCount.setIcon(SmarttoolsHelper.loadIcon("count16x16.png"));
		btnRecordCount.addActionListener(this);
		btnRecordCount.setEnabled(false);

		initTableColumnWidth();
		tblResult.setDefaultRenderer(Object.class, new Renderer());

		pbMain.setValue(0);
		pbMain.setStringPainted(true);
	}

	private void createTableHeader()
	{
		vecHeader.add(i18n.GLOBAL_TABLE);
		vecHeader.add(i18n.TABLECOLUMN_RECORD_COUNT);
		vecHeader.add(i18n.TABLECOLUMN_PRIMARY_KEY_COUNT);
		vecHeader.add(i18n.TABLECOLUMN_UNIQUE_INDEX_COUNT);
		vecHeader.add(i18n.TABLECOLUMN_INDEX_COUNT);
	}

	// controlling
	// ------------------------------------------------------------------------
	public void controlComponents(int type)
	{
		if (type == START_WORKING || type == STOP_WORKING)
		{
			boolean b = type == STOP_WORKING;
			tfTablename.setEnabled(b);
			btnStart.setEnabled(b);
			btnStop.setEnabled(!b);
			btnPrint.setEnabled(b && tblResult.getRowCount() > 0);
			btnDdl.setEnabled(btnPrint.isEnabled());
			btnRecordCount.setEnabled(btnPrint.isEnabled());
		}
	}

	// user checks
	// ------------------------------------------------------------------------
	private void startWork()
	{
		controlComponents(START_WORKING);
		threadWork = new ThreadWork();
		threadWork.start();
	}

	private void stopWork()
	{
		threadSuspended = true;
		if (JOptionPane.showConfirmDialog(
		   session.getApplication().getMainFrame(), i18n.QUESTION_CANCEL_WORK, i18n.QUESTION_CANCEL_WORK_TITLE,
		   JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			threadWork = null;
			controlComponents(STOP_WORKING);
		}
		threadSuspended = false;
	}

	private void printResult()
	{
		try
		{
			MessageFormat headerFormat = new MessageFormat(lblTitleTable.getText());
			MessageFormat footerFormat = new MessageFormat(i18n.GLOBAL_ALIAS + ": "
			      + session.getAlias().getName() + " | "
			      + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date())
			      + " | " + i18n.GLOBAL_PAGE + " {0} ");

			tblResult.print(PrintMode.FIT_WIDTH, headerFormat, footerFormat);
		} catch (PrinterException e)
		{
			log.error(e.getLocalizedMessage());
		}
	}

	private void getRecordCounts()
	{
		Statement stmt = null;
		try
		{
			stmt = session.getSQLConnection().createStatement();
			for (int i = 0; i < tblResult.getRowCount() - 1; i++)
			{
				int records = getRecordCount(stmt, (String) tblResult.getValueAt(i, COL_TABLENAME));
				tblResult.setValueAt(new Integer(records), i, COL_RECORDS);
				((DefaultTableModel) tblResult.getModel()).fireTableDataChanged();
			}
			stmt.close();
			stmt = null;
		} catch (SQLException e)
		{
			log.error(e.getLocalizedMessage());
		} finally
		{
			SQLUtilities.closeStatement(stmt);
		}
	}

	private int getRecordCount(Statement stmt, String tablename) throws SQLException
	{
		int recordCount = 0;

		ResultSet rs = null;
		try
		{
			rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tablename);
			if (rs.next())
			{
				recordCount = rs.getInt(1);
			}
		} finally
		{
			SQLUtilities.closeResultSet(rs);
		}

		return recordCount;
	}

	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == btnStart)
		{
			startWork();
		} else if (e.getSource() == btnStop)
		{
			stopWork();
		} else if (e.getSource() == btnPrint)
		{
			printResult();
		} else if (e.getSource() == btnDdl)
		{
			if (tblResult.getSelectedRow() > -1)
			{
				new SmarttoolCreateIndexD(null, session, (String) tblResult.getValueAt(
				   tblResult.getSelectedRow(), COL_TABLENAME));
			} else
			{
				JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), i18n.ERROR_NO_ROW_SELECTED);
			}
		}
		if (e.getSource() == btnRecordCount)
		{
			getRecordCounts();
		}
	}

	// ------------------------------------------------------------------------
	class ThreadWork extends Thread
	{
		private Thread thisThread = null;

		@Override
		public void run()
		{
			super.run();
			thisThread = Thread.currentThread();

			startTest();

			controlComponents(STOP_WORKING);
			threadWork = null;
		}

		private boolean isThreadInvalid()
		{
			if (thisThread != threadWork)
			{
				return true;
			}
			try
			{
				synchronized (this)
				{
					while (threadSuspended)
					{
						wait(200);
					}
				}
			} catch (InterruptedException e)
			{
			}
			return false;
		}

		private void startTest()
		{
			long startTime = System.currentTimeMillis();
			boolean error = false;

			((DefaultTableModel) tblResult.getModel()).setDataVector(new Vector<Vector<Object>>(), vecHeader);

			String tableNamePattern = tfTablename.getText().trim();
			if (tableNamePattern.length() == 0)
			{
				tableNamePattern = "%";
			}
			try
			{
				ITableInfo[] tableInfoArray = session.getMetaData().getTables(
				   null, null, tableNamePattern, new String[]
					   { "TABLE" }, null);
				lblFooterTableResult.setText("");
				pbMain.setValue(0);
				pbMain.setMaximum(tableInfoArray.length);

				for (int iTableInfo = 0; iTableInfo < tableInfoArray.length; iTableInfo++)
				{
					ITableInfo tableInfo = tableInfoArray[iTableInfo];
					pbMain.setString(tableInfo.getSimpleName() + " " + (iTableInfo + 1) + "/"
					      + pbMain.getMaximum());

					if (!checkIndices(tableInfo))
					{
						error = true;
					}

					pbMain.setValue(iTableInfo + 1);
					pbMain.repaint();
					if (isThreadInvalid())
					{
						break;
					}
				}
				long diffTime = System.currentTimeMillis() - startTime;
				lblFooterTableResult.setText(" " + i18n.INFO_FINISHED + " " + diffTime + " ms");
				if (error)
				{
					JOptionPane.showMessageDialog(null, i18n.ERROR_RECORD_COUNT);
				}
			} catch (SQLException e)
			{
				log.error(e);
				JOptionPane.showMessageDialog(null, i18n.ERROR_READ_CHECKING_DATA);
			}
		}

		private boolean checkIndices(ITableInfo tableInfo) throws SQLException
		{
			int uniqueIndexCount = 0;
			int recordCount = 0;
			List<IndexInfo> listIndexInfo = session.getMetaData().getIndexInfo(tableInfo);
			for (int iIndexInfo = 0; iIndexInfo < listIndexInfo.size(); iIndexInfo++)
			{
				if (!listIndexInfo.get(iIndexInfo).isNonUnique())
				{
					uniqueIndexCount++;
				}
			}

			PrimaryKeyInfo[] arrayPrimaryKeyInfo = session.getMetaData().getPrimaryKey(tableInfo);

			if (rbDisplayTypeAll.isSelected()
			      || (rbDisplayTypePK.isSelected() && arrayPrimaryKeyInfo.length == 0)
			      || (uniqueIndexCount == 0))
			{
				addTableEntry(
				   tableInfo.getSimpleName(), recordCount, arrayPrimaryKeyInfo.length, uniqueIndexCount,
				   listIndexInfo.size());
			}

			return recordCount > -1;
		}

		private void addTableEntry(String tableName, int recordCount, int primaryKey, int uniqueIndices,
		      int indexSummary)
		{
			Vector<Object> vecRow = new Vector<Object>();
			vecRow.add(tableName);
			vecRow.add(recordCount);
			vecRow.add(primaryKey);
			vecRow.add(uniqueIndices);
			vecRow.add(indexSummary);
			DefaultTableModel tm = (DefaultTableModel) tblResult.getModel();
			tm.addRow(vecRow);
			tm.fireTableDataChanged();
			initTableColumnWidth();
		}
	}

	private void initTableColumnWidth()
	{
		TableColumnModel tcm = tblResult.getTableHeader().getColumnModel();
		tcm.getColumn(COL_TABLENAME).setPreferredWidth(190);
		tcm.getColumn(COL_RECORDS).setPreferredWidth(80);
		tcm.getColumn(COL_PRIMARY).setPreferredWidth(90);
		tcm.getColumn(COL_UNIQUE).setPreferredWidth(100);
		tcm.getColumn(COL_SUMMARY).setPreferredWidth(60);
	}

	private class Renderer implements TableCellRenderer
	{
		private ImageIcon iconMarked = SmarttoolsHelper.loadIcon("gridMarked16x16.png");

		private ImageIcon iconDemarked = SmarttoolsHelper.loadIcon("gridDemarked16x16.png");

		private ImageIcon iconMarkedSelected = SmarttoolsHelper.loadIcon("gridMarkedSelected16x16.png");

		private ImageIcon iconDemarkedSelected = SmarttoolsHelper.loadIcon("gridDemarkedSelected16x16.png");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		      boolean hasFocus, int row, int column)
		{
			JLabel lbl = new JLabel(value + "");
			lbl.setOpaque(true);

			if (isSelected)
			{
				lbl.setBackground(tblResult.getSelectionBackground());
				lbl.setForeground(tblResult.getSelectionForeground());
			} else
			{
				lbl.setBackground(tblResult.getBackground());
				lbl.setForeground(tblResult.getForeground());
			}
			if (column >= COL_RECORDS)
			{
				if (column == COL_PRIMARY)
				{
					lbl.setText("");
					if (((Integer) value).intValue() == 0)
					{
						if (isSelected)
						{
							lbl.setIcon(iconDemarkedSelected);
						} else
						{
							lbl.setIcon(iconDemarked);
						}
					} else
					{
						if (isSelected)
						{
							lbl.setIcon(iconMarkedSelected);
						} else
						{
							lbl.setIcon(iconMarked);
						}
					}
				}
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				if (((Integer) value).intValue() == 0)
				{
					if (column == COL_PRIMARY)
					{
						lbl.setBackground(Color.ORANGE);
					} else if (column > COL_PRIMARY)
					{
						lbl.setBackground(Color.RED);
					}
				}
			} else
			{
				lbl.setHorizontalAlignment(SwingConstants.LEADING);
			}

			return lbl;
		}

	}
}
