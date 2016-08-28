package org.squirrelsql.session.graph;

import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.TableInfo;

import java.util.*;

public class GraphColumn
{
   private ColumnInfo _columnInfo;
   private final PrimaryKeyInfo _primaryKeyInfo;
   private final ImportedKeysInfo _impKeysInfo;
   private String _postFix;
   private HashMap<String, NonDbImportedKey> _nonDbImportedKeyByNonDbFkId = new HashMap<>();
   private HashSet<String> _nonDbFkIdsPointingAtMe = new HashSet<>();

   public GraphColumn(ColumnInfo columnInfo, PrimaryKeyInfo primaryKeyInfo, ImportedKeysInfo impKeysInfo)
   {
      _columnInfo = columnInfo;
      _primaryKeyInfo = primaryKeyInfo;
      _impKeysInfo = impKeysInfo;

      _postFix = " ";

      if(_primaryKeyInfo.belongsToPk(_columnInfo))
      {
         _postFix += "(PK)";
      }

      if(_impKeysInfo.isFk(_columnInfo))
      {
         _postFix += "(FK)";
      }

   }

   public String getDescription()
   {
      return _columnInfo.getDescription() + _postFix;
   }

   public boolean belongsToPk()
   {
      return _primaryKeyInfo.belongsToPk(_columnInfo);
   }

   public boolean belongsToFk(String fkName)
   {
      return _impKeysInfo.belongsToFk(fkName, _columnInfo);
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

   public boolean importsNonDbFkId(String nonDbFkId)
   {
      return _nonDbImportedKeyByNonDbFkId.containsKey(nonDbFkId);
   }
}
