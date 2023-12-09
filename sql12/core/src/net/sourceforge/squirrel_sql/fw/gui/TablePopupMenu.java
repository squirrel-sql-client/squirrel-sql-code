package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.tableselectiondiff.TableSelectionDiff;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableClickPosition;
import net.sourceforge.squirrel_sql.fw.gui.action.*;
import net.sourceforge.squirrel_sql.fw.gui.action.colorrows.ColorSelectionCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.colorrows.ColorSelectionType;
import net.sourceforge.squirrel_sql.fw.gui.action.colorrows.CopyColoredRowsToNewWindowCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.colorrows.GotoColorMenuController;
import net.sourceforge.squirrel_sql.fw.gui.action.columndetails.ColumnDetailsController;
import net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown.TableCopyAsMarkdownCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.copyseparatedby.TableCopySeparatedByCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ExportUtil;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.TableExport;
import net.sourceforge.squirrel_sql.fw.gui.action.makeeditable.MakeEditableCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow.CopySelectedRowsToOwnWindowCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues.ShowDistinctValuesCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.CopyWikiTableActionFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.ICopyWikiTableActionFactory;
import net.sourceforge.squirrel_sql.fw.gui.table.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.resources.ResourceUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.time.StopWatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterJob;


public class TablePopupMenu extends BasePopupMenu
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TablePopupMenu.class);

	private ISession _session;

	private JCheckBoxMenuItem _alwaysAdjustAllColWidtshActionItem;
	private JCheckBoxMenuItem _showRowNumbersItem;


	private DataSetViewerTableCopyAction _copy;
	private CopyWithHeadersAction _copyWithHeaders = new CopyWithHeadersAction();
	private CopyHtmlAction _copyHtml = new CopyHtmlAction();
	private CopyAlignedAction _copyAligned = new CopyAlignedAction();
	private CopyAsMarkdownAction _copyAsMarkdown = new CopyAsMarkdownAction();
	private CopySeparatedByAction _copySeparatedBy = new CopySeparatedByAction();

	private CopyInStatementAction _copyInStatement = new CopyInStatementAction();
	private CopyWhereStatementAction _copyWhereStatement = new CopyWhereStatementAction();
	private CopyUpdateStatementAction _copyUpdateStatement = new CopyUpdateStatementAction();
	private CopyInsertStatementAction _copyInsertStatement = new CopyInsertStatementAction();
	private CopyColumnHeaderAction _copyColumnHeader = new CopyColumnHeaderAction();
	private CopySelectedRowsToOwnWindowAction _copySelectedRowsToOwnWindow = new CopySelectedRowsToOwnWindowAction();

	private ShowReferencesAction _showReferences = new ShowReferencesAction();
	private ShowColumnDetailsAction _showColumnDetails = new ShowColumnDetailsAction();
	private ShowDistinctValuesAction _showDistinctValues = new ShowDistinctValuesAction();

	private ColorSelectedRowsAction _colorSelectedRows = new ColorSelectedRowsAction();
	private ColorSelectedCellsAction _colorSelectedCells = new ColorSelectedCellsAction();
	private GotoColorMenuController _gotoColorMenuController = new GotoColorMenuController();
	private CopyColoredRowsToNewWindowAction _copyColoredRowsToNewWindow = new CopyColoredRowsToNewWindowAction();

	private ExportAction _export = new ExportAction();
   private AdjustAllColWidthsAction _adjustAllColWidthsAction = new AdjustAllColWidthsAction();
	private AlwaysAdjustAllColWidthsAction _alwaysAdjustAllColWidthsAction = new AlwaysAdjustAllColWidthsAction();
   private ShowRowNumbersAction _showRowNumbersAction = new ShowRowNumbersAction();

   private MakeEditableAction _makeEditable = new MakeEditableAction();
	private UndoMakeEditableAction _undoMakeEditable = new UndoMakeEditableAction();
	private DeleteRowsAction _deleteRows = new DeleteRowsAction();
	private InsertRowAction _insertRow = new InsertRowAction();
	private SelectAllAction _select = new SelectAllAction();
	private SelectRowsAction _selectRows = new SelectRowsAction();
	private PrintAction _print = new PrintAction();


	// The following pointer is needed to allow the "Make Editable button
	// to tell the application to set up an editable display panel
	private IDataSetUpdateableModel _updateableModel = null;

   // pointer to the viewer
	// This is needed for insert and delete operations
	private DataSetViewerTablePanel _dataSetViewerTablePanel;
	
	private ICopyWikiTableActionFactory copyWikiTableActionFactory = CopyWikiTableActionFactory.getInstance();


	/**
	 * Constructor used when caller wants to be able to make table editable.
	 * We need both parameters because there is at least one case where the
	 * underlying data model is updateable, but we do not want to allow the
	 * user to enter editing mode because they are already in edit mode.
	 * The caller needs to determine whether or not to allow a request for edit mode.
	 */
	public TablePopupMenu(boolean allowEditing,
								 IDataSetUpdateableModel updateableModel,
								 DataSetViewerTablePanel dataSetViewerTable,
								 ISession session)
	{
		// save the pointer needed to enable editing of data on-demand
		_updateableModel = updateableModel;


		// save the pointer needed for insert and delete operations
		_dataSetViewerTablePanel = dataSetViewerTable;
		_session = session;


		_copy = new DataSetViewerTableCopyAction(dataSetViewerTable.getTable());
		addAction(_copy, DataSetViewerTableCopyAction.getTableCopyActionKeyStroke());


		addAction(_copyWithHeaders);
		addAction(_copyHtml);
		addAction(_copyAligned);
		addAction(_copyAsMarkdown);
		addAction(_copySeparatedBy);

		// This is a JMenu with sub menu items that's why we can't use addMenuItem(...).
		add(copyWikiTableActionFactory.createMenueItem(() -> _dataSetViewerTablePanel.getTable()));

		addAction(_copyInStatement);
		addAction(_copyWhereStatement);
		addAction(_copyUpdateStatement);
      addAction(_copyInsertStatement);
      addAction(_copyColumnHeader);
		addAction(_copySelectedRowsToOwnWindow);

		if (null != _session)
		{
			addSeparator();
			addAction(_showReferences);
			addAction(_showColumnDetails);
		}

		addAction(_showDistinctValues);

		addSeparator();
		addAction(_colorSelectedRows);
		addAction(_colorSelectedCells);

		// This is a JMenu with sub menu items that's why we can't use addMenuItem(...).
		add(_gotoColorMenuController.getParentMenu());

		add(_copyColoredRowsToNewWindow);
		addSeparator();

		addAction(_export);
      addSeparator();

		add(TableSelectionDiff.createMenu(() -> _dataSetViewerTablePanel.getTable()));
      addSeparator();

		addAction(_adjustAllColWidthsAction);

      _alwaysAdjustAllColWidtshActionItem = new JCheckBoxMenuItem();
		_alwaysAdjustAllColWidtshActionItem.setSelected(ButtonTableHeader.isAlwaysAdjustAllColWidths());
		_alwaysAdjustAllColWidtshActionItem.setAction(_alwaysAdjustAllColWidthsAction);
		addMenuItem(_alwaysAdjustAllColWidtshActionItem);

      addSeparator();
      
      _showRowNumbersItem = new JCheckBoxMenuItem();
		_showRowNumbersItem.setSelected(false);
		_showRowNumbersItem.setAction(_showRowNumbersAction);
		addMenuItem(_showRowNumbersItem);


		if (allowEditing)
		{
			addSeparator();
			addAction(_makeEditable);
		}
//		if  ( ! allowEditing )
		if (updateableModel != null && updateableModel.editModeIsForced())
		{
			addAction(_undoMakeEditable);
		}
		addSeparator();
		addAction(_select);
		
		addAction(_selectRows);
		

		// add entries for insert and delete rows
		// only if table is updateable and already editable (ie. allowEditing is false)
		if (_updateableModel != null && allowEditing == false)
		{
			addSeparator();
			addAction(_insertRow);
			addAction(_deleteRows);
		}

		addSeparator();
		addAction(_print);
	}

	private void addMenuItem(JMenuItem menuItem)
	{
		String actionName = menuItem.getText();
		KeyStroke validKeyStroke = Main.getApplication().getShortcutManager().setAccelerator(menuItem, null, actionName);
		add(menuItem);

		if (null != validKeyStroke)
		{
			DataSetViewerTable table = _dataSetViewerTablePanel.getTable();
			table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(validKeyStroke, actionName);
			table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(validKeyStroke, actionName);
			table.getInputMap(JComponent.WHEN_FOCUSED).put(validKeyStroke, actionName);
			table.getActionMap().put(actionName, new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e)
				{
					menuItem.doClick();
				}
			});
		}
	}

	private void addAction(Action action)
	{
		addAction(action, null);
	}

	private void addAction(Action action, KeyStroke defaultKeyStroke)
	{
		JMenuItem mnuAdded = add(action);
		KeyStroke validKeyStroke = Main.getApplication().getShortcutManager().setAccelerator(mnuAdded, defaultKeyStroke, action);
		ResourceUtil.trySetToolTip(mnuAdded, action);

		if (null != validKeyStroke)
		{
			DataSetViewerTable table = _dataSetViewerTablePanel.getTable();
			table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(validKeyStroke, action.getClass().getName());
			table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(validKeyStroke, action.getClass().getName());
			table.getInputMap(JComponent.WHEN_FOCUSED).put(validKeyStroke, action.getClass().getName());
			table.getActionMap().put(action.getClass().getName(), action);
		}

	}

	/**
	 * Constructor used when creating menu for use in cell editor.
	 */
	public TablePopupMenu(IDataSetUpdateableModel updateableModel, DataSetViewerTablePanel dataSetViewerTablePanel, DataSetViewerTable table)
	{
		// save the pointer needed to enable editing of data on-demand
		_updateableModel = updateableModel;

      // save the pointer needed for insert and delete operations
		_dataSetViewerTablePanel = dataSetViewerTablePanel;

		add(_select);
		addSeparator();
		add(_insertRow);
		add(_deleteRows);

		addSeparator();
		add(_print);
	}

	public void reset()
	{
		_showRowNumbersItem.setSelected(false);
	}


	/**
	 * Show the menu.
	 */
	public void showPopupMenu(Component invoker, int x, int y, TableClickPosition tableClickPosition)
	{
		_gotoColorMenuController.createSubMenus(_dataSetViewerTablePanel.getTable());
		_copyColumnHeader.setCurrentTableClickPosition(tableClickPosition);
		_showColumnDetails.setCurrentTableClickPosition(tableClickPosition);

		super.show(invoker, x, y);
	}

	public void show(MouseEvent evt)
	{
		super.show(evt);
	}

	public void ensureRowNumbersMenuItemIsUpToDate(boolean currentState)
	{
		if( currentState == _showRowNumbersItem.isSelected())
		{
			return;
		}

		_showRowNumbersItem.setAction(null);
		_showRowNumbersItem.setSelected(currentState);
		_showRowNumbersItem.setAction(_showRowNumbersAction);
	}


	private class CopyWithHeadersAction extends BaseAction
	{
		CopyWithHeadersAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyWithHeaders"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyCommand(_dataSetViewerTablePanel.getTable(), true).execute();
		}
	}



	private class CopyHtmlAction extends BaseAction
	{
		CopyHtmlAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyashtml"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyHtmlCommand(_dataSetViewerTablePanel.getTable()).execute();
		}
	}
	
	private class CopyAlignedAction extends BaseAction
	{
		CopyAlignedAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyaligned"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyAlignedCommand(_dataSetViewerTablePanel.getTable(), _session).execute();
		}
	}

	private class CopyAsMarkdownAction extends BaseAction
	{
		CopyAsMarkdownAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyasmarkdown"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyAsMarkdownCommand(_dataSetViewerTablePanel.getTable()).execute();
		}
	}

	private class CopySeparatedByAction extends BaseAction
	{
		CopySeparatedByAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyseparatedby"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopySeparatedByCommand(_dataSetViewerTablePanel.getTable()).execute();
		}
	}



	private class CopyInStatementAction extends BaseAction
	{
		CopyInStatementAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyasinstatement"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyInStatementCommand(_dataSetViewerTablePanel.getTable(), _session).execute();
		}
	}

	private class CopyWhereStatementAction extends BaseAction
	{
		CopyWhereStatementAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyaswherestatement"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyWhereStatementCommand(_dataSetViewerTablePanel.getTable(), _session).execute();
		}
	}

	private class CopyUpdateStatementAction extends BaseAction
	{
		CopyUpdateStatementAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyasupdatestatement"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyUpdateStatementCommand(_dataSetViewerTablePanel.getTable(), _dataSetViewerTablePanel.getDataModelImplementationDetails()).execute();
		}
	}

	private class CopyInsertStatementAction extends BaseAction
	{
		CopyInsertStatementAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyasinsertstatement"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyInsertStatementCommand(_dataSetViewerTablePanel.getTable(), _dataSetViewerTablePanel.getDataModelImplementationDetails()).execute();
		}

   }

	private class CopyColumnHeaderAction extends BaseAction
	{
		private TableClickPosition _currentTableClickPosition;

		CopyColumnHeaderAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copycolumnheader"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableCopyColumnHeaderCommand(_dataSetViewerTablePanel, _currentTableClickPosition).execute();
		}

		public void setCurrentTableClickPosition(TableClickPosition tableClickPosition)
		{
			_currentTableClickPosition = tableClickPosition;
		}
	}

	private class CopySelectedRowsToOwnWindowAction extends BaseAction
	{
		CopySelectedRowsToOwnWindowAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.CopySelectedRowsToOwnWindow"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new CopySelectedRowsToOwnWindowCommand(_dataSetViewerTablePanel.getTable(), _session).execute();
		}
	}


	private class ExportAction extends BaseAction
   {
		ExportAction()
      {
         // i18n[TablePopupMenu.export=Export CSV / MS Excel / XML ...]
         super(s_stringMgr.getString("TablePopupMenu.export"));
      }

      public void actionPerformed(ActionEvent evt)
      {
			TableExport tableExport = new TableExport(_dataSetViewerTablePanel.getTable());


			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			tableExport.export();
			stopWatch.stop();
			ExportUtil.writeExportMessage(stopWatch, tableExport.getWrittenRows(), tableExport.getTargetFile());
		}

	}

   private class ShowReferencesAction extends BaseAction
   {
      ShowReferencesAction()
      {
         super(s_stringMgr.getString("TablePopupMenu.showRefernces"));
			ResourceUtil.storeToolTipInAction(this, s_stringMgr.getString("TablePopupMenu.showReferences.toolTip"));
      }

      public void actionPerformed(ActionEvent evt)
      {
         new ShowReferencesCommand(_dataSetViewerTablePanel.getTable(), _updateableModel, (JFrame) GUIUtils.getOwningFrame(_dataSetViewerTablePanel.getTable()), _session).execute();
      }
   }

   private class ShowColumnDetailsAction extends BaseAction
   {
		private TableClickPosition _tableClickPosition;

		ShowColumnDetailsAction()
      {
         super(s_stringMgr.getString("TablePopupMenu.showColumnDetails"));
      }

      public void actionPerformed(ActionEvent evt)
      {
			new ColumnDetailsController(_dataSetViewerTablePanel.getTable(), _session, _tableClickPosition);
      }

		public void setCurrentTableClickPosition(TableClickPosition tableClickPosition)
		{
			_tableClickPosition = tableClickPosition;
		}
	}

   private class ShowDistinctValuesAction extends BaseAction
   {
		ShowDistinctValuesAction()
      {
         super(s_stringMgr.getString("TablePopupMenu.showDistinctValues"));
      }

      public void actionPerformed(ActionEvent evt)
      {
         new ShowDistinctValuesCommand(_dataSetViewerTablePanel.getTable(), GUIUtils.getOwningWindow(_dataSetViewerTablePanel.getTable()), _session).execute();
      }
   }


   private class ColorSelectedRowsAction extends BaseAction
   {
		ColorSelectedRowsAction()
      {
         super(s_stringMgr.getString("TablePopupMenu.ColorSelectedRows"));
      }

      public void actionPerformed(ActionEvent evt)
      {
         new ColorSelectionCommand(_dataSetViewerTablePanel.getTable(), ColorSelectionType.ROWS).execute();
      }
   }

   private class ColorSelectedCellsAction extends BaseAction
   {
		ColorSelectedCellsAction()
      {
         super(s_stringMgr.getString("TablePopupMenu.ColorSelectedCells"));
      }

      public void actionPerformed(ActionEvent evt)
      {
         new ColorSelectionCommand(_dataSetViewerTablePanel.getTable(), ColorSelectionType.CELLS).execute();
      }
   }

   private class CopyColoredRowsToNewWindowAction extends BaseAction
   {
		CopyColoredRowsToNewWindowAction()
      {
         super(s_stringMgr.getString("TablePopupMenu.CopyColoredRowsToNewWindow"));
      }

      public void actionPerformed(ActionEvent evt)
      {
         new CopyColoredRowsToNewWindowCommand(_dataSetViewerTablePanel.getTable(), _session).execute();
      }
   }


	private class AdjustAllColWidthsAction extends BaseAction
	{

		AdjustAllColWidthsAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.adoptAllColWidthsAction"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if(_dataSetViewerTablePanel.getTable().getTableHeader() instanceof ButtonTableHeader)
			{
				(_dataSetViewerTablePanel.getTable().getButtonTableHeader()).adjustAllColWidths(true);
			}
		}
	}


   private class AlwaysAdjustAllColWidthsAction extends BaseAction
	{

		AlwaysAdjustAllColWidthsAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.alwaysAdoptAllColWiths"));
		}

		public void actionPerformed(ActionEvent evt)
		{
         ButtonTableHeader.setAlwaysAdjustAllColWidths(_alwaysAdjustAllColWidtshActionItem.isSelected());
         if (_alwaysAdjustAllColWidtshActionItem.isSelected())
			{
            (_dataSetViewerTablePanel.getTable().getButtonTableHeader()).adjustAllColWidths(true);
			}
		}
	}

   private class ShowRowNumbersAction extends BaseAction
	{
		ShowRowNumbersAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.showRowNumbers"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			JCheckBoxMenuItem mnu = (JCheckBoxMenuItem) evt.getSource();
			new ShowRowNumbersCommand(_dataSetViewerTablePanel, mnu.isSelected()).execute();
		}
	}



	private class MakeEditableAction extends BaseAction
	{
		MakeEditableAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.makeeditable"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_updateableModel != null)
			{
				new MakeEditableCommand(_updateableModel).execute();
			}
		}
	}


	private class UndoMakeEditableAction extends BaseAction
	{
		UndoMakeEditableAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.undomakeeditable"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_updateableModel != null)
			{
				new UndoMakeEditableCommand(_updateableModel).execute();
			}
		}
	}
	private class DeleteRowsAction extends BaseAction
	{
		DeleteRowsAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.deleterows"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			int selectedRows[] = _dataSetViewerTablePanel.getTable().getSelectedRows();

			// Tell the DataSetViewer to delete the rows
			// Note: rows are indexes in the SORTABLE model, not the ACTUAL model
			_dataSetViewerTablePanel.deleteRows(selectedRows);
		}
	}

	private class InsertRowAction extends BaseAction
	{
		InsertRowAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.insertrow"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			_dataSetViewerTablePanel.insertRow();
		}
	}

	private class SelectAllAction extends BaseAction
	{
		SelectAllAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.selectall"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableSelectAllCellsCommand(_dataSetViewerTablePanel.getTable()).execute();
		}
	}

	/**
	 * Select the entire rows of the current selection.
	 * @author Stefan Willinger
	 *
	 */
	private class SelectRowsAction extends BaseAction
	{

		SelectRowsAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.selectEntireRows"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			new TableSelectEntireRowsCommand(_dataSetViewerTablePanel.getTable()).execute();
		}
	}
	

   private class PrintAction extends BaseAction
   {
		PrintAction()
      {
         super(s_stringMgr.getString("TablePopupMenu.print"));
      }

      public void actionPerformed(ActionEvent evt)
      {
			try
			{

				PrinterJob printerJob = PrinterJob.getPrinterJob();

				printerJob.setPrintable(_dataSetViewerTablePanel);

				if (printerJob.printDialog())
				{
					printerJob.print();
				}
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
      }
   }

}

