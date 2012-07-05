package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class DataScaleTableColumnWidthsPersister
{
   private static final int DEFAULT_COL_WIDTH_COLUMN = 100;
   private static final int DEFAULT_COL_WIDTH_DATA = 1000;

   private static final String PREF_KEY_COL_WIDTH_COLUMN = "Squirrel.overview.colWidthColumn";
   private static final String PREF_KEY_COL_WIDTH_DATA = "Squirrel.overview.colWidthData";

   private final Timer _timer;
   private JTableHeader _tableHeader;

   public DataScaleTableColumnWidthsPersister(final JTableHeader tableHeader)
   {
      _tableHeader = tableHeader;
      tableHeader.addMouseMotionListener(new MouseMotionAdapter()
      {
         @Override
         public void mouseDragged(MouseEvent e)
         {
            onMouseDraggedOnTableHeader(e);
         }
      });

      tableHeader.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            if(2 == e.getClickCount() && _tableHeader.getCursor().getType() == Cursor.E_RESIZE_CURSOR)
            {
               onMouseDraggedOnTableHeader(e);
            }
         }
      });

      _timer = new Timer(200, new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            saveColumnWidths();
         }
      });

      _timer.setRepeats(false);

   }

   private void onMouseDraggedOnTableHeader(MouseEvent e)
   {
      _timer.restart();
   }

   public static int getColumnWidthForColName(String colName)
   {
      if(DataScaleTableModel.COL_NAME_COLUMN.equals(colName))
      {
         return Preferences.userRoot().getInt(PREF_KEY_COL_WIDTH_COLUMN, DEFAULT_COL_WIDTH_COLUMN);
      }
      else if(DataScaleTableModel.COL_NAME_DATA.equals(colName))
      {
         return Preferences.userRoot().getInt(PREF_KEY_COL_WIDTH_DATA, DEFAULT_COL_WIDTH_DATA);
      }
      else
      {
         throw new IllegalArgumentException("Unknown column name " + colName);
      }

   }

   private void saveColumnWidths()
   {
      TableColumnModel columnModel = _tableHeader.getColumnModel();

      int wColumn = DEFAULT_COL_WIDTH_COLUMN;
      int wData = DEFAULT_COL_WIDTH_DATA;
      for (int i = 0; i < columnModel.getColumnCount(); i++)
      {
         if(DataScaleTableModel.COL_NAME_COLUMN.equals(columnModel.getColumn(i).getHeaderValue()))
         {
            wColumn = columnModel.getColumn(i).getWidth();
         }
         else if(DataScaleTableModel.COL_NAME_DATA.equals(columnModel.getColumn(i).getHeaderValue()))
         {
            wData = columnModel.getColumn(i).getWidth();
         }

      }

      Preferences.userRoot().putInt(PREF_KEY_COL_WIDTH_COLUMN, wColumn);
      Preferences.userRoot().putInt(PREF_KEY_COL_WIDTH_DATA, wData);

      //System.out.println("DataScaleTableColumnWidthsPersister.saveColumnWidths wC=" + wColumn + ";  wD=" +wData);

   }
}
