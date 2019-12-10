package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

@FunctionalInterface
public interface SQLPanelApiChangedListener
{
   void activeSqlPanelApiChanged(ChangeTrackTypeEnum newSqlPanelAPIsChangeTrackType);
}
