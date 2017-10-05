package org.squirrelsql.session.graph;

import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.TableInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GraphColumn
{
   private ColumnInfo _columnInfo;
   private final PrimaryKeyInfo _primaryKeyInfo;
   private final ImportedKeysInfo _impKeysInfo;
   private ColumnPersistence _columnPersistence;
   private String _postFix;
   private HashMap<String, NonDbImportedKey> _nonDbImportedKeyByNonDbFkId = new HashMap<>();
   private HashSet<String> _nonDbFkIdsPointingAtMe = new HashSet<>();

   public GraphColumn(ColumnInfo columnInfo, PrimaryKeyInfo primaryKeyInfo, ImportedKeysInfo impKeysInfo, ColumnPersistence columnPersistence, GraphChannel graphChannel)
   {
      _columnInfo = columnInfo;
      _primaryKeyInfo = primaryKeyInfo;
      _impKeysInfo = impKeysInfo;
      _columnPersistence = columnPersistence;

      if(null == _columnPersistence)
      {
         _columnPersistence = new ColumnPersistence(_columnInfo.getCatalogName(), _columnInfo.getSchemaName(), _columnInfo.getTableName(), _columnInfo.getColName());

      }

      _postFix = " ";

      if(_primaryKeyInfo.belongsToPk(_columnInfo))
      {
         _postFix += "(PK)";
      }

      if(_impKeysInfo.isFk(_columnInfo))
      {
         _postFix += "(FK)";
      }


      if(null != columnPersistence)
      {
         graphChannel.addAllTablesAddedListener(() -> onAllTablesAdded(columnPersistence, graphChannel));
      }

   }

   private void onAllTablesAdded(ColumnPersistence pers, GraphChannel graphChannel)
   {
      _nonDbFkIdsPointingAtMe = pers.getNonDbFkIdsPointingAtMe();
      _nonDbImportedKeyByNonDbFkId = ColumnPersistence.toNonDbImportedKeyByNonDbFkId(pers, graphChannel.getGraphFinder());
   }

   public String getDescription()
   {
      return _columnInfo.getDescription() + _postFix;
   }

   public boolean belongsToPk()
   {
      return _primaryKeyInfo.belongsToPk(_columnInfo);
   }

   public boolean belongsToFk(String fkNameOrId)
   {
      return _impKeysInfo.belongsToFk(fkNameOrId, _columnInfo);
   }

   public String getFkNameTo(TableInfo toPkTable)
   {
      return _impKeysInfo.getFkNameTo(toPkTable, _columnInfo);
   }


   public void addNonDbImportedKey(NonDbImportedKey nonDbImportedKey)
   {
      _nonDbImportedKeyByNonDbFkId.put(nonDbImportedKey.getNonDbFkId(), nonDbImportedKey);
   }

   public boolean doesNonDbFkIdPointAtMe(String nonDbFkId)
   {
      return _nonDbFkIdsPointingAtMe.contains(nonDbFkId);
   }

   public void addNonDbFkIdPointingAtMe(String nonDbFkId)
   {
      _nonDbFkIdsPointingAtMe.add(nonDbFkId);
   }

   public ArrayList<NonDbImportedKey> getNonDbImportedKeys()
   {
      return new ArrayList<>(_nonDbImportedKeyByNonDbFkId.values());
   }

   public NonDbImportedKey getNonDbImportedKeyForId(String nonDbFkId)
   {
      return _nonDbImportedKeyByNonDbFkId.get(nonDbFkId);
   }


   public boolean importsNonDbFkId(String nonDbFkId)
   {
      return _nonDbImportedKeyByNonDbFkId.containsKey(nonDbFkId);
   }

   public boolean isMyNonDbPkCol(GraphColumn pkCol, String nonDbFkId)
   {
      NonDbImportedKey nonDbImportedKey = _nonDbImportedKeyByNonDbFkId.get(nonDbFkId);

      if(null == nonDbImportedKey)
      {
         return false;
      }

      return nonDbImportedKey.getColumnThisImportedKeyPointsTo().matches(pkCol);
   }

   private boolean matches(GraphColumn other)
   {
      return _columnInfo.matches(other._columnInfo);
   }

   public ColumnInfo getColumnInfo()
   {
      return _columnInfo;
   }

   @Override
   public String toString()
   {
      return _columnInfo.getDescription();
   }

   public void removeNonDbFkId(String nonDbFkId)
   {
      _nonDbImportedKeyByNonDbFkId.remove(nonDbFkId);
      _nonDbFkIdsPointingAtMe.remove(nonDbFkId);
   }

   public ColumnPersistence getColumnPersistence()
   {

      _columnPersistence.setNonDbFkIdsPointingAtMe(_nonDbFkIdsPointingAtMe);

      HashMap<String, NonDbImportedKeyPersistence> buf = new HashMap<>();
      for (Map.Entry<String, NonDbImportedKey> entry : _nonDbImportedKeyByNonDbFkId.entrySet())
      {
         buf.put(entry.getKey(), NonDbImportedKeyPersistence.toNonDbImportedKeyPersistence(entry.getValue()));
      }
      _columnPersistence.setNonDbImportedKeyPersistenceByNonDbFkId(buf);

      return _columnPersistence;
   }

   public List<String> removeNonDBConstraintDataTo(GraphColumn otherCol)
   {
      ArrayList<String> keysToRemove = new ArrayList<>();

      for (String nonDbFkId : otherCol._nonDbImportedKeyByNonDbFkId.keySet())
      {
         removeNonDbFkId(nonDbFkId);
         keysToRemove.add(nonDbFkId);
      }

      for (String nonDbFkId : otherCol._nonDbFkIdsPointingAtMe)
      {
         removeNonDbFkId(nonDbFkId);
         keysToRemove.add(nonDbFkId);
      }

      return keysToRemove;
   }

   public String getQualifiedTableName()
   {
      return _columnInfo.getQualifiedTableName();
   }

   public String getPkTableName(String fkNameOrId)
   {
      if(null != _nonDbImportedKeyByNonDbFkId.get(fkNameOrId))
      {
         return _nonDbImportedKeyByNonDbFkId.get(fkNameOrId).getTableThisImportedKeyPointsTo().getName();
      }

      return _impKeysInfo.getPkTable(fkNameOrId).getName().toString();
   }

   public ColumnConfigurationPersistence getColumnConfigurationPersistence()
   {
      return _columnPersistence.getColumnConfigurationPersistence();
   }
}
