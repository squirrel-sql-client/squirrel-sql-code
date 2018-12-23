package net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
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
   private final DataSetViewerTablePanel _dataSetViewerTablePanel;


   public RowsWindowFrame(List<Object[]> rows, Window parent, ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions, ISession session)
   {
      super(parent);
      _columnDisplayDefinitions = columnDisplayDefinitions;
      _session = session;

      getContentPane().setLayout(new GridLayout(1,1));

      _contentPanel = new JPanel(new GridLayout(1, 1));
      getContentPane().add(_contentPanel);


      _dataSetViewerTablePanel = createDataSetViewerTablePanel(rows, _columnDisplayDefinitions);
      _contentPanel.add(new JScrollPane(_dataSetViewerTablePanel.getComponent()));

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


      final JTable table = _dataSetViewerTablePanel.getTable();

      table.invalidate();

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


   private DataSetViewerTablePanel createDataSetViewerTablePanel(List<Object[]> allRows, List<ColumnDisplayDefinition> columnDisplayDefinitions)
   {
      try
      {
         SimpleDataSet ods = new SimpleDataSet(allRows, columnDisplayDefinitions.toArray(new ColumnDisplayDefinition[columnDisplayDefinitions.size()]));
         DataSetViewerTablePanel dsv = new DataSetViewerTablePanel();
         dsv.init(null, new DataModelImplementationDetails(_session), _session);
         dsv.show(ods);
         return dsv;
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
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
