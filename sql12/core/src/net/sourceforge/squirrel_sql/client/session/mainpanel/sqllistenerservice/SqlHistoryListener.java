package net.sourceforge.squirrel_sql.client.session.mainpanel.sqllistenerservice;

import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;

@FunctionalInterface
public interface SqlHistoryListener
{
   void newSqlHistoryItem(SQLHistoryItem sqlHistoryItem);
}
