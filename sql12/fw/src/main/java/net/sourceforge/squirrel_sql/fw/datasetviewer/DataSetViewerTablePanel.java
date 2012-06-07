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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableJTextField;
import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.gui.RectangleSelectionHandler;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DataSetViewerTablePanel extends BaseDataSetViewerDestination
				implements IDataSetTableControls, Printable
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataSetViewerTablePanel.class);

	private ILogger s_log = LoggerController.createLogger(DataSetViewerTablePanel.class);

	private MyJTable _table = null;
	private MyTableModel _typedModel;
	private IDataSetUpdateableModel _updateableModel;
   private DataSetViewerTableListSelectionHandler _selectionHandler;

   private IDataModelImplementationDetails _dataModelImplementationDetails =
         new IDataModelImplementationDetails()
         {
            @Override
            public String getStatementSeparator()
            {
               return ";";
            }
         };

   public DataSetViewerTablePanel()
	{
		super();
	}

   public void init(IDataSetUpdateableModel updateableModel)
   {
      init(updateableModel, ListSelectionModel.SINGLE_INTERVAL_SELECTION);
   }


	public void init(IDataSetUpdateableModel updateableModel, int listSelectionMode)
	{
      init(updateableModel, listSelectionMode, null);
	}

   public void init(IDataSetUpdateableModel updateableModel, IDataModelImplementationDetails dataModelImplementationDetails)
   {
      init(updateableModel, ListSelectionModel.SINGLE_INTERVAL_SELECTION, dataModelImplementationDetails);
   }

   public void init(IDataSetUpdateableModel updateableModel, int listSelectionMode, IDataModelImplementationDetails dataModelImplementationDetails)
   {
      if (null != dataModelImplementationDetails)
      {
         _dataModelImplementationDetails = dataModelImplementationDetails;
      }

      _table = new MyJTable(this, updateableModel, listSelectionMode);
      _selectionHandler = new DataSetViewerTableListSelectionHandler(_table);
      _updateableModel = updateableModel;


   }

	
	public IDataSetUpdateableModel getUpdateableModel()
	{
		return _updateableModel;
	}

   public IDataModelImplementationDetails getDataModelImplementationDetails()
   {
      return _dataModelImplementationDetails;
   }

   public void clear()
	{
		_typedModel.clear();
		_typedModel.fireTableDataChanged();
	}
	

	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		super.setColumnDefinitions(colDefs);
		_table.setColumnDefinitions(colDefs);
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

	/*
	 * @see BaseDataSetViewerDestination#addRow(Object[])
	 */
	protected void addRow(Object[] row)
	{
		_typedModel.addRow(row);
	}
	
	/*
	 * @see BaseDataSetViewerDestination#getRow(row)
	 */
	protected Object[] getRow(int row)
	{
		Object values[] = new Object[_typedModel.getColumnCount()];
		for (int i=0; i < values.length; i++)
			values[i] = _typedModel.getValueAt(row, i);
		return values;
	}

	/*
	 * @see BaseDataSetViewerDestination#allRowsAdded()
	 */
	protected void allRowsAdded()
	{
		_typedModel.fireTableStructureChanged();
      _table.initColWidths();
   }

	/*
	 * @see IDataSetViewer#getRowCount()
	 */
	public int getRowCount()
	{
		return _typedModel.getRowCount();
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

   public int[] getSeletedModelRows()
   {
      int[] selectedViewRows = _table.getSelectedRows();

      int[] ret = new int[selectedViewRows.length];

      for (int i = 0; i < selectedViewRows.length; i++)
      {
         ret[i] = (((SortableTableModel)_table.getModel()).transfromToModelRow(selectedViewRows[i]));
      }

      return ret;
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


   /*
     * The JTable used for displaying all DB ResultSet info.
     */
	protected final class MyJTable extends JTable
	{
		private static final long serialVersionUID = 1L;
		private final int _multiplier;
		private static final String data = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";

		private TablePopupMenu _tablePopupMenu;
		private IDataSetTableControls _creator;

      private RectangleSelectionHandler _rectangleSelectionHandler = new RectangleSelectionHandler(this);
		private RowNumberTableColumn _rntc;
		private ButtonTableHeader _tableHeader = new ButtonTableHeader();

		MyJTable(IDataSetTableControls creator,
               IDataSetUpdateableModel updateableObject, int listSelectionMode)
		{
			super(new SortableTableModel(new MyTableModel(creator)));
			_creator = creator;
			_typedModel = (MyTableModel) ((SortableTableModel) getModel()).getActualModel();
			_multiplier =
				getFontMetrics(getFont()).stringWidth(data) / data.length();
			setRowHeight(getFontMetrics(getFont()).getHeight());
			boolean allowUpdate = false;
			// we want to allow editing of read-only tables on-demand, but
			// it would be confusing to include the "Make Editable" option
			// when we are already in edit mode, so only allow that option when
			// the background model is updateable AND we are not already editing
			if (updateableObject != null && ! creator.isTableEditable())
				allowUpdate = true;
			createGUI(allowUpdate, updateableObject, listSelectionMode);

			// just in case table is editable, call creator to set up cell editors
			_creator.setCellEditors(this);

			/*
			 * TODO: When 1.4 is the earliest version supported, add the following line:
			*		setSurrendersFocusOnKeystroke(true);
			* This should help handle some problems with navigation using tab & return
			* to move through cells.
			*/


		}


		public void paint(Graphics g)
		{
			super.paint(g);
         _rectangleSelectionHandler.paintRectWhenNeeded(g);
		}

		public IDataSetTableControls getCreator() {
			return _creator;
		}

		/*
		 * override the JTable method so that whenever something asks for
		 * the cellEditor, we save a reference to that cell editor.
		 * Our ASSUMPTION is that the cell editor is only requested
		 * when it is about to be activated.
		 */
		public TableCellEditor getCellEditor(int row, int col)
		{
			TableCellEditor cellEditor = super.getCellEditor(row, col);
			currentCellEditor = (DefaultCellEditor)cellEditor;
			return cellEditor;
		}


		/**
		 * There are two special cases where we need to override the default behavior
		 * when we begin cell editing.  For some reason, when you use the keyboard to
		 * enter a cell (tab, enter, arrow keys, etc), the first character that you type
		 * after entering the field is NOT passed through the KeyListener mechanism
		 * where we have the special handling in the DataTypes.  Instead, it is passed
		 * through the KeyMap and Action mechanism, and the default Action on the
		 * JTextField is to add the character to the end of the existing text, or if it is delete
		 * to delete the last character of the existing text.  In most cases, this is ok, but
		 * there are three special cases of which we only handle two here:
		 * 	- If the data field currently contains "<null>" and the user types a character,
		 * 	  we want that character to replace the string "<null>", which represents the
		 * 	  null value.  In this case we process the event normally, which usually adds
		 * 	  the char to the end of the string, then remove the char afterwards.
		 * 	  We take this approach rather than just immediately replacing the "<null>"
		 * 	  with the char because there are some chars that should not be put into
		 * 	  the editable text, such as control-characters.
		 * 	- If the data field contains "<null>" and the user types a delete, we do not
		 * 	  want to delete the last character from the string "<null>" since that string
		 * 	  represents the null value.  In this case we simply ignore the user input.
		 * 	- Whether or not the field initially contains null, we do not run the input validation
		 * 	  function for the DataType on the input character.  This means that the user
		 * 	  can type an illegal character into the field.  For example, after entering an
		 * 	  Integer field by typing a tab, the user can enter a letter (e.g. "a") into that
		 * 	  field.  The normal keyListener processing prevents that, but we cannot
		 * 	  call it from this point.  (More accurately, I cannot figure out how to do it
		 * 	  easilly.)  Thus the user may enter one character of invalid data into the field.
		 * 	  This is not too serious a problem, however, because the normal validation
		 * 	  is still done when the user leaves the field and it SQuirreL tries to convert
		 * 	  the text into an object of the correct type, so errors of this nature will still
		 * 	  be caught.  They just won't be prevented.
		 */
		public void processKeyEvent(KeyEvent e) {

				// handle special case of delete with <null> contents
				if (e.getKeyChar() == '\b' && getEditorComponent() != null &&
						((RestorableJTextField)getEditorComponent()).getText().equals("<null>") ) {
						//ignore the user input
						return;
				}

				// generally for KEY_TYPED this means add the typed char to the end of the text,
				// but there are some things (e.g. control chars) that are ignored, so let the
				// normal processing do its thing
				super.processKeyEvent(e);

				// now check to see if the original contents were <null>
				// and we have actually added the input char to the end of it                                                              
				if (getEditorComponent() != null) {
						if (e.getID() == KeyEvent.KEY_TYPED && ((RestorableJTextField)getEditorComponent()).getText().length() == 7) {
								// check that we did not just add a char to a <null>
								if (((RestorableJTextField)getEditorComponent()).getText().equals("<null>"+e.getKeyChar())) {
										// replace the null with just the char
										((RestorableJTextField)getEditorComponent()).updateText(""+e.getKeyChar());
								}
						}
				}

		}


		/*
		 * When user leaves a cell after editing it, the contents of
		 * that cell need to be converted from a string into an
		 * object of the appropriate type before updating the table.
		 * However, when the call comes from the Popup window, the data
		 * has already been converted and validated.
		 * We assume that a String being passed in here is a value from
		 * a text field that needs to be converted to an object, and
		 * a non-string object has already been validated and converted.
		 */
		public void setValueAt(Object newValueString, int row, int col)
		{
			if (! (newValueString instanceof java.lang.String))
			{
				// data is an object - assume already validated
				super.setValueAt(newValueString, row, col);
				return;
			}

			// data is a String, so we need to convert to real object
			StringBuffer messageBuffer = new StringBuffer();

			int modelIndex = getColumnModel().getColumn(col).getModelIndex();
			ColumnDisplayDefinition colDef = getColumnDefinitions()[modelIndex];
			Object newValueObject = CellComponentFactory.validateAndConvert(
				colDef, getValueAt(row, col), (String) newValueString, messageBuffer);

			if (messageBuffer.length() > 0)
			{

				// i18n[dataSetViewerTablePanel.textCantBeConverted=The given text cannot be converted into the internal object.\nThe database has not been changed.\nThe conversion error was:\n{0}]
				String msg = s_stringMgr.getString("dataSetViewerTablePanel.textCantBeConverted", messageBuffer);

				if (s_log.isDebugEnabled()) {
					s_log.debug("setValueAt: msg from DataTypeComponent was: "+msg);
				}
				
				// display error message and do not update the table
				JOptionPane.showMessageDialog(this,
					msg,
					// i18n[dataSetViewerTablePanel.conversionError=Conversion Error]
					s_stringMgr.getString("dataSetViewerTablePanel.conversionError"),
					JOptionPane.ERROR_MESSAGE);
				
			}
			else
			{
				// data converted ok, so update the table
				super.setValueAt(newValueObject, row, col);
			}
		}


		public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
		{
			TableColumnModel tcm = createColumnModel(colDefs);
			setColumnModel(tcm);
			_typedModel.setHeadings(colDefs);

			// just in case table is editable, call creator to set up cell editors
			_creator.setCellEditors(this);
			_tablePopupMenu.reset();
		}

		MyTableModel getTypedModel()
		{
			return _typedModel;
		}

		/**
		 * Display the popup menu for this component.
		 */
		private void displayPopupMenu(MouseEvent evt)
		{
			_tablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}


		private TableColumnModel createColumnModel(ColumnDisplayDefinition[] colDefs)
		{
			//_colDefs = hdgs;
			TableColumnModel cm = new DefaultTableColumnModel();

			_rntc = new RowNumberTableColumn();

			for (int i = 0; i < colDefs.length; ++i)
			{
				ColumnDisplayDefinition colDef = colDefs[i];

				int colWidth;

            if (null == colDef.getAbsoluteWidth())
            {
               colWidth = colDef.getDisplayWidth() * _multiplier;
               if (colWidth > MAX_COLUMN_WIDTH * _multiplier)
               {
                  colWidth = MAX_COLUMN_WIDTH * _multiplier;
               }
               else if (colWidth < MIN_COLUMN_WIDTH * _multiplier)
               {
                    colWidth = MIN_COLUMN_WIDTH * _multiplier;
               }
            }
            else
            {
               colWidth = colDef.getAbsoluteWidth();
            }

            ExtTableColumn col = new ExtTableColumn(i, colWidth,
					CellComponentFactory.getTableCellRenderer(colDefs[i]), null);

            String headerValue = colDef.getColumnHeading();
            col.setHeaderValue(headerValue);
				col.setColumnDisplayDefinition(colDef);
				cm.addColumn(col);
			}

			return cm;
		}

		void setShowRowNumbers(boolean show)
		{
			try
			{
				int rowNumColIx = getColumnModel().getColumnIndex(RowNumberTableColumn.ROW_NUMBER_COL_IDENTIFIER);
				_tableHeader.columnIndexWillBeRemoved(rowNumColIx);
			}
			catch(IllegalArgumentException e)
			{
				// Column not in model
			}

			getColumnModel().removeColumn(_rntc);
			if(show)
			{
				_tableHeader.columnIndexWillBeAdded(0);
				getColumnModel().addColumn(_rntc);
				getColumnModel().moveColumn(getColumnModel().getColumnCount()-1, 0);
			}
		}

		private void createGUI(boolean allowUpdate,
                             IDataSetUpdateableModel updateableObject, int selectionMode)
		{
			setSelectionMode(selectionMode);
			setRowSelectionAllowed(false);
			setColumnSelectionAllowed(false);
			setCellSelectionEnabled(true);
			getTableHeader().setResizingAllowed(true);
			getTableHeader().setReorderingAllowed(true);
			setAutoCreateColumnsFromModel(false);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setTableHeader(_tableHeader);
			_tableHeader.setTable(this);

			_tablePopupMenu = new TablePopupMenu(allowUpdate, updateableObject, DataSetViewerTablePanel.this, getDataModelImplementationDetails());
			_tablePopupMenu.setTable(this);

         addMouseListener(new MouseAdapter()
         {
            public void mousePressed(MouseEvent evt)
            {
               onMousePressed(evt, false);
            }

            public void mouseReleased(MouseEvent evt)
            {
               onMouseReleased(evt);
            }
         });

         getTableHeader().addMouseListener(new MouseAdapter()
         {
            public void mousePressed(MouseEvent evt)
            {
               onMousePressed(evt, true);
            }

            public void mouseReleased(MouseEvent evt)
            {
               onMouseReleased(evt);
            }
         });

      }

      private void onMouseReleased(MouseEvent evt)
      {
         if (evt.isPopupTrigger())
         {
            this.displayPopupMenu(evt);
         }
      }

      private void onMousePressed(MouseEvent evt, boolean clickedOnTableHeader)
      {
         if (evt.isPopupTrigger())
         {
            this.displayPopupMenu(evt);
         }
         else if (evt.getClickCount() == 2 && false == clickedOnTableHeader)
         {
            // If this was done when the header was clicked
            // it prevents MS Excel like adopition of column
            // sizes by double click. See class ButtonTableHeader.

            // figure out which column the user clicked on
            // so we can pass in the right column description
            Point pt = evt.getPoint();
            TableColumnModel cm = this.getColumnModel();
            int columnIndexAtX = cm.getColumnIndexAtX(pt.x);

            int modelIndex = cm.getColumn(columnIndexAtX).getModelIndex();


            if (RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX != modelIndex)
            {
               ColumnDisplayDefinition colDefs[] = getColumnDefinitions();
               CellDataPopup.showDialog(this, colDefs[modelIndex], evt, this._creator.isTableEditable());

            }
         }
      }

      public void initColWidths()
      {
         _tableHeader.initColWidths();
      }
   }


	
	
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Implement the IDataSetTableControls interface,
	// functions needed to support table operations
	//
	// These functions are called from within MyJTable and MyTable to tell
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
		return CellComponentFactory.needToReRead(_colDefs[col], originalValue);
	}
	
	/**
	 * Re-read the contents of this cell from the database.
	 * If there is a problem, the message will have a non-zero length after return.
	 */
	public Object reReadDatum(Object[] values, int col, StringBuffer message) {
		// call the underlying model to get the whole data, if possible
		return ((IDataSetUpdateableTableModel)_updateableModel).
			reReadDatum(values, _colDefs, col, message);
	}
	
	/**
	 * Function to set up CellEditors.  Null for read-only tables.
	 */
	public void setCellEditors(JTable table) {}
	
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
}
