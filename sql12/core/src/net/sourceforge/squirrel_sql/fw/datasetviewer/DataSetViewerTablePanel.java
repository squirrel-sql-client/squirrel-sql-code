package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 * Modifications copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
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

import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.LimitReadLengthFeatureUnstable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DefaultFindService;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FindService;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Enumeration;

public class DataSetViewerTablePanel extends BaseDataSetViewerDestination implements IDataSetViewAccess, Printable
{

	private DataSetViewerTable _table = null;
	private IDataSetUpdateableModel _updateableModel;
   private DataSetViewerTableListSelectionHandler _selectionHandler;

   private DataModelImplementationDetails _dataModelImplementationDetails = new DataModelImplementationDetails();

   private ContinueReadHandler _continueReadHandler;
   private RowColSelectedCountListener _rowColSelectedCountListener;

	public DataSetViewerTablePanel()
	{
	}

   public void init(IDataSetUpdateableModel updateableModel, ISession session)
   {
      init(updateableModel, ListSelectionModel.SINGLE_INTERVAL_SELECTION, session);
   }


	public void init(IDataSetUpdateableModel updateableModel, int listSelectionMode, ISession session)
	{
      init(updateableModel, listSelectionMode, null, session);
	}

   public void init(IDataSetUpdateableModel updateableModel, DataModelImplementationDetails dataModelImplementationDetails, ISession session)
   {
      init(updateableModel, ListSelectionModel.SINGLE_INTERVAL_SELECTION, dataModelImplementationDetails, session);
   }

   public void init(IDataSetUpdateableModel dataSetUpdateableModel, int listSelectionMode, DataModelImplementationDetails dataModelImplementationDetails, ISession session)
   {
		if (null != dataModelImplementationDetails)
      {
         _dataModelImplementationDetails = dataModelImplementationDetails;
      }

      _table = new DataSetViewerTable(this, this, dataSetUpdateableModel, listSelectionMode, session);
      _continueReadHandler = new ContinueReadHandler(_table);
      _selectionHandler = new DataSetViewerTableListSelectionHandler(_table);
      _updateableModel = dataSetUpdateableModel;

		// Introduced during implementation of SQL result edit button. Seemed to have been missing.
		// Noted for clearance in case troubles occur because BaseDataSetViewerDestination._updateableModelReference isn't null anymore.
		setUpdateableModelReference(_updateableModel);

      _table.getSelectionModel().addListSelectionListener(e -> onSelectionChanged());
   }

   private void onSelectionChanged()
   {
      if(null != _rowColSelectedCountListener)
      {
         _rowColSelectedCountListener.rowColSelectedCountOrPosChanged(_table.getSelectedRowCount(), _table.getSelectedColumnCount(), _table.getSelectedRow(), _table.getSelectedColumn());
      }
   }

   @Override
   public void setRowColSelectedCountListener(RowColSelectedCountListener rowColSelectedCountListener)
   {
      _rowColSelectedCountListener = rowColSelectedCountListener;
   }

   public IDataSetUpdateableModel getUpdateableModel()
	{
		return _updateableModel;
	}

   public DataModelImplementationDetails getDataModelImplementationDetails()
   {
      return _dataModelImplementationDetails;
   }

   public void clear()
	{
		_table.getDataSetViewerTableModel().clear();
		_table.getDataSetViewerTableModel().fireTableDataChanged();
	}
	

	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		super.setColumnDefinitions(colDefs);
		_table.setColumnDefinitions(colDefs);

