package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

public interface ChangeTrackActionUpdateListener
{
   void activeSqlPanelApiChanged(ChangeTrackTypeEnum newSqlPanelAPIsChangeTrackType);

   void externallySetChangeTrackType(ChangeTrackTypeEnum type);
}
