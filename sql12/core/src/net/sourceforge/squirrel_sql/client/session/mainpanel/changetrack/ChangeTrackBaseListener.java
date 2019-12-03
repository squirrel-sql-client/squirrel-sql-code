package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

@FunctionalInterface
public interface ChangeTrackBaseListener
{
   void changeTrackBaseChanged(String entireSqlScript);
}
