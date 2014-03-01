package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbcopy.commands.PasteTableCommand;

public class PasteTableUtil
{
   private final static ILogger log = LoggerController.createLogger(PasteTableUtil.class);
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PasteTableUtil.class);


   public static void excePasteTable(SessionInfoProvider sessionInfoProv, IApplication app1, String pasteToTableName)
   {
      ISession destSession = sessionInfoProv.getDestSession();
      IObjectTreeAPI api =
          destSession.getObjectTreeAPIOfActiveSessionWindow();
      if (api == null) {
          return;
      }
      IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
      if (dbObjs.length > 1) {
          sessionInfoProv.setDestDatabaseObject(null);
          //i18n[PasteTableAction.error.multischemapaste=The paste
          //operation may only be applied to one schema at a time]
          String msg =
             s_stringMgr.getString("PasteTableAction.error.multischemapaste");
          app1.showErrorDialog(msg);

          return;
      } else {
         // When the user pastes on a TABLE label which is located under a
         // schema/catalog, build the schema DatabaseObjectInfo.
         if (DatabaseObjectType.TABLE_TYPE_DBO.equals(dbObjs[0].getDatabaseObjectType())) {
            IDatabaseObjectInfo tableLabelInfo = dbObjs[0];
            ISQLConnection destCon = destSession.getSQLConnection();
            SQLDatabaseMetaData md = null;
            if (destCon != null) {
               md = destCon.getSQLMetaData();
            }
            IDatabaseObjectInfo schema =
               new DatabaseObjectInfo(null,
                                 tableLabelInfo.getSchemaName(),
                                 tableLabelInfo.getSchemaName(),
                                 DatabaseObjectType.SCHEMA,
                                 md);
            sessionInfoProv.setDestDatabaseObject(schema);
         } else {
            sessionInfoProv.setDestDatabaseObject(dbObjs[0]);
         }

         sessionInfoProv.setPasteToTableName(pasteToTableName);

      }

      try {
          IDatabaseObjectInfo info
                          = sessionInfoProv.getDestDatabaseObject();
          if (info == null || destSession == null) {
              return;
          }
          if (!checkSession(destSession, info, app1)) {
              return;
          }
      } catch (UserCancelledOperationException e) {
          return;
      }
      if (sessionInfoProv.getSourceSession() == null) {
          return;
      }
      if (!sourceDestSchemasDiffer()) {
          // TODO: tell the user that the selected destination schema is
          // the same as the source schema.
          //monitor.showMessageDialog(...)
          return;
      }
      new PasteTableCommand(sessionInfoProv).execute();
   }

   /**
    * This a work-around for the fact that some databases in SQuirreL show
    * "schemas" as catalogs (MySQL) while most others show them as "schemas".
    * If we restrict the Paste menu-item to schemas, then it won't appear in
    * the context menu in the MySQL object tree.  However, if add catalogs to
    * the list of database objects that the paste menu item appears in, then
    * we must be careful not to attempt the copy operation on databases where
    * schema != catalog.(Otherwise the copy operation will fail as the qualified
    * name will be [catalog].[tablename] instead of [schema].[tablename]
    *
    * @param app1
    * @param session
    * @param dbObjs
    *
    * @return true if it is ok to proceed with the copy operation; false otherwise.
    */
   private static boolean checkSession(ISession session, IDatabaseObjectInfo dbObj, IApplication app1)
       throws UserCancelledOperationException
   {
       if (session == null || dbObj == null) {
           return true;
       }
       String typeName = dbObj.getDatabaseObjectType().getName();

       log.debug("PasteTableAction.checkSession: dbObj type="+typeName+
                 " name="+dbObj.getSimpleName());

       HibernateDialect d =
           DialectFactory.getDialect(DialectFactory.DEST_TYPE,
                 session.getApplication().getMainFrame(),
                 session.getMetaData());
       if (!d.canPasteTo(dbObj)) {
           //i18n[PasteTableAction.error.destdbobj=The destination database
           //doesn't support copying tables into '{0}' objects.\n Please
           //select a schema to paste into.]
           String errmsg =
               s_stringMgr.getString("PasteTableAction.error.destdbobj",
                                     new Object[] { typeName });
           app1.showErrorDialog(errmsg);
           return false;
       }
       return true;
   }

   /**
    * Returns a boolean value indicating whether or not the source and
    * destination sessions refer to the same schema.
    *
    * @return
    */
   private static boolean sourceDestSchemasDiffer() {
       //ISession sourceSession = sessionInfoProv.getCopySourceSession();
       //ISession destSession = sessionInfoProv.getCopyDestSession();

       // TODO: check to be sure that the source and destination schemas are
       // different. Abort if they are the same and inform the user.

       return true;
   }
}
