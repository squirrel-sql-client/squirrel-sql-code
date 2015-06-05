package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.CallDepthComboModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class DataScaleTableModel extends AbstractTableModel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataScaleTableModel.class);


   public static final String COL_NAME_COLUMN = s_stringMgr.getString("DataScaleTableModel.colNameColumn");
   public static final String COL_NAME_DATA = s_stringMgr.getString("DataScaleTableModel.colNameData");


   private DataScale[] _dataScales;
   private CallDepthComboModel _callDepth;
   private ArrayList<String> _columnNames;


   public DataScaleTableModel(DataScale[] dataScales, CallDepthComboModel callDepth)
   {
      _dataScales = dataScales;
      _callDepth = callDepth;
      _columnNames = createColumnNames(_dataScales);
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
         return _columnNames.get(rowIndex);
      }
      else
      {
         return _dataScales[rowIndex];
      }
   }

   private ArrayList<String> createColumnNames(DataScale[] dataScales)
   {
      ArrayList<String> columnNames = new ArrayList<String>();
      for (DataScale dataScale : dataScales)
      {
         String colName = dataScale.getColumn() + " [" + dataScale.getIndexedColumn().countDistinctValsForColumn() + "]";
         columnNames.add(colName);
      }
      return columnNames;
   }

   public DataScale getDataScaleAt(int row)
   {
      return _dataScales[row];
   }

   public int getDataScaleCount()
   {
      return _dataScales.length;
   }

   public CallDepthComboModel getCallDepth()
   {
      return _callDepth;
   }
}
