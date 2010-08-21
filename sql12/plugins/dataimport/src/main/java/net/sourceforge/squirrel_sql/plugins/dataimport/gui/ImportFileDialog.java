package net.sourceforge.squirrel_sql.plugins.dataimport.gui;
/*
 * Copyright (C) 2007 Thorsten Mürell
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportDataIntoTableExecutor;
import static net.sourceforge.squirrel_sql.plugins.dataimport.gui.SpecialColumnMapping.*;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * This dialog has some options to specify how the file is imported into
 * the database.
 * 
 * @author Thorsten Mürell
 */
public class ImportFileDialog extends DialogWidget
{	
	private static final long serialVersionUID = 3470927611018381204L;

	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(ImportFileDialog.class);	
	
	private String[][] previewData = null;
	private List<String> importerColumns = new Vector<String>();
	
	private JTable previewTable = null;
	private JTable mappingTable = null;
	private JCheckBox headersIncluded = null;
	private JCheckBox suggestColumns = null;
	private JCheckBox suggestColumnsIgnoreCase = null;
	private OkClosePanel btnsPnl = new OkClosePanel();
	
	private ISession session = null;
	private IFileImporter importer = null;
	private ITableInfo table = null;
	private TableColumnInfo[] columns = null;

	/**
	 * The standard constructor
	 * 
	 * @param session The session
	 * @param importer The file importer
	 * @param table The table to import into
	 * @param columns The columns of the import table
	 */
	public ImportFileDialog(ISession session, IFileImporter importer, ITableInfo table, TableColumnInfo[] columns) {
        //i18n[ImportFileDialog.fileImport=Import file]
		super(stringMgr.getString("ImportFileDialog.fileImport"), true, session.getApplication());
		this.session = session;
		this.importer = importer;
		this.table = table;
		this.columns = columns;

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		makeToolWindow(true);

		final JPanel content = new JPanel(new BorderLayout());
		content.add(createMainPanel(), BorderLayout.CENTER);
        setContentPane(content);
		btnsPnl.makeOKButtonDefault();
		btnsPnl.getRootPane().setDefaultButton(btnsPnl.getOKButton());
        pack();
	}
	
