package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeResultTab
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeResultTab.class);

   private final ResultTabProvider _resultTabProvider;

   private GlobSearchNodeResultTabSqlResTable _globSearchNodeResultTabSqlResTable;
   private GlobSearchNodeResultDetailDisplay _globSearchNodeResultDetailDisplay;

   public GlobSearchNodeResultTab(ResultTabProvider resultTabProvider)
   {
      this._resultTabProvider = resultTabProvider;
   }

   public ResultTabProvider getResultTabProvider()
   {
      return _resultTabProvider;
   }

   public void setResultTab(GlobSearchNodeResultTabSqlResTable gsnResultTabSqlResTable)
   {
      _globSearchNodeResultTabSqlResTable = gsnResultTabSqlResTable;
   }

   public void setDetailDisplay(GlobSearchNodeResultDetailDisplay globSearchNodeResultDetailDisplay)
   {
      _globSearchNodeResultDetailDisplay = globSearchNodeResultDetailDisplay;
   }

   public GlobSearchNodeResultTabSqlResTable getGlobSearchNodeResultTabSqlResTable()
   {
      return _globSearchNodeResultTabSqlResTable;
   }

   public GlobSearchNodeResultDetailDisplay getGlobSearchNodeResultDetailDisplay()
   {
      return _globSearchNodeResultDetailDisplay;
   }

   @Override
   public String toString()
   {
      return _globSearchNodeResultTabSqlResTable.toString();
   }
}
