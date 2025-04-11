package net.sourceforge.squirrel_sql.client.globalsearch;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.SQLPanelApiInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeSqlPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeSqlPanel.class);

   private final SQLPanelApiInfo _sqlPanelApiInfo;

   private List<GlobSearchNodeResultTab> _globSearchNodeResultTabs = new ArrayList<>();

   public GlobSearchNodeSqlPanel(SQLPanelApiInfo sqlPanelApiInfo)
   {
      this._sqlPanelApiInfo = sqlPanelApiInfo;
   }

   public SQLPanelApiInfo getSqlPanelApiInfo()
   {
      return _sqlPanelApiInfo;
   }

   public void addGlobSearchNodeResultTab(GlobSearchNodeResultTab gsnResultTab)
   {
      _globSearchNodeResultTabs.add(gsnResultTab);
   }

   public List<GlobSearchNodeResultTab> getGlobSearchNodeResultTabs()
   {
      return _globSearchNodeResultTabs;
   }

   @Override
   public String toString()
   {
      if( null != _sqlPanelApiInfo.getParentSqlInternalFrame() )
      {
         return s_stringMgr.getString("GlobSearchNodeSqlPanel.sql.internal.frame", _sqlPanelApiInfo.getParentSqlInternalFrame().getTitleWithoutFile());
      }
      else if( null != _sqlPanelApiInfo.getParentAdditionalSQLTab() )
      {
         return s_stringMgr.getString("GlobSearchNodeSqlPanel.sql.additional.sql.tab", _sqlPanelApiInfo.getParentAdditionalSQLTab().getTitleWithoutFile());
      }
      else
      {
         return s_stringMgr.getString("GlobSearchNodeSqlPanel.session.main.sql.tab");
      }
   }
}
