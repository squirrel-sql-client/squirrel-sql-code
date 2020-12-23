package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellRenderer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableJTextField;
import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.ColoringService;
import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.gui.RectangleSelectionHandler;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * The JTable used for displaying all DB ResultSet info.
 */
public final class DataSetViewerTable extends JTable
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetViewerTable.class);

   private ILogger s_log = LoggerController.createLogger(DataSetViewerTable.class);


   private DataSetViewerTablePanel _dataSetViewerTablePanel;
   private final int _multiplier;
   private static final String data = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";

   private TablePopupMenuHandler _tablePopupMenuHandler;

   private RectangleSelectionHandler _rectangleSelectionHandler = new RectangleSelectionHandler(this);
   private RowNumberTableColumn _rowNumberTableColumn;
   private ButtonTableHeader _tableHeader = new ButtonTableHeader();


   private ColoringService _coloringService = new ColoringService(this);


   DataSetViewerTable(DataSetViewerTablePanel dataSetViewerTablePanel, IDataSetViewAccess dataSetViewAccess, IDataSetUpdateableModel updateableObject, int listSelectionMode, ISession session)
   {
      super(new SortableTableModel(new DataSetViewerTableModel(dataSetViewerTablePanel)));
      _dataSetViewerTablePanel = dataSetViewerTablePanel;

      _multiplier = getFontMetrics(getFont()).stringWidth(data) / data.length();
      setRowHeight(getFontMetrics(getFont()).getHeight());

      boolean allowUpdate = false;
      // we want to allow editing of read-only tables on-demand, but
      // it would be confusing to include the "Make Editable" option
      // when we are already in edit mode, so only allow that option when
      // the background model is updateable AND we are not already editing
      if (updateableObject != null && !dataSetViewAccess.isTableEditable())
      {
         allowUpdate = true;
      }

      createGUI(allowUpdate, updateableObject, listSelectionMode, session);

      // just in case table is editable, call dataSetViewAccess to set up cell editors
      _dataSetViewerTablePanel.setCellEditors(this);

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

   /*
    * override the JTable method so that whenever something asks for
    * the cellEditor, we save a reference to that cell editor.
    * Our ASSUMPTION is that the cell editor is only requested
    * when it is about to be activated.
    */
   public TableCellEditor getCellEditor(int row, int col)
   {
      TableCellEditor cellEditor = super.getCellEditor(row, col);
      _dataSetViewerTablePanel.setCurrentCellEditor((DefaultCellEditor) cellEditor);
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
    * - If the data field currently contains "<null>" and the user types a character,
    * we want that character to replace the string "<null>", which represents the
    * null value.  In this case we process the event normally, which usually adds
    * the char to the end of the string, then remove the char afterwards.
    * We take this approach rather than just immediately replacing the "<null>"
    * with the char because there are some chars that should not be put into
    * the editable text, such as control-characters.
    * - If the data field contains "<null>" and the user types a delete, we do not
    * want to delete the last character from the string "<null>" since that string
    * represents the null value.  In this case we simply ignore the user input.
    * - Whether or not the field initially contains null, we do not run the input validation
    * function for the DataType on the input character.  This means that the user
    * can type an illegal character into the field.  For example, after entering an
    * Integer field by typing a tab, the user can enter a letter (e.g. "a") into that
    * field.  The normal keyListener processing prevents that, but we cannot
    * call it from this point.  (More accurately, I cannot figure out how to do it
    * easilly.)  Thus the user may enter one character of invalid data into the field.
    * This is not too serious a problem, however, because the normal validation
    * is still done when the user leaves the field and it SQuirreL tries to convert
    * the text into an object of the correct type, so errors of this nature will still
    * be caught.  They just won't be prevented.
    */
   public void processKeyEvent(KeyEvent e)
   {

      // handle special case of delete with <null> contents
      if (e.getKeyChar() == '\b' && getEditorComponent() != null &&
            ((RestorableJTextField) getEditorComponent()).getText().equals(StringUtilities.NULL_AS_STRING))
      {
         //ignore the user input
         return;
      }

      // generally for KEY_TYPED this means add the typed char to the end of the text,
      // but there are some things (e.g. control chars) that are ignored, so let the
      // normal processing do its thing
      super.processKeyEvent(e);

      // now check to see if the original contents were <null>
      // and we have actually added the input char to the end of it
      if (getEditorComponent() != null)
      {
         if (e.getID() == KeyEvent.KEY_TYPED && ((RestorableJTextField) getEditorComponent()).getText().length() == 7)
         {
            // check that we did not just add a char to a <null>
            if (((RestorableJTextField) getEditorComponent()).getText().equals(StringUtilities.NULL_AS_STRING + e.getKeyChar()))
            {
               // replace the null with just the char
               ((RestorableJTextField) getEditorComponent()).updateText("" + e.getKeyChar());
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
      if (!(newValueString instanceof String))
      {
         // data is an object - assume already validated
         super.setValueAt(newValueString, row, col);
         return;
      }

      // data is a String, so we need to convert to real object
      StringBuffer messageBuffer = new StringBuffer();

      int modelIndex = getColumnModel().getColumn(col).getModelIndex();
      ColumnDisplayDefinition colDef = _dataSetViewerTablePanel.getColumnDefinitions()[modelIndex];
      Object newValueObject = CellComponentFactory.validateAndConvert(
            colDef, getValueAt(row, col), (String) newValueString, messageBuffer);

      if (messageBuffer.length() > 0)
      {

         // i18n[dataSetViewerTablePanel.textCantBeConverted=The given text cannot be converted into the internal object.\nThe database has not been changed.\nThe conversion error was:\n{0}]
         String msg = s_stringMgr.getString("dataSetViewerTablePanel.textCantBeConverted", messageBuffer);

         if (s_log.isDebugEnabled())
         {
            s_log.debug("setValueAt: msg from DataTypeComponent was: " + msg);
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
      getDataSetViewerTableModel().setHeadings(colDefs);

      // just in case table is editable, call creator to set up cell editors
      _dataSetViewerTablePanel.setCellEditors(this);
      _tablePopupMenuHandler.reset();
   }

   public DataSetViewerTableModel getDataSetViewerTableModel()
   {
      return (DataSetViewerTableModel) ((SortableTableModel) getModel()).getActualModel();
   }

   public SortableTableModel getSortableTableModel()
   {
      return (SortableTableModel) getModel();
   }

   /**
    * Display the popup menu for this component.
    */
   private void displayPopupMenu(MouseEvent evt)
   {
      _tablePopupMenuHandler.displayPopupMenu(evt);
   }


   private TableColumnModel createColumnModel(ColumnDisplayDefinition[] colDefs)
   {
      TableColumnModel cm = new DefaultTableColumnModel();

      _rowNumberTableColumn = new RowNumberTableColumn();

      for (int i = 0; i < colDefs.length; ++i)
      {
         ColumnDisplayDefinition colDef = colDefs[i];

         int colWidth;

         if (null == colDef.getAbsoluteWidth())
         {
            colWidth = colDef.getDisplayWidth() * _multiplier;
            if (colWidth > IDataSetViewer.MAX_COLUMN_WIDTH * _multiplier)
            {
               colWidth = IDataSetViewer.MAX_COLUMN_WIDTH * _multiplier;
            }
            else if (colWidth < IDataSetViewer.MIN_COLUMN_WIDTH * _multiplier)
            {
               colWidth = IDataSetViewer.MIN_COLUMN_WIDTH * _multiplier;
            }
         }
         else
         {
            colWidth = colDef.getAbsoluteWidth();
         }

         CellRenderer tableCellRenderer = CellComponentFactory.getTableCellRenderer(colDefs[i]);
         tableCellRenderer.setColoringService(_coloringService);

         ExtTableColumn col = new ExtTableColumn(i, colWidth, tableCellRenderer, null);

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
      catch (IllegalArgumentException e)
      {
         // Column not in model
      }

      getColumnModel().removeColumn(_rowNumberTableColumn);
      if (show)
      {
         _tableHeader.columnIndexWillBeAdded(0);
         getColumnModel().addColumn(_rowNumberTableColumn);
         getColumnModel().moveColumn(getColumnModel().getColumnCount() - 1, 0);
      }

      _tablePopupMenuHandler.ensureRowNumersMenuItemIsUpToDate(show);
   }

   public boolean isShowingRowNumbers()
   {
      try
      {
         getColumnModel().getColumnIndex(RowNumberTableColumn.ROW_NUMBER_COL_IDENTIFIER);
         return true;
      }
      catch (IllegalArgumentException e)
      {
         return false;
      }
   }


   private void createGUI(boolean allowUpdate, IDataSetUpdateableModel updateableObject, int selectionMode, ISession session)
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

      _tablePopupMenuHandler = new TablePopupMenuHandler(allowUpdate, updateableObject, _dataSetViewerTablePanel, session);

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
            ColumnDisplayDefinition colDefs[] = _dataSetViewerTablePanel.getColumnDefinitions();
            CellDataPopup.showDialog(this, colDefs[modelIndex], evt, _dataSetViewerTablePanel.isTableEditable());

         }
      }
   }

   public void initColWidths()
   {
      _tableHeader.initColWidths();
   }

   public ColoringService getColoringService()
   {
      return _coloringService;
   }

   public void scrollToVisible(int viewRow, int viewCol)
   {
      Rectangle cellRect = getCellRect(viewRow, viewCol, true);
      scrollRectToVisible(cellRect);
      repaint(cellRect);
   }

   public ButtonTableHeader getButtonTableHeader()
   {
      return (ButtonTableHeader) getTableHeader();
   }

   public int[] getSelectedModelRows()
   {
      int[] selectedViewRows = getSelectedRows();

      int[] ret = new int[selectedViewRows.length];

      for (int i = 0; i < selectedViewRows.length; i++)
      {
         ret[i] = (((SortableTableModel)getModel()).transformToModelRow(selectedViewRows[i]));
      }

      return ret;
   }

}
