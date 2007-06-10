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
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportDataIntoTableExecutor;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This dialog has some options to specify how the file is imported into
 * the database.
 * 
 * @author Thorsten Mürell
 */
public class ImportFileDialog extends BaseInternalFrame {
	private static final long serialVersionUID = 3470927611018381204L;

	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(ImportFileDialog.class);
	
	private String[][] previewData = null;
	private List<String> importerColumns = new Vector<String>();
	
	private JTable previewTable = null;
	private JTable mappingTable = null;
	private JCheckBox headersIncluded = null;
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
		super(stringMgr.getString("ImportFileDialog.fileImport"), true);
		this.session = session;
		this.importer = importer;
		this.table = table;
		this.columns = columns;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		GUIUtils.makeToolWindow(this, true);

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
				"12dlu, 6dlu, 12dlu, 6dlu, 80dlu:grow, 6dlu, 80dlu:grow, 6dlu, pref");
		
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
		headersIncluded.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				GUIUtils.processOnSwingEventThread(new Runnable() {
					public void run() {
						ImportFileDialog.this.updatePreviewData();
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
		editBox.addItem("Skip");
		editBox.addItem("Fixed value");
		editBox.addItem("Autoincrement");
		editBox.addItem("NULL");
		
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
					headers[i] = "Column" + i;
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



