package org.squirrelsql.session.schemainfo.schemacacheloading;

import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.session.schemainfo.StructItem;
import org.squirrelsql.session.schemainfo.StructItemProcedureType;
import org.squirrelsql.session.schemainfo.StructItemTableType;
import org.squirrelsql.session.schemainfo.StructItemUDTType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class SerializedCache implements Serializable
{
   private HashMap<StructItem, List<TableInfo>> _tableInfos = new HashMap<>();
   private HashMap<StructItem, List<ProcedureInfo>> _procedureInfos = new HashMap<>();
   private HashMap<StructItem, List<UDTInfo>> _udtInfos = new HashMap<>();

   public List<TableInfo> getTableInfos(StructItemTableType structItem)
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
      return _tableInfos.isEmpty() && _procedureInfos.isEmpty() && _udtInfos.isEmpty();
   }

   public List<ProcedureInfo> getProcedureInfos(StructItemProcedureType procedureType)
   {
      return _procedureInfos.get(procedureType);
   }

   public void putProcedureInfos(StructItemProcedureType procedureType, List<ProcedureInfo> procedureInfos)
   {
      _procedureInfos.put(procedureType, procedureInfos);
   }

   public List<UDTInfo> getUDTInfos(StructItemUDTType udtType)
   {
      return _udtInfos.get(udtType);
   }

   public void putUDTInfos(StructItemUDTType udtType, List<UDTInfo> udtInfos)
   {
      _udtInfos.put(udtType, udtInfos);
   }
}
