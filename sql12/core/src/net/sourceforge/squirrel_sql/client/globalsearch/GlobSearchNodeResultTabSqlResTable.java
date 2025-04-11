package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeResultTabSqlResTable
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeResultTabSqlResTable.class);


   private ResultTabProvider _resultTab;

   public GlobSearchNodeResultTabSqlResTable(ResultTabProvider resultTab)
   {
      this._resultTab = resultTab;
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString("GlobSearchNodeResultTabSearchable.sql", _resultTab.getResultTab().getViewableSqlString());
   }
}