	private Component createMainPanel()
	{
		btnsPnl.addListener(new MyOkClosePanelListener());

		final FormLayout layout = new FormLayout(
				// Columns
				"left:pref:grow",
				// Rows
//				"12dlu, 6dlu, 12dlu, 6dlu, 80dlu:grow, 6dlu, 12dlu,              6dlu, 80dlu:grow, 6dlu, pref"
				"12dlu, 6dlu, 12dlu, 6dlu, 80dlu:grow, 6dlu, 12dlu, 2dlu, 12dlu, 6dlu, 80dlu:grow, 6dlu, pref"
				);
				
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		previewTable = new JTable();
		JScrollPane scrollPane = new JScrollPane(previewTable);
		
		mappingTable = new JTable(new ColumnMappingTableModel(columns));
		JScrollPane scrollPane2 = new JScrollPane(mappingTable);
		
        //i18n[ImportFileDialog.headersIncluded=Headers in first line]
		headersIncluded = new JCheckBox(stringMgr.getString("ImportFileDialog.headersIncluded"));
		headersIncluded.setSelected(true);
		headersIncluded.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIUtils.processOnSwingEventThread(new Runnable() {
					public void run() {
						ImportFileDialog.this.updatePreviewData();
					}
				});
			}
		});
		
		// i18n[ImportFileDialog.suggestColumns=Suggest columns (find matching columns)]
		suggestColumns = new JCheckBox(stringMgr.getString("ImportFileDialog.suggestColumns"));
		suggestColumns.setSelected(false);
		suggestColumns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				GUIUtils.processOnSwingEventThread(new Runnable() {
					public void run() {
						ImportFileDialog.this.suggestColumns();
					}
				});
			}
		});
		// i18n[ImportFileDialog.suggestColumnsIgnoreCase=ignore case]
		suggestColumnsIgnoreCase = new JCheckBox(stringMgr.getString("ImportFileDialog.suggestColumnsIgnoreCase"));
		suggestColumnsIgnoreCase.setSelected(false);
		suggestColumnsIgnoreCase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIUtils.processOnSwingEventThread(new Runnable() {
					public void run() {
						ImportFileDialog.this.suggestColumns();
					}
				});
			}
		});
		
		int y = 1;
        //i18n[ImportFileDialog.dataPreview=Data preview]
		builder.add(new JLabel(stringMgr.getString("ImportFileDialog.dataPreview")), cc.xy(1, y));
		
		y += 2;
		builder.add(headersIncluded, cc.xy(1, y));		

		y += 2;
		builder.add(scrollPane, cc.xy(1, y));
		
		y += 2;
		builder.add(suggestColumns, cc.xy(1, y));
		
		y += 2;
		builder.add(suggestColumnsIgnoreCase, cc.xy(1, y));
		
		y += 2;
		builder.add(scrollPane2, cc.xy(1, y));

		y += 2;
		builder.add(btnsPnl, cc.xywh(1, y, 1, 1));

		
		return builder.getPanel();
	}
	
	/**
	 * Sets the preview data for the dialog
	 * 
	 * @param data
	 */
	public void setPreviewData(String[][] data) {
		previewData = data;
		updatePreviewData();
	}
	
	private void updatePreviewData() {
		JComboBox editBox = new JComboBox();
		editBox.addItem(SKIP.getVisibleString());
		editBox.addItem(FIXED_VALUE.getVisibleString());
		editBox.addItem(AUTO_INCREMENT.getVisibleString());
		editBox.addItem(NULL.getVisibleString());
		
		editBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int selectedRow = mappingTable.getSelectedRow();
				if (selectedRow == -1) {
					return;
				}
				TableModel model = mappingTable.getModel();
				String comboValue = ((JComboBox)e.getSource()).getSelectedItem().toString();
				int fixedValueColumnIdx = ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN;
				if (comboValue.equals(AUTO_INCREMENT.getVisibleString())) {
					// If the user picks auto-increment, auto-fill the "Fixed value" column with 0 for the start 
					// value if it is currently empty.
					if (model.getValueAt(selectedRow, fixedValueColumnIdx) == null || 
						 "".equals(model.getValueAt(selectedRow, fixedValueColumnIdx) )) 
					{
						model.setValueAt("0", selectedRow, fixedValueColumnIdx);
					}
					
				} else if (!comboValue.equals(FIXED_VALUE.getVisibleString())) {
					// If the user chooses neither Fixed value nor Auto-Increment, then clear the "Fixed value" 
					// field if it has a value.
					model.setValueAt("", selectedRow, fixedValueColumnIdx);
				}
				mappingTable.clearSelection();
			}
			
		});
		
		if (previewData != null && previewData.length > 0) {
			String[] headers = new String[previewData[0].length];
			String[][] data = previewData;

			if (headersIncluded.isSelected()) {
				for (int i = 0; i < headers.length; i++) {
					headers[i] = data[0][i];
				}
				data = new String[previewData.length-1][];
				for (int i = 1; i < previewData.length; i++) {
					data[i-1] = previewData[i];
				}
			} else {
				for (int i = 0; i < headers.length; i++) {
					//i18n[ImportFileDialog.column=Column]
					headers[i] = stringMgr.getString("ImportFileDialog.column") + i;
				}
			}

			importerColumns.clear();
			for (int i = 0; i < headers.length; i++) {
				importerColumns.add(headers[i]);
			}

			for (String header : headers) {
				editBox.addItem(header);
			}
			previewTable.setModel(new DefaultTableModel(data, headers));
		}
		mappingTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editBox));
		((ColumnMappingTableModel) mappingTable.getModel()).resetMappings();
		if (suggestColumns.isSelected())
		{
			this.suggestColumns();
		}
	}
	
	public void suggestColumns()
	{
		final ColumnMappingTableModel columnMappingTableModel = ((ColumnMappingTableModel) mappingTable.getModel());
		final boolean ignorecase = suggestColumnsIgnoreCase.isSelected();		
		if (suggestColumns.isSelected())
		{
			for (String importerColumn : importerColumns )
			{
				if (null != importerColumn && !importerColumn.isEmpty())
				{
					final TableColumnInfo suggestedColumn = suggestColumn(importerColumn, ignorecase);
					if (suggestedColumn != null)
					{						
						final String suggestedColumnName = suggestedColumn.getColumnName();
						int row = columnMappingTableModel.findTableColumn(suggestedColumnName);
						columnMappingTableModel.setValueAt(importerColumn, row, ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN);
					}
				}
			}			
		}
		else
		{
			columnMappingTableModel.resetMappings();
		}
	}	
	
	private TableColumnInfo suggestColumn(final String importerColumn, final boolean ignoreCase) {
		for (TableColumnInfo colInfo : columns)
		{
			if (!ignoreCase && colInfo.getColumnName().equals(importerColumn))
			{
				return colInfo;
			} 
			else if (ignoreCase && colInfo.getColumnName().equalsIgnoreCase(importerColumn))
			{
				return colInfo;
			}
		}
		return null;
	}

	/**
	 * This is invoked if the user presses ok
	 */
	public void ok() {
		dispose();
		ImportDataIntoTableExecutor executor = new ImportDataIntoTableExecutor(session, table, columns, importerColumns, (ColumnMappingTableModel) mappingTable.getModel(), importer);
		executor.setSkipHeader(headersIncluded.isSelected());
		executor.execute();
	}
	
	private final class MyOkClosePanelListener implements IOkClosePanelListener
	{
		/**
		 * Callback for the ok key.
		 * 
		 * @param evt the event
		 */
		public void okPressed(OkClosePanelEvent evt)
		{
			ImportFileDialog.this.ok();
		}

		/**
		 * Callback for the close key.
		 * 
		 * @param evt the event
		 */
		public void closePressed(OkClosePanelEvent evt)
		{
			ImportFileDialog.this.dispose();
		}

		/**
		 * Callback for the cancel key.
		 * 
		 * @param evt the event
		 */
		public void cancelPressed(OkClosePanelEvent evt)
		{
			ImportFileDialog.this.dispose();
		}
	}
	
}



