package net.sourceforge.squirrel_sql.plugins.dataimport.action;
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
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportFileType;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.ImportFileDialog;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.FileImporterFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;

/**
 * This command shows the necessary dialogs to import a file.
 * 
 * @author Thorsten Mürell
 */
public class ImportTableDataCommand implements ICommand {
	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(ImportTableDataCommand.class);
	
	private ISession session;
	private ITableInfo table;
	
	
	/**
	 * The constructor.
	 * 
	 * @param session The session to work in
	 * @param table The table to import the data
	 */
	public ImportTableDataCommand(ISession session, ITableInfo table) {
		super();
		this.session = session;
		this.table = table;
	}

	/**
	 * This is the command action.
	 * 
	 * It shows a file open dialog and then the specific import options for the file
	 * importer.
	 * 
	 * Then the column mapping dialog is shown.
	 */
	public void execute() {
		JFileChooser openFile = new JFileChooser();
		
		int res = openFile.showOpenDialog(session.getApplication().getMainFrame());
		
		if (res == JFileChooser.APPROVE_OPTION) {
			File f = openFile.getSelectedFile();
			try {
				TableColumnInfo[] columns = session.getMetaData().getColumnInfo(table);
				
				ImportFileType type = determineType(f);
				
				IFileImporter importer = FileImporterFactory.createImporter(type, f);
				
				if (importer.getConfigurationPanel() != null) {
					//i18n[ImportTableDataCommand.settingsDialogTitle=Import file settings]
					final JDialog dialog = new JDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportTableDataCommand.settingsDialogTitle"), true);
					StateListener dialogState = new StateListener(dialog);
					dialog.setLayout(new BorderLayout());
					dialog.add(importer.getConfigurationPanel(), BorderLayout.CENTER);
					OkClosePanel buttons = new OkClosePanel();
					//i18n[ImportTableDataCommand.cancel=Cancel]
					buttons.getCloseButton().setText(stringMgr.getString("ImportTableDataCommand.cancel"));
					buttons.addListener(dialogState);
					dialog.add(buttons, BorderLayout.SOUTH);
					dialog.pack();
					GUIUtils.centerWithinParent(dialog);
					dialog.setVisible(true);
					if (!dialogState.isOkPressed()) {
						return;
					}
				}
				
				
				final ImportFileDialog importFileDialog = new ImportFileDialog(session, importer, table, columns);
				
				importFileDialog.setPreviewData(importer.getPreview(10));
				
				GUIUtils.processOnSwingEventThread(new Runnable() {
					public void run() {
						session.getApplication().getMainFrame().addInternalFrame(importFileDialog, true);
						importFileDialog.moveToFront();
						GUIUtils.centerWithinDesktop(importFileDialog);
						importFileDialog.setVisible(true);
					}
				}, true);
				
			} catch (SQLException e) {
				//i18n[ImportTableDataCommand.sqlErrorOccured=An error occured while reading database data.]
				//i18n[ImportTableDataCommand.error=Error]
				JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportTableDataCommand.sqlErrorOccured"), stringMgr.getString("ImportTableDataCommand.error"), JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				//i18n[ImportTableDataCommand.ioErrorOccured=An error occured while reading import file data.]
				JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportTableDataCommand.ioErrorOccured"), stringMgr.getString("ImportTableDataCommand.error"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private ImportFileType determineType(File f) {
		// TODO: Implement this better
		if (f.getName().toLowerCase().endsWith("xls")) {
			return ImportFileType.XLS;
		}
		return ImportFileType.CSV;
	}
	
	private class StateListener implements IOkClosePanelListener {
		private boolean okPressed = false;
		private JDialog dialog = null;
		
		/**
		 * The constructor
		 * 
		 * @param dialog The dialog
		 */
		public StateListener(JDialog dialog) {
			this.dialog = dialog;
		}
		
		/**
		 * Invoked on cancel press
		 * 
		 * @param evt The event
		 */
		public void cancelPressed(OkClosePanelEvent evt) { /* Not needed */ }
		
		/**
		 * Invoked on close press
		 * 
		 * @param evt The event
		 */
		public void closePressed(OkClosePanelEvent evt) {
			okPressed = false;
			dialog.dispose();
		}
		
		/**
		 * Invoked on ok press
		 * 
		 * @param evt The event
		 */
		public void okPressed(OkClosePanelEvent evt) {
			okPressed = true;
			dialog.dispose();
		}
		
		/**
		 * Returns if the OK button was pressed.
		 * 
		 * @return true or false
		 */
		public boolean isOkPressed() {
			return okPressed;
		}
	}

}
