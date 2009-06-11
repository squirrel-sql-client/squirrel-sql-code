package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.ArrayList;

public class ConstraintDataSet implements IDataSet
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ConstraintDataSet.class);


   private static final int DISPLAY_WIDTH = 30;

   private int _curIx = -1;

   private ColumnDisplayDefinition[] _columnDisplayDefinitions;

   private ArrayList<ContraintDisplayData> _contraintDisplayData = new ArrayList<ContraintDisplayData>();

   public ConstraintDataSet(ConstraintView constraintView, String fkTableName, String pkTableName)
   {

      for (ColumnInfo columnInfo : constraintView.getData().getColumnInfos())
      {
         ContraintDisplayData buf = new ContraintDisplayData(columnInfo.getName(), columnInfo.getImportedColumnName());
         _contraintDisplayData.add(buf);
      }


      _columnDisplayDefinitions = new ColumnDisplayDefinition[]
      {
         new ColumnDisplayDefinition(DISPLAY_WIDTH, s_stringMgr.getString("graph.ConstraintDataSet.LocalColumn", fkTableName)),
         new ColumnDisplayDefinition(DISPLAY_WIDTH, s_stringMgr.getString("graph.ConstraintDataSet.ReferencingColumn", pkTableName)),
      };
   }

   

   public int getColumnCount() throws DataSetException
   {
      return _columnDisplayDefinitions.length;
   }

   public DataSetDefinition getDataSetDefinition() throws DataSetException
   {
      return new DataSetDefinition(_columnDisplayDefinitions);
   }

   public boolean next(IMessageHandler msgHandler) throws DataSetException
   {
      return ++_curIx < _contraintDisplayData.size();
   }

   public Object get(int columnIndex) throws DataSetException
   {
      switch(columnIndex)
      {
         case 0:
            return _contraintDisplayData.get(_curIx).getColumnName();
         case 1:
            return _contraintDisplayData.get(_curIx).getImportedColumnName();
         default:
            throw new IndexOutOfBoundsException("Invalid column index " + columnIndex);
      }
   }

   public boolean removeRows(int[] rows)
   {
      if(0 == rows.length)
      {
         return false;
      }

      ArrayList<ContraintDisplayData> toRemove = new ArrayList<ContraintDisplayData>();

      for (int row : rows)
      {
         if (row < _contraintDisplayData.size())
         {
            toRemove.add(_contraintDisplayData.get(row));
         }
      }

      _contraintDisplayData.removeAll(toRemove);

      _curIx = -1;

      return true;
   }

   public boolean addRow(ColumnInfo fkColumn, ColumnInfo pkColumn)
   {

      for (ContraintDisplayData contraintDisplayData : _contraintDisplayData)
      {
         if(   contraintDisplayData.getColumnName().equalsIgnoreCase(fkColumn.getName())
            || contraintDisplayData.getImportedColumnName().equalsIgnoreCase(pkColumn.getName()))
         {
            // The rule for adding is: A fkColumn as well as a pkColumn should only occur once.
            return false;
         }
      }

      _contraintDisplayData.add(new ContraintDisplayData(fkColumn.getName(), pkColumn.getName()));
      _curIx = -1;

      return true;
   }

   public void writeConstraintView(ConstraintView constraintView, TableFrameController fkFrameOriginatingFrom, TableFrameController pkFramePointingTo)
   {
      constraintView.getData().removeAllColumns();
      for (ContraintDisplayData contraintDisplayData : _contraintDisplayData)
      {
         ColumnInfo fkCol = findColumnByName(fkFrameOriginatingFrom, contraintDisplayData.getColumnName());
         ColumnInfo pkCol = findColumnByName(pkFramePointingTo, contraintDisplayData.getImportedColumnName());
         fkCol.setImportData(pkFramePointingTo.getTableInfo().getSimpleName(), pkCol.getName(), constraintView.getData().getConstraintName(), true);
         constraintView.getData().addColumnInfo(fkCol);
      }
   }

   private ColumnInfo findColumnByName(TableFrameController toFindIn, String columnName)
   {
      for (ColumnInfo columnInfo : toFindIn.getColumnInfos())
      {
         if(columnInfo.getName().equalsIgnoreCase(columnName))
         {
            return columnInfo;
         }
      }

      throw new IllegalArgumentException("Column not found: " + columnName);
   }

   public boolean isEmpty()
   {
      return _contraintDisplayData.isEmpty();
   }

   private static class ContraintDisplayData
   {
      private String _colName;
      private String _importedColumnName;

      public ContraintDisplayData(String colName, String importedColumnName)
      {
         _colName = colName;
         _importedColumnName = importedColumnName;
      }

      public String getColumnName()
      {
         return _colName;
      }

      public String getImportedColumnName()
      {
         return _importedColumnName;
      }
   }
}