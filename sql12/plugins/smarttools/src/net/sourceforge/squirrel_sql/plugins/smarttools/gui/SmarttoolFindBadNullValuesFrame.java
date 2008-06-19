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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
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

public class SmarttoolFindBadNullValuesFrame extends BaseInternalFrame
		implements ISmarttoolFrame, ActionListener {
	private static final long serialVersionUID = -1504852937961154906L;

	private final String INDENT = "   ";
	private final int START_WORKING = 1;
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

	private STButton btnStart = new STButton();
	private STButton btnStop = new STButton();
	private JLabel lblTitleTableResult = new JLabel();
	private JLabel lblFooterTableResult = new JLabel();
	private STButton btnPrint = new STButton();
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
	public SmarttoolFindBadNullValuesFrame(ISession session, String title) {
		super("Smarttool - " + title,
				true, true, true, true);
		this.session = session;

		initLayout();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.moveToFront();
	}

	private interface i18n {
		// Labels
		String LBL_TITLE_USED_TABLES = stringManager.getString("badnullvalues.title.tables");

		String LBL_TABLENAME = stringManager.getString("badnullvalues.lbl.table.name");

		String LBL_BTN_START = stringManager.getString("global.lbl.btn.start");
		String LBL_BTN_STOP = stringManager.getString("global.lbl.btn.stop");
		String LBL_BTN_PRINT = stringManager.getString("global.lbl.btn.print");

		// Tooltips and questions
		String TOOLTIP_WILDCARD = stringManager.getString("badnullvalues.tooltip.wildcard");
		String TOOLTIP_BTN_PRINT = stringManager.getString("global.tooltip.btn.print");

		// Global misc
		String GLOBAL_RECORDS = stringManager.getString("global.records");
		String GLOBAL_TABLE = stringManager.getString("global.table");
		String GLOBAL_COLUMN = stringManager.getString("global.column");
		String GLOBAL_DATATYPE = stringManager.getString("global.datatype");
		String GLOBAL_PAGE = stringManager.getString("global.page");
		String GLOBAL_ALIAS = stringManager.getString("global.alias");

		// Questions
		String QUESTION_CANCEL_WORK = stringManager.getString("badnullvalues.question.cancel.work");
		String QUESTION_CANCEL_WORK_TITLE = stringManager.getString("badnullvalues.question.cancel.work.title");

		// Errors
		String ERROR_READ_CHECKING_DATA = stringManager.getString("badnullvalues.error.read.checking.data");
		String ERROR_ON_TABLE = stringManager.getString("badnullvalues.error.on.table");

		// Infos
		String INFO_FINISHED = stringManager.getString("badnullvalues.info.finished");
		String INFO_REPORT = stringManager.getString("badnullvalues.info.report");
	}

	private void initLayout() {
		this.setLayout(new BorderLayout());
		createTableHeader();
		tblResult = new JTable(vecData, vecHeader);
		this.add(createPanel());

		initVisualObjects();
	}

	public JPanel createPanel() {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:4DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE",
				"CENTER:2DLU:NONE,FILL:DEFAULT:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:2DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		pbMain.setName("pbMain");
		pbMain.setValue(25);
		jpanel1.add(pbMain, cc.xywh(2, 11, 3, 1));

		tblResult.setName("taResult");
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(tblResult);
		jscrollpane1
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane1, cc.xywh(2, 7, 3, 1));

		jpanel1.add(createpanelTableAndColumn(), cc.xy(2, 2));
		jpanel1.add(createPanelButton(), cc.xy(4, 2));
		lblTitleTableResult.setBackground(new Color(102, 102, 102));
		lblTitleTableResult.setName("lblTitleTableResult");
		lblTitleTableResult.setOpaque(true);
		lblTitleTableResult.setText(" Searching for ...");
		jpanel1.add(lblTitleTableResult, cc.xy(2, 4));

		lblFooterTableResult.setBackground(new Color(102, 102, 102));
		lblFooterTableResult.setName("lblFooterTableResult");
		lblFooterTableResult.setOpaque(true);
		lblFooterTableResult.setText(" Finshed in ...");
		jpanel1.add(lblFooterTableResult, cc.xywh(2, 9, 3, 1));

		btnPrint.setActionCommand("Print");
		btnPrint.setName("btnPrint");
		btnPrint.setText("Print");
		jpanel1.add(btnPrint, cc.xy(4, 4));

		return jpanel1;
	}

	public JPanel createpanelTableAndColumn() {
	    JPanel jpanel1 = new JPanel();
		jpanel1.setName("panelTableAndColumn");
		EtchedBorder etchedborder1 = new EtchedBorder(EtchedBorder.RAISED,
				null, null);
		jpanel1.setBorder(etchedborder1);
		FormLayout formlayout1 = new FormLayout(
				"FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE",
				"CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		lblTitleTable.setBackground(new Color(102, 102, 102));
		lblTitleTable.setName("lblTitleTable");
		lblTitleTable.setOpaque(true);
		lblTitleTable.setText(" Used tables");
		jpanel1.add(lblTitleTable, cc.xywh(1, 1, 5, 1));

		lblTablename.setName("lblTablename");
		lblTablename.setText("table name");
		jpanel1.add(lblTablename, cc.xy(2, 3));

		tfTablename.setName("tfTablename");
		jpanel1.add(tfTablename, cc.xy(4, 3));

		return jpanel1;
	}

	public JPanel createPanelButton() {
		JPanel jpanel1 = new JPanel();
		jpanel1.setName("panelButton");
		FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		btnStart.setActionCommand("Start");
		btnStart.setName("btnStart");
		btnStart.setText("Start");
		jpanel1.add(btnStart, cc.xy(1, 1));

		btnStop.setActionCommand("Stop");
		btnStop.setName("btnStop");
		btnStop.setText("Stop");
		jpanel1.add(btnStop, cc.xy(1, 3));

		return jpanel1;
	}

	public void setFocusToFirstEmptyInputField() {
		// nothing to do
	}

	private void initVisualObjects() {
		lblTitleTable.setText(i18n.LBL_TITLE_USED_TABLES);
		lblTablename.setText(i18n.LBL_TABLENAME);
		lblTitleTableResult.setText(" "
				+ i18n.INFO_REPORT);
		lblFooterTableResult.setText("");

		tfTablename.setToolTipText(i18n.TOOLTIP_WILDCARD);

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


		pbMain.setValue(0);
		pbMain.setStringPainted(true);
	}

	private void createTableHeader() {
		vecHeader.add(i18n.GLOBAL_TABLE);
		vecHeader.add(i18n.GLOBAL_COLUMN);
		vecHeader.add(i18n.GLOBAL_DATATYPE);
		vecHeader.add(i18n.GLOBAL_RECORDS);
	}

	// controlling
	// ------------------------------------------------------------------------
	public void controlComponents(int type) {
		if (type == START_WORKING 
				|| type == STOP_WORKING) {
			boolean b = type == STOP_WORKING;
			tfTablename.setEnabled(b);
			btnStart.setEnabled(b);
			btnStop.setEnabled(!b);
			btnPrint.setEnabled(b
					&& tblResult.getRowCount() > 0);
		}
	}

	// user checks
	// ------------------------------------------------------------------------
	private void startWork() {
		controlComponents(START_WORKING);
		threadWork = new ThreadWork();
		threadWork.start();
	}

	private void stopWork() {
		threadSuspended = true;
		if (JOptionPane.showConfirmDialog(this, i18n.QUESTION_CANCEL_WORK,
				i18n.QUESTION_CANCEL_WORK_TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			threadWork = null;
			controlComponents(STOP_WORKING);
		}
		threadSuspended = false;
	}

	private void printResult() {
		SmarttoolsHelper.printTable(tblResult, this.getTitle(), 
					i18n.GLOBAL_ALIAS + ": " + session.getAlias().getName()
					+ " | " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date())
					+ " | " + i18n.GLOBAL_PAGE + " {0}");
	}
	
	// ########################################################################
	// ########## events
	// ########################################################################
	// ------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			startWork();
		} else if (e.getSource() == btnStop) {
			stopWork();
		} else if (e.getSource() == btnPrint) {
			printResult();
		}
	}

	// ------------------------------------------------------------------------
	class ThreadWork extends Thread {
		private Thread thisThread = null;

		@Override
		public void run() {
			super.run();
			thisThread = Thread.currentThread();

			startTest();

			controlComponents(STOP_WORKING);
			threadWork = null;
		}

		private boolean isThreadInvalid() {
			if (thisThread != threadWork) {
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

		private void startTest() {
			long startTime = System.currentTimeMillis();

			((DefaultTableModel)tblResult.getModel()).setDataVector(new Vector<Vector<Object>>(), vecHeader);
			
			String tableNamePattern = tfTablename.getText().trim();
			if (tableNamePattern.length() == 0) {
				tableNamePattern = "%";
			}
			try {
				ITableInfo[] tableInfoArray = session.getMetaData().getTables(
						null, null, tableNamePattern, new String[] { "TABLE" },
						null);
				lblFooterTableResult.setText("");
				pbMain.setValue(0);
				pbMain.setMaximum(tableInfoArray.length);
			
				for (int i = 0; i < tableInfoArray.length; i++) {
					ITableInfo tableInfo = tableInfoArray[i];
					pbMain.setString(tableInfo.getSimpleName() + " " + (i + 1)
							+ "/" + pbMain.getMaximum());

					checkColumns(tableInfo);

					pbMain.setValue(i + 1);
					pbMain.repaint();
					if (isThreadInvalid()) {
						break;
					}
				}
				long diffTime = System.currentTimeMillis() - startTime;
				lblFooterTableResult.setText(" " + i18n.INFO_FINISHED + " "
						+ diffTime + " ms");
			} catch (SQLException e) {
				log.error(e);
				JOptionPane.showMessageDialog(null,
						i18n.ERROR_READ_CHECKING_DATA);
			}
		}

		private void checkColumns(ITableInfo tableInfo) throws SQLException {
			TableColumnInfo[] columnInfos = session.getMetaData().getColumnInfo(tableInfo);
			Statement stmt = session.getSQLConnection().createStatement();
			int resultFound = 0;

			for (int c = 0; c < columnInfos.length; c++) {
				TableColumnInfo tableColumnInfo = columnInfos[c];
				String sql = null;

				if (tableColumnInfo.isNullAllowed() == 0) {
					sql = "SELECT COUNT(*) FROM " + tableInfo.getSimpleName()
							+ " WHERE " + tableColumnInfo.getColumnName()
							+ " IS NULL";
				}

				if (sql != null) {
					try {
						resultFound = SmarttoolsHelper.checkColumnData(stmt, sql);
						if (resultFound > 0) {
							addTableEntry(tableInfo.getSimpleName(),
									tableColumnInfo.getColumnName(),
									SmarttoolsHelper.getDataTypeForDisplay(tableColumnInfo),
									resultFound + "");
						}
					} catch (SQLException e) {
						String text = INDENT + i18n.ERROR_ON_TABLE + " ["
								+ tableInfo.getSimpleName() + "] "
								+ i18n.GLOBAL_COLUMN + " ["
								+ tableColumnInfo.getColumnName() + "] :"
								+ e.getLocalizedMessage();
						addTableEntry(tableInfo.getSimpleName(),
								tableColumnInfo.getColumnName(),
								SmarttoolsHelper.getDataTypeForDisplay(tableColumnInfo),
								i18n.ERROR_ON_TABLE);
						log.error(text);
					}
				}
			}
			stmt.close();
		}

		private void addTableEntry(String tableName, String columnName,
				String dataType, String recordsFound) {
			Vector<Object> vecRow = new Vector<Object>();
			vecRow.add(tableName);
			vecRow.add(columnName);
			vecRow.add(dataType);
			vecRow.add(recordsFound);
			DefaultTableModel tm = (DefaultTableModel) tblResult.getModel();
			tm.addRow(vecRow);
			tm.fireTableDataChanged();
		}
	}
}
