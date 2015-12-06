package org.squirrelsql.session.schemainfo.schemacacheloading;

import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.schemainfo.StructItem;
import org.squirrelsql.session.schemainfo.StructItemTableType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class SerializedCache implements Serializable
{
   private HashMap<StructItem, List<TableInfo>> _tableInfos = new HashMap<>();

   public List<TableInfo> getTableInfos(StructItem structItem)
   {
      return _tableInfos.get(structItem);
   }

   public void putTableInfos(StructItemTableType tableType, List<TableInfo> tableInfos)
   {
      _tableInfos.put(tableType, tableInfos);
   }

   public boolean empty()
   {
      // TODO
      return _tableInfos.isEmpty();
   }
}
