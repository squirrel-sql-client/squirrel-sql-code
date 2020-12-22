package net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum.RowColAndSumController;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RowsWindowFrame extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RowsWindowFrame.class);

   private static final String PREF_KEY_ROWS_WINDOW_FRAME_WIDTH = "Squirrel.rowselectionwindow.FrameWidth";
   private static final String PREF_KEY_ROWS_WINDOW_FRAME_HIGHT = "Squirrel.rowselectionwindow.FrameHight";

   private final JPanel _contentPanel;
   private ArrayList<ColumnDisplayDefinition> _columnDisplayDefinitions;
   private ISession _session;
   private int _myCounterId;
   private DataSetViewerTablePanel _dataSetViewerTablePanel;

   private RowColAndSumController _rowColAndSumController = new RowColAndSumController();

   public RowsWindowFrame(List<Object[]> rows, Window parent, ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions, ISession session)
   {
      super(parent);
      _columnDisplayDefinitions = columnDisplayDefinitions;
      _session = session;

      _contentPanel = initContentPanel(rows);

      getContentPane().setLayout(new GridLayout(1,1));
      getContentPane().add(_contentPanel);

      int width = Props.getInt(PREF_KEY_ROWS_WINDOW_FRAME_WIDTH, 300);
      int height = Props.getInt(PREF_KEY_ROWS_WINDOW_FRAME_HIGHT, 300);

      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onWindowClosing();
         }
      });


      setSize(new Dimension(width, height));

      setVisible(true);
   }

   private JPanel initContentPanel(List<Object[]> rows)
   {
      JPanel ret;
      ret = new JPanel(new BorderLayout(0, 3));

      _dataSetViewerTablePanel = DataSetViewerTablePanelUtil.createDataSetViewerTablePanel(rows, _columnDisplayDefinitions, _session);
      ret.add(new JScrollPane(_dataSetViewerTablePanel.getComponent()), BorderLayout.CENTER);


      _rowColAndSumController.setDataSetViewer(_dataSetViewerTablePanel);
      JPanel pnlNorth = new JPanel(new BorderLayout());

      _rowColAndSumController.getPanel().setBorder(BorderFactory.createEmptyBorder(2,0,2,5));
      pnlNorth.add(_rowColAndSumController.getPanel(), BorderLayout.EAST);
      pnlNorth.add(new JPanel(), BorderLayout.CENTER);

      _rowColAndSumController.setRowColSumLayoutListener(() -> onRowColSumLayoutDone(pnlNorth));

      ret.add(pnlNorth, BorderLayout.NORTH);
      return ret;
   }

   private void onRowColSumLayoutDone(JPanel pnlNorth)
   {
      pnlNorth.revalidate();
   }

   private void onWindowClosing()
   {
      Dimension size = getSize();

      Props.putInt(PREF_KEY_ROWS_WINDOW_FRAME_WIDTH, size.width);
      Props.putInt(PREF_KEY_ROWS_WINDOW_FRAME_HIGHT, size.height);

      Main.getApplication().getRowsWindowFrameRegistry().remove(this);
   }

   public void appendSelectedRows(List<Object[]> rows, ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions)
   {
      HashMap<Integer, Integer> myIndexByOtherIndexMap = getMyIndexByOtherIndexMap(columnDisplayDefinitions);

      if(null == myIndexByOtherIndexMap)
      {
         throw new IllegalStateException("appendSelectedRows() should be called after columnsMatch() was called to check.");
      }

      for (Object[] row : rows)
      {
         Object[] myRow = new Object[row.length];

         for (int i = 0; i < row.length; i++)
         {
            myRow[myIndexByOtherIndexMap.get(i)] = row[i];
         }

         RowSelectionTableUtil.getActualTableModel(_dataSetViewerTablePanel).addRow(myRow);

      }

      RowSelectionTableUtil.getActualTableModel(_dataSetViewerTablePanel).allRowsAdded();


      DataSetViewerTable table = _dataSetViewerTablePanel.getTable();

      table.getDataSetViewerTableModel().fireTableDataChanged();
      table.validate();


      SwingUtilities.invokeLater(() -> scrollTableToBottom(table));

   }

   private void scrollTableToBottom(JTable table)
   {
      int rowCount = RowSelectionTableUtil.getActualTableModel(table).getRowCount();

      Rectangle cellRect = table.getCellRect(rowCount, 0, true);

      Rectangle visibleRect = table.getVisibleRect();
      cellRect.x = visibleRect.x;
      cellRect.height = 1;
      cellRect.width = 1;
      cellRect.y = 1000000;

      table.scrollRectToVisible(cellRect);

      //table.scrollRectToVisible(new Rectangle(0, 1000000, 1,1));
   }

   public void close()
   {
      onWindowClosing();
      setVisible(false);
      dispose();
   }

   public boolean columnsMatch(ArrayList<ColumnDisplayDefinition> otherCols)
   {
      return null != getMyIndexByOtherIndexMap(otherCols);
   }

   private HashMap<Integer, Integer> getMyIndexByOtherIndexMap(ArrayList<ColumnDisplayDefinition> otherCols)
   {
      if(_columnDisplayDefinitions.size() != otherCols.size())
      {
         return null;
      }

      HashMap<Integer, Integer> myIndexByOthersIndex = new HashMap<>();

      ArrayList<ColumnDisplayDefinition> myColsBuf = new ArrayList<>(_columnDisplayDefinitions);


      HashSet<Integer> usedIncicesOfMine = new HashSet<>();
      for (int i = 0; i < otherCols.size(); i++)
      {
         ColumnDisplayDefinition otherCol = otherCols.get(i);

         boolean found = false;
         for (int j = 0; j < myColsBuf.size(); j++)
         {
            ColumnDisplayDefinition myCol = myColsBuf.get(j);

            if(   match(otherCol, myCol)
               && false == usedIncicesOfMine.contains(j) // There may be mor than on column of same name and type
              )
            {
               myIndexByOthersIndex.put(i, j);
               usedIncicesOfMine.add(j);
               found = true;
               break;
            }
         }

         if(false == found)
         {
            myIndexByOthersIndex = null;
            break;
         }
      }

      return myIndexByOthersIndex;
   }

   private boolean match(ColumnDisplayDefinition col1, ColumnDisplayDefinition col2)
   {
      return Utilities.equalsRespectNull(col1.getColumnName(), col2.getColumnName()) && col1.getSqlType() == col2.getSqlType();
   }

   public void setMyCounterId(int myCounterId)
   {
      _myCounterId = myCounterId;
      setTitle(s_stringMgr.getString("RowsWindowFrame.title") + " / " + _session.getTitle() + " / (" + _myCounterId + ")");
   }


   public void markWindow(boolean b)
   {
      if (b)
      {
         _contentPanel.setBorder(BorderFactory.createLineBorder(Color.red, 3));
      }
      else
      {
         _contentPanel.setBorder(BorderFactory.createEmptyBorder());
      }
   }
}