		// A new column model is set in the call of _table.setColumnDefinitions(colDefs)
		// That is why this listener is added here.
		_table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				onSelectionChanged();
			}
		});

	}

	public void moveToTop()
	{
		if (_table.getRowCount() > 0)
		{
			_table.setRowSelectionInterval(0, 0);
		}
	}

	/**
	 * Get the component for this viewer.
	 *
	 * @return	The component for this viewer.
	 */
	public Component getComponent()
	{
		return _table;
	}

	public DataSetViewerTable getTable()
	{
		return _table;
	}

	/*
	 * @see BaseDataSetViewerDestination#addRow(Object[])
	 */
	protected void addRow(Object[] row)
	{
		_table.getDataSetViewerTableModel().addRow(row);
	}
	
	public Object[] getRow(int row)
	{
		Object values[] = new Object[_table.getDataSetViewerTableModel().getColumnCount()];

		for (int i = 0; i < values.length; i++)
		{
			values[i] = _table.getDataSetViewerTableModel().getValueAt(row, i);
		}

		return values;
	}

	/*
	 * @see BaseDataSetViewerDestination#allRowsAdded()
	 */
	protected void allRowsAdded()
	{
		_table.getDataSetViewerTableModel().fireTableStructureChanged();
      _table.initColWidths();
   }

	/*
	 * @see IDataSetViewer#getRowCount()
	 */
	public int getRowCount()
	{
		return _table.getDataSetViewerTableModel().getRowCount();
	}

	public void setShowRowNumbers(boolean showRowNumbers)
	{
		_table.setShowRowNumbers(showRowNumbers);
	}

   public void addRowSelectionListener(RowSelectionListener rowSelectionListener)
   {
      _selectionHandler.addRowSelectionListener(rowSelectionListener);
   }

   public void removeRowSelectionListener(RowSelectionListener rowSelectionListener)
   {
      _selectionHandler.removeRowSelectionListener(rowSelectionListener);
   }

   public int[] getSeletedRows()
   {
      return _table.getSelectedRows();
   }

   public int[] getSelectedModelRows()
   {
      return _table.getSelectedModelRows();
   }

   public int getColumnWidthForHeader(String header)
   {
      TableColumnModel columnModel = _table.getColumnModel();

      for (int i = 0; i < columnModel.getColumnCount(); i++)
      {
         if(columnModel.getColumn(i).getHeaderValue().equals(header))
         {
            return columnModel.getColumn(i).getWidth();
         }
      }

      throw new IllegalStateException("No col with header: " + header);
   }

   public FindService createFindService()
   {
      return new DefaultFindService(_table, getColumnDefinitions());
   }


	@Override
	public void switchColumnHeader(ColumnHeaderDisplay columnHeaderDisplay)
	{
		for(Enumeration e = _table.getColumnModel().getColumns(); e.hasMoreElements();)
		{
			ExtTableColumn col = (ExtTableColumn) e.nextElement();

			if (ColumnHeaderDisplay.COLUMN_NAME == columnHeaderDisplay)
			{
				col.setHeaderValue(col.getColumnDisplayDefinition().getColumnName());
			}
			else if (ColumnHeaderDisplay.COLUMN_LABEL == columnHeaderDisplay)
			{
				col.setHeaderValue(col.getColumnDisplayDefinition().getLabel());
			}
		}

		_table.getTableHeader().repaint();
	}


	public void scrollColumnToVisible(ExtTableColumn columnDisplayDefinition)
	{
		new ScrollColumnToVisibleHandler(_table, columnDisplayDefinition);
	}

	public void moveColumnsToFront(ArrayList<ExtTableColumn> columnsToMoveToFront)
	{
		MoveColumnsToFrontHandler.moveColumnsToFront(_table, columnsToMoveToFront);
	}


	/////////////////////////////////////////////////////////////////////////
	//
	// Implement the IDataSetViewAccess interface,
	// functions needed to support table operations
	//
	// These functions are called from within DataSetViewerTable and MyTable to tell
	// those classes how to operate.  The code in these functions will be
	// different depending on whether the table is read-only or editable.
	//
	// The definitions below are for read-only operation.  The editable
	// table panel overrides these functions with the versions that tell the
	// tables how to set up for editing operations.
	//
	//
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * Tell the table that it is editable.  This is called from within
	 * MyTable.isCellEditable().  We do not bother to distinguish between
	 * editable and non-editable cells within the same table.
	 */
	public boolean isTableEditable() {
		return false;
	}
	
	/**
	 * Tell the table whether particular columns are editable.
	 */
	public boolean isColumnEditable(int col, Object originalValue) {
		return false;
	}

	/**
	 * See if a value in a column has been limited in some way and
	 * needs to be re-read before being used for editing.
	 * For read-only tables this may actually return true since we want
	 * to be able to view the entire contents of the cell even if it was not
	 * completely loaded during the initial table setup.
	 */
	public boolean needToReRead(int col, Object originalValue) {
		// call the DataType object for this column and have it check the current value
		return CellComponentFactory.needToReRead(getColDefs()[col], originalValue);
	}
	
	/**
	 * Re-read the contents of this cell from the database.
	 * If there is a problem, the message will have a non-zero length after return.
	 */
	public Object reReadDatum(Object[] values, int col, StringBuffer message)
	{
		if(null == _updateableModel)
		{
			LimitReadLengthFeatureUnstable.unknownTable();
			return values[col];
		}

		return ((IDataSetUpdateableTableModel)_updateableModel).reReadDatum(values, getColDefs(), col, message);
	}
	
	/**
	 * Function to set up CellEditors.  Null for read-only tables.
	 */
	public void setCellEditors(DataSetViewerTable table) {}
	
	/**
	 * Change the data in the permanent store that is represented by the JTable.
	 * Does nothing in read-only table.
	 */
	public int[] changeUnderlyingValueAt(
		int row,
		int col,
		Object newValue,
		Object oldValue)
	{
		return new int[0];	// underlaying data cannot be changed
	}
	
	/**
	 * Delete a set of rows from the table.
	 * The indexes are the row indexes in the SortableModel.
	 */
	public void deleteRows(int[] rows) {}	// cannot delete rows in read-only table
	
	/**
	 * Initiate operations to insert a new row into the table.
	 */
	public void insertRow() {}	// cannot insert row into read-only table
	
	

	//
	// Begin code related to printing
	//

                                                                                
	//
	// variables used in printing
	//
	JTableHeader tableHeader;
	int [] subTableSplit = null;
	boolean pageinfoCalculated=false;
	int totalNumPages=0;
	int prevPageIndex = 0;
	int subPageIndex = 0;
	int subTableSplitSize = 0;
	double tableHeightOnFullPage, headerHeight;
	double pageWidth, pageHeight;
	int fontHeight, fontDesent;
	double tableHeight, rowHeight;
	double scale = 8.0/12.0;        // default is 12 point, so define font relative to that


	/**
	 * Print the table contents.
	 * This was copied from a tutorial paper on the Sun Java site:
	 * paper: http://developer.java.sun.com/developer/onlineTraining/Programming/JDCBook/advprint.html
	 * code: http://developer.java.sun.com/developer/onlineTraining/Programming/JDCBook/Code/SalesReport.java
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {

		Graphics2D g2=(Graphics2D)g;
		
		// reset each time we start a new print
		if (pageIndex==0)
			pageinfoCalculated = false;
		
		if(!pageinfoCalculated) {
			getPageInfo(g, pageFormat);
		}
 
		g2.setColor(Color.black);
		if(pageIndex>=totalNumPages) {
			return NO_SUCH_PAGE;
		}
		if (prevPageIndex != pageIndex) {
			subPageIndex++;
			if( subPageIndex == subTableSplitSize -1) {
					subPageIndex=0;
			}
		}
 
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
 
		int rowIndex = pageIndex/ (subTableSplitSize -1);
         
		printTablePart(g2, pageFormat, rowIndex, subPageIndex);
		prevPageIndex= pageIndex;
 
		return Printable.PAGE_EXISTS;
	}
 
 
	/**
	 * Part of print code coped from Sun
	 */
	public void getPageInfo(Graphics g, PageFormat pageFormat) {
 
		subTableSplit = null;
		subTableSplitSize = 0;
		subPageIndex = 0;
		prevPageIndex = 0;
 
		fontHeight=(int)(g.getFontMetrics().getHeight() * scale);
		fontDesent=(int)(g.getFontMetrics().getDescent() * scale);
 
		tableHeader = _table.getTableHeader();
//		double headerWidth = tableHeader.getWidth() * scale;
		headerHeight = tableHeader.getHeight() +_table.getRowMargin() * scale;
 
		pageHeight = pageFormat.getImageableHeight();
		pageWidth =  pageFormat.getImageableWidth();
 
//		double tableWidth =_table.getColumnModel().getTotalColumnWidth() * scale;
		tableHeight = _table.getHeight() * scale;
		rowHeight = _table.getRowHeight() + _table.getRowMargin() * scale;
 
		tableHeightOnFullPage = (int)(pageHeight - headerHeight - fontHeight*2);
		tableHeightOnFullPage = tableHeightOnFullPage/rowHeight * rowHeight;
 
		TableColumnModel tableColumnModel = tableHeader.getColumnModel();
		int columns = tableColumnModel.getColumnCount();
		int columnMargin = (int)(tableColumnModel.getColumnMargin() * scale);
 
		int [] temp = new int[columns];
		int columnIndex = 0;
		temp[0] = 0;
		int columnWidth;
		int length = 0;
		subTableSplitSize = 0;
		while ( columnIndex < columns ) {
 
			columnWidth = (int)(tableColumnModel.getColumn(columnIndex).getWidth() * scale);
 
			if ( length + columnWidth + columnMargin > pageWidth ) {
				temp[subTableSplitSize+1] = temp[subTableSplitSize] + length;
				length = columnWidth;
				subTableSplitSize++;
			}
			else {
				length += columnWidth + columnMargin;
			}
			columnIndex++;
		} //while
 
		if ( length > 0 )  {  // if are more columns left, part page
		   temp[subTableSplitSize+1] = temp[subTableSplitSize] + length;
		   subTableSplitSize++;
		}
 
		subTableSplitSize++;
		subTableSplit = new int[subTableSplitSize];
		for ( int i=0; i < subTableSplitSize; i++ ) {
			subTableSplit[i]= temp[i];
		}
		totalNumPages = (int)(tableHeight/tableHeightOnFullPage);
		if ( tableHeight%tableHeightOnFullPage >= rowHeight ) { // at least 1 more row left
			totalNumPages++;
		}
 
		totalNumPages *= (subTableSplitSize-1);
		pageinfoCalculated = true;
	}
 
	/**
	 * Part of print code coped from Sun
	 */
	public void printTablePart(Graphics2D g2, PageFormat pageFormat, int rowIndex, int columnIndex) {
 
		String pageNumber = "Page: "+(rowIndex+1);
		if ( subTableSplitSize > 1 ) {
			pageNumber += "-" + (columnIndex+1);
		}
 
		int pageLeft = subTableSplit[columnIndex];
		int pageRight = subTableSplit[columnIndex + 1];
 
		int pageWidth =  pageRight-pageLeft;
 
 
		// page number message (in smaller type)
		g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), 8));
		g2.drawString(pageNumber,  pageWidth/2-35, (int)(pageHeight - fontHeight));
 
		double clipHeight = Math.min(tableHeightOnFullPage, tableHeight - rowIndex*tableHeightOnFullPage);
 
		g2.translate(-subTableSplit[columnIndex], 0);
		g2.setClip(pageLeft ,0, pageWidth, (int)headerHeight);
 
		g2.scale(scale, scale);
		tableHeader.paint(g2);   // draw the header on every page
		g2.scale(1/scale, 1/scale);
		g2.translate(0, headerHeight);
		g2.translate(0,  -tableHeightOnFullPage*rowIndex);
 
		// cut table image and draw on the page
 
		g2.setClip(pageLeft, (int)tableHeightOnFullPage*rowIndex, pageWidth, (int)clipHeight);
		g2.scale(scale, scale);
		_table.paint(g2);
		g2.scale(1/scale, 1/scale);
 
		double pageTop =  tableHeightOnFullPage*rowIndex - headerHeight;
//		double pageBottom = pageTop +  clipHeight + headerHeight;
		g2.drawRect(pageLeft, (int)pageTop, pageWidth, (int)(clipHeight+ headerHeight));
	}
	
	//
	// End of code related to printing
	//


   @Override
   public TableState getResultSortableTableState()
   {
      return new TableState(_table);
   }

   @Override
   public void applyResultSortableTableState(TableState sortableTableState)
   {
      sortableTableState.apply(_table);
   }

   @Override
   public void setContinueReadChannel(ContinueReadChannel continueReadChannel)
   {
      _continueReadHandler.setContinueReadChannel(continueReadChannel);
   }

   @Override
   public void disableContinueRead()
   {
      _continueReadHandler.disableContinueRead();
   }

}
