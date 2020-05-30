package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.DataSetTextAreaController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;

public class DataSetViewerTextPanel extends BaseDataSetViewerDestination
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetViewerTextPanel.class);


	private DataSetTextAreaController _dataSetTextAreaController = new DataSetTextAreaController();


	public DataSetViewerTextPanel()
	{
	}

	public void clear()
	{
		_dataSetTextAreaController.clear();
	}

	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		super.setColumnDefinitions(colDefs);
		_dataSetTextAreaController.init(colDefs, true);

//
//    Making TextOutput editable didn't work since the fix of #1051 "SQL Results: Text" mode is not working in ver. 3.4
//		Note: Former Bug number was 3575054
//
//		TextPopupMenu popupMenu = _dataSetTextAreaController.getPopupMenu();
//
//
//		AbstractAction action = new AbstractAction()
//		{
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				if (_updateableTableModel != null)
//				{
//					new MakeEditableCommand(_updateableTableModel).execute();
//				}
//			}
//		};
//
//		action.putValue(Action.NAME, s_stringMgr.getString("dataSetViewerTablePanel.makeEditable"));
//
//		popupMenu.addSeparator();
//		popupMenu.add(action);
	}

	protected void addRow(Object[] row)
	{
		_dataSetTextAreaController.addRow(row);
	}

  protected void close()
	{
	  _dataSetTextAreaController.close();
	}

	public void moveToTop()
	{
		_dataSetTextAreaController.moveToTop();
	}

	/*
	 * @see BaseDataSetViewerDestination#allRowsAdded()
	 */
	protected void allRowsAdded()
	{
	}

	/**
	 * Get the component for this viewer.
	 *
	 * @return	The component for this viewer.
	 */
	public Component getComponent()
	{
		return _dataSetTextAreaController.getComponent();
	}

	/*
	 * @see IDataSetViewer#getRowCount()
	 */
	public int getRowCount()
	{
		return _dataSetTextAreaController.getRowCount();
	}
}
