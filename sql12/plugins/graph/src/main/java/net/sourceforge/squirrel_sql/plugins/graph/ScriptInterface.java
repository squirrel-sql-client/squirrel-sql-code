package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

public interface ScriptInterface
{
   void scriptTablesToSQLEntryArea(ISession sess, ITableInfo[] tis);
}
