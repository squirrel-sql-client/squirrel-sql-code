package net.sourceforge.squirrel_sql.client.session.mainpanel;

@FunctionalInterface
public interface SqlHistoryListener
{
   void newSqlHistoryItem(SQLHistoryItem sqlHistoryItem);
}
