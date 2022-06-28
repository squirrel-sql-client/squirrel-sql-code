package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.ArrayList;

public class TableNameProvider
{
   private DataModelImplementationDetails _dataModelImplementationDetails;
   private boolean _colDefsFinished;

   private ArrayList<ColumnDisplayDefinition> _colDefs = new ArrayList<>();
   private String _tableName;

   public TableNameProvider(DataModelImplementationDetails dataModelImplementationDetails)
   {
      _dataModelImplementationDetails = dataModelImplementationDetails;
   }

   public void addColDef(ColumnDisplayDefinition colDef)
   {
      if(_colDefsFinished)
      {
         return;
      }

      _colDefs.add(colDef);

   }

   public void colDefsFinished()
   {
      _colDefsFinished = true;
   }

   public String getTableName()
   {
      if(null == _tableName)
      {
         _tableName = _findTableName();
      }
      return _tableName;
   }

   private String _findTableName()
   {
      for (ColumnDisplayDefinition colDef : _colDefs)
      {
         String tableName = _dataModelImplementationDetails.getTableName(colDef);
         if(null != tableName)
         {
            return tableName;
         }
      }

      return "PressCtrlH";
   }
}
