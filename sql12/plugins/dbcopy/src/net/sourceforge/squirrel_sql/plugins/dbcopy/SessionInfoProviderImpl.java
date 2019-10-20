package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.List;

public class SessionInfoProviderImpl implements SessionInfoProvider
{
   private IObjectTreeAPI _copyDestobjectTreeAPI = null;

   private List<IDatabaseObjectInfo> _selectedDatabaseObjects = null;

   private IDatabaseObjectInfo _selectedDestDatabaseObject = null;

   private String _pasteToTableName;
   private String _whereClause;

   private ISession _copySourceSession = null;

   private final static ILogger s_log = LoggerController.createLogger(SessionInfoProviderImpl.class);


   public void setSourceDatabaseObjects(List<IDatabaseObjectInfo> dbObjList)
   {
      if (dbObjList != null)
      {
         _selectedDatabaseObjects = dbObjList;
         int sourceObjectCount = 0;
         if (s_log.isDebugEnabled())
         {
            for (IDatabaseObjectInfo info : dbObjList)
            {
               s_log.debug("setSelectedDatabaseObjects: IDatabaseObjectInfo[" + (sourceObjectCount++) + "]="
                     + info);
            }
         }
      }
   }


   /**
    * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getSourceSession()
    */
   public ISession getSourceSession()
   {
      return _copySourceSession;
   }

   /**
    * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#initCopy(net.sourceforge.squirrel_sql.client.session.ISession)
    */
   public void initCopy(ISession copySourceSession)
   {
      if (copySourceSession != null)
      {
         _copySourceSession = copySourceSession;


         _pasteToTableName = null;
         _whereClause = null;
         _selectedDestDatabaseObject = null;
      }
   }

   public ISession getCopySourceSession()
   {
      return _copySourceSession;
   }

   public void dispose()
   {
      _copySourceSession = null;
   }


   /**
    * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getSourceDatabaseObjects()
    */
   public List<IDatabaseObjectInfo> getSourceDatabaseObjects()
   {
      return _selectedDatabaseObjects;
   }

   /**
    * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getDestSession()
    */
   public ISession getDestSession()
   {
      return _copyDestobjectTreeAPI.getSession();
   }



   @Override
   public void setPasteToTableName(String pasteToTableName)
   {
      _pasteToTableName = pasteToTableName;
   }

   @Override
   public String getPasteToTableName()
   {
      return _pasteToTableName;
   }

   @Override
   public void setWhereClause(String whereClause)
   {
      _whereClause = whereClause;
   }

   @Override
   public String getWhereClause()
   {
      return _whereClause;
   }


   @Override
   public TableInfo getPasteToTableInfo(ISQLConnection destConn, String destSchema, String destCatalog)
   {
      if(null == _pasteToTableName)
      {
         return null;
      }

      if(1 != _selectedDatabaseObjects.size() || false == _selectedDatabaseObjects.get(0) instanceof TableInfo)
      {
         throw new IllegalStateException("Invalid paste table as state");
      }

      TableInfo ret = new TableInfo(destCatalog, destSchema, _pasteToTableName, "TABLE", null, destConn.getSQLMetaData());
      return ret;
   }

   @Override
   public boolean isCopiedFormDestinationSession()
   {
      return _copyDestobjectTreeAPI.equals(_copySourceSession);
   }

   public void setDestObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _copyDestobjectTreeAPI = objectTreeAPI;
   }

   @Override
   public IObjectTreeAPI getDestObjectTreeAPI()
   {
      return _copyDestobjectTreeAPI;
   }

   /**
    * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getDestDatabaseObject()
    */
   public IDatabaseObjectInfo getDestDatabaseObject()
   {
      return _selectedDestDatabaseObject;
   }

   /**
    * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#setDestDatabaseObject(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
    */
   public void setDestDatabaseObject(IDatabaseObjectInfo info)
   {
      _selectedDestDatabaseObject = info;
   }


}
