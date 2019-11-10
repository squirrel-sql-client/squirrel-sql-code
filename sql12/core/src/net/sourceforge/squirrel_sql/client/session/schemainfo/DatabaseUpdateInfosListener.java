package net.sourceforge.squirrel_sql.client.session.schemainfo;

@FunctionalInterface
public interface DatabaseUpdateInfosListener
{
   void databaseUpdated(DatabaseUpdateInfos databaseUpdateInfos);
}
