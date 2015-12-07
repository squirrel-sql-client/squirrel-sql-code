package org.squirrelsql.session.schemainfo.schemacacheloading;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.session.schemainfo.StructItemProcedureType;
import org.squirrelsql.session.schemainfo.StructItemTableType;
import org.squirrelsql.session.schemainfo.StructItemUDTType;

import java.util.List;

public class SerializedCacheManager
{
   private I18n _i18n = new I18n(getClass());


   SerializedCache _serializedCache;
   private Alias _alias;

   public SerializedCacheManager(Alias alias)
   {
      _alias = alias;
   }

   public List<TableInfo> getTableInfos(StructItemTableType structItem)
   {
      return getSerializedCache().getTableInfos(structItem);
   }

   public void putTableInfos(StructItemTableType tableType, List<TableInfo> tableInfos)
   {
      getSerializedCache().putTableInfos(tableType, tableInfos);
   }

   public List<ProcedureInfo> getProcedureInfos(StructItemProcedureType procedureType)
   {
      return getSerializedCache().getProcedureInfos(procedureType);
   }

   public void putProcedureInfos(StructItemProcedureType procedureType, List<ProcedureInfo> procedureInfos)
   {
      getSerializedCache().putProcedureInfos(procedureType, procedureInfos);
   }

   public List<UDTInfo> getUDTInfos(StructItemUDTType udtType)
   {
      return getSerializedCache().getUDTInfos(udtType);
   }

   public void putUDTInfos(StructItemUDTType udtType, List<UDTInfo> udtInfos)
   {
      getSerializedCache().putUDTInfos(udtType, udtInfos);
   }


   private SerializedCache getSerializedCache()
   {
      if(null == _serializedCache)
      {
         _serializedCache = new SerializedCache();

         try
         {
            SerializedCache buf = Dao.readSerializedSchemaCache(_alias);

            if(buf != null)
            {
               _serializedCache = buf;
            }
         }
         catch(Exception e)
         {
            new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL).warning(_i18n.t("failed.load.serialized.cache.panel", e));
            new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_LOG).warning(_i18n.t("failed.load.serialized.cache"), e);
         }
      }

      return _serializedCache;
   }

   public void writeCache()
   {
      // We check null != _serializedCache here because it might be that no cache
      // was accessed at all and we don't want to try to read it now.
      if(null != _serializedCache && false == _serializedCache.empty())
      {
         Dao.writeSerializedSchemaCache(_alias, _serializedCache);
      }
   }
}
