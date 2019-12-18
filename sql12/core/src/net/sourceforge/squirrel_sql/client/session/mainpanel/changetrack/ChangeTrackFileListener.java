package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

@FunctionalInterface
public interface ChangeTrackFileListener
{
   void changeTrackBaseChanged(String entireSqlScript);
}
