package net.sourceforge.squirrel_sql.client.session.action.dataimport.gui;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

public class CreateTableInDatabaseResult
{
   private ITableInfo _createdTable;
   private boolean _errorOccured;
   private boolean _tableNotFound;

   public CreateTableInDatabaseResult(ITableInfo createdTable)
   {
      _createdTable = createdTable;
   }

   public CreateTableInDatabaseResult()
   {
   }

   public CreateTableInDatabaseResult setErrorOccured(boolean errorOccured)
   {
      _errorOccured = errorOccured;
      return this;
   }

   public CreateTableInDatabaseResult setTableNotFound(boolean tableNotFound)
   {
      _tableNotFound = tableNotFound;
      return this;
   }


   public ITableInfo getCreatedTable()
   {
      return _createdTable;
   }

   public boolean isErrorOccured()
   {
      return _errorOccured;
   }

   public boolean isTableNotFound()
   {
      return _tableNotFound;
   }
}
