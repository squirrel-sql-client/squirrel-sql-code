package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class NonExistentingTableInfoPlaceHolder implements ITableInfo
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(NonExistentingTableInfoPlaceHolder.class);


   private String _catalog;
   private String _schema;
   private String _tableName;

   public NonExistentingTableInfoPlaceHolder(String catalog, String schema, String tableName)
   {
      _catalog = catalog;
      _schema = schema;
      _tableName = tableName;
   }

   @Override
   public String getType()
   {
      return "TABLE";
   }

   @Override
   public String getRemarks()
   {
      return null;
   }

   @Override
   public ITableInfo[] getChildTables()
   {
      return new ITableInfo[0];
   }

   @Override
   public ForeignKeyInfo[] getImportedKeys()
   {
      return new ForeignKeyInfo[0];
   }

   @Override
   public ForeignKeyInfo[] getExportedKeys()
   {
      return new ForeignKeyInfo[0];
   }

   @Override
   public void setExportedKeys(ForeignKeyInfo[] foreignKeys)
   {
   }

   @Override
   public void setImportedKeys(ForeignKeyInfo[] foreignKeys)
   {
   }

   @Override
   public String getCatalogName()
   {
      return null;
   }

   @Override
   public String getSchemaName()
   {
      return _schema;
   }

   @Override
   public String getSimpleName()
   {
      return s_stringMgr.getString("nonExistentingTableInfoPlaceHolder.tableNotExisting", _tableName);
   }

   @Override
   public String getQualifiedName()
   {
      String qualifiedTableName = SQLUtilities.getQualifiedTableName(_catalog, _schema, _tableName);
      return s_stringMgr.getString("nonExistentingTableInfoPlaceHolder.tableNotExisting", qualifiedTableName);
   }

   @Override
   public DatabaseObjectType getDatabaseObjectType()
   {
      return DatabaseObjectType.TABLE;
   }

   @Override
   public int compareTo(IDatabaseObjectInfo o)
   {
      if(false == o instanceof NonExistentingTableInfoPlaceHolder)
      {
         return 1;
      }

      NonExistentingTableInfoPlaceHolder neth = (NonExistentingTableInfoPlaceHolder) o;

      return getQualifiedName().compareTo(neth.getQualifiedName());
   }
}
