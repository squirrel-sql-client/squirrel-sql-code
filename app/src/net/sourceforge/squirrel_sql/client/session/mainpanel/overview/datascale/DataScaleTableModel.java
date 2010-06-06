package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.prefs.Preferences;

public class DataScaleTableModel extends AbstractTableModel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataScaleTableModel.class);

   private static final String PREF_KEY_COL_WIDTH_COLUMN = "Squirrel.overview.colWidthColumn";
   private static final String PREF_KEY_COL_WIDTH_DATA = "Squirrel.overview.colWidthData";


   public static final String COL_NAME_COLUMN = s_stringMgr.getString("DataScaleTableModel.colNameColumn");
   public static final String COL_NAME_DATA = s_stringMgr.getString("DataScaleTableModel.colNameData");

   public static final int DEFAULT_COL_WIDTH_COLUMN = 100;
   public static final int DEFAULT_COL_WIDTH_DATA = 1000;
   

   private DataScale[] _dataScales;


   public DataScaleTableModel(DataScale[] dataScales)
   {
      _dataScales = dataScales;
   }

   public int getColumnWidthForColName(String colName)
   {
      if(COL_NAME_COLUMN.equals(colName))
      {
         return Preferences.userRoot().getInt(PREF_KEY_COL_WIDTH_COLUMN, DEFAULT_COL_WIDTH_COLUMN);
      }
      else if(COL_NAME_DATA.equals(colName))
      {
         return Preferences.userRoot().getInt(PREF_KEY_COL_WIDTH_DATA, DEFAULT_COL_WIDTH_DATA);
      }
      else
      {
         throw new IllegalArgumentException("Unknown column name " + colName);
      }

   }


   public static String[] getColumnNames()
   {
      return new String[]{COL_NAME_COLUMN, COL_NAME_DATA};
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return 1 ==columnIndex;
   }

   @Override
   public int getRowCount()
   {
      return _dataScales.length;
   }

   @Override
   public int getColumnCount()
   {
      return 2;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex)
   {
      if (0 == columnIndex)
      {
         return _dataScales[rowIndex].getColumn();
      }
      else
      {
         return _dataScales[rowIndex];
      }
   }

   public DataScale getDataScaleAt(int row)
   {
      return _dataScales[row];
   }

   public static void saveColumWidhts(int wColumn, int wData)
   {
      Preferences.userRoot().putInt(PREF_KEY_COL_WIDTH_COLUMN, wColumn);
      Preferences.userRoot().putInt(PREF_KEY_COL_WIDTH_DATA, wData);
   }
}
