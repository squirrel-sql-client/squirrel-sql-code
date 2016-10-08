package org.squirrelsql.session.graph;

import org.squirrelsql.services.SQLUtil;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.TableInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NonDbColumnImportPersistence
{
   private String _catalogName;
   private String _schemaName;
   private String _tableName;
   private String _colName;

   private HashSet<String> _nonDbFkIdsPointingAtMe = new HashSet<>();
   private HashMap<String, NonDbImportedKeyPersistence> _nonDbImportedKeyPersistenceByNonDbFkId = new HashMap<>();

   public NonDbColumnImportPersistence(String catalogName, String schemaName, String tableName, String colName)
   {
      _catalogName = catalogName;
      _schemaName = schemaName;
      _tableName = tableName;
      _colName = colName;
   }

   public NonDbColumnImportPersistence()
   {
   }

   public void setNonDbFkIdsPointingAtMe(HashSet<String> nonDbFkIdsPointingAtMe)
   {
      _nonDbFkIdsPointingAtMe = nonDbFkIdsPointingAtMe;
   }

   public HashSet<String> getNonDbFkIdsPointingAtMe()
   {
      return _nonDbFkIdsPointingAtMe;
   }

   public void setNonDbImportedKeyPersistenceByNonDbFkId(HashMap<String, NonDbImportedKeyPersistence> nonDbImportedKeyPersistenceByNonDbFkId)
   {
      _nonDbImportedKeyPersistenceByNonDbFkId = nonDbImportedKeyPersistenceByNonDbFkId;
   }

   public HashMap<String, NonDbImportedKeyPersistence> getNonDbImportedKeyPersistenceByNonDbFkId()
   {
      return _nonDbImportedKeyPersistenceByNonDbFkId;
   }

   public String getCatalogName()
   {
      return _catalogName;
   }

   public String getSchemaName()
   {
      return _schemaName;
   }

   public String getTableName()
   {
      return _tableName;
   }

   public String getColName()
   {
      return _colName;
   }

   public static NonDbColumnImportPersistence getMatching(ColumnInfo c, List<NonDbColumnImportPersistence> nonDbColumnImportPersistences)
   {
      for (NonDbColumnImportPersistence pers : nonDbColumnImportPersistences)
      {
         if(SQLUtil.getQualifiedName(pers.getCatalogName(), pers.getSchemaName(), pers.getTableName()).equalsIgnoreCase(SQLUtil.getQualifiedName(c.getCatalogName(), c.getSchemaName(), c.getTableName())))
         {
            if(pers.getColName().equalsIgnoreCase(c.getColName()))
            {
               return pers;
            }
         }
      }

      return null;
   }

   public static HashMap<String, NonDbImportedKey> toNonDbImportedKeyByNonDbFkId(NonDbColumnImportPersistence pers, GraphFinder finder)
   {
      HashMap<String, NonDbImportedKey> ret = new HashMap<>();

      for (Map.Entry<String, NonDbImportedKeyPersistence> entry : pers.getNonDbImportedKeyPersistenceByNonDbFkId().entrySet())
      {
         NonDbImportedKeyPersistence value = entry.getValue();

         GraphColumn col = finder.findCol(value.getCatalogName(), value.getSchemaName(), value.getTableName(), value.getColName());
         TableInfo table = finder.getTable(SQLUtil.getQualifiedName(value.getCatalogName(), value.getSchemaName(), value.getTableName()));

         //if (null != col && null != table)
         {
            ret.put(entry.getKey(), new NonDbImportedKey(value.getNonDbFkId(), col, table));
         }
      }

      return ret;
   }

   public void setCatalogName(String catalogName)
   {
      _catalogName = catalogName;
   }

   public void setSchemaName(String schemaName)
   {
      _schemaName = schemaName;
   }

   public void setTableName(String tableName)
   {
      _tableName = tableName;
   }

   public void setColName(String colName)
   {
      _colName = colName;
   }
}
