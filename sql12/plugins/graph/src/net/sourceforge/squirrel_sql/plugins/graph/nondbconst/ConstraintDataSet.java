package net.sourceforge.squirrel_sql.plugins.graph.nondbconst;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.ConstraintView;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

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

      for (int i = 0; i < constraintView.getData().getFkColumnInfos().length; i++)
      {
         ColumnInfo fkCol = constraintView.getData().getFkColumnInfos()[i];
         ColumnInfo pkCol = constraintView.getData().getPkColumnInfos()[i];

         ContraintDisplayData buf = new ContraintDisplayData(fkCol, pkCol);
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
            return _contraintDisplayData.get(_curIx).getFkCol();
         case 1:
            return _contraintDisplayData.get(_curIx).getPkCol();
         default:
            throw new IndexOutOfBoundsException("Invalid column index " + columnIndex);
      }
   }

   public ArrayList<ContraintDisplayData> removeRows(int[] rows)
   {
      if(0 == rows.length)
      {
         return new ArrayList<ContraintDisplayData>();
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

      return toRemove;
   }

   public void addRow(ColumnInfo fkCol, ColumnInfo pkCol)
   {
      _contraintDisplayData.add(new ContraintDisplayData(fkCol, pkCol));
      _curIx = -1;
   }

   public void writeConstraintView(ConstraintView constraintView, TableFrameController fkFrameOriginatingFrom, TableFrameController pkFramePointingTo)
   {
      constraintView.getData().removeAllColumns();

      ArrayList<ColumnInfo> pkCols = new ArrayList<ColumnInfo>();
      ArrayList<ColumnInfo> fkCols = new ArrayList<ColumnInfo>();
      for (ContraintDisplayData contraintDisplayData : _contraintDisplayData)
      {
         fkCols.add(contraintDisplayData.getFkCol());
         pkCols.add(contraintDisplayData.getPkCol());
      }
      constraintView.getData().setColumnInfos(pkCols, fkCols);
   }

   public boolean isEmpty()
   {
      return _contraintDisplayData.isEmpty();
   }

}