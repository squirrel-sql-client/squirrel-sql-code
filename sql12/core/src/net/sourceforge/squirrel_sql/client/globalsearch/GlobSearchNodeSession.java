package net.sourceforge.squirrel_sql.client.globalsearch;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeSession
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeSession.class);

   private final ISession _session;
   private List<GlobSearchNodeSqlPanel> _globSearchNodeSQLPanels = new ArrayList<>();

   public GlobSearchNodeSession(ISession session)
   {
      this._session = session;
   }

   public ISession getSession()
   {
      return _session;
   }

   public void addGlobSearchNodeSQLPanel(GlobSearchNodeSqlPanel gsnSQLPanel)
   {
      _globSearchNodeSQLPanels.add(gsnSQLPanel);
   }

   public List<GlobSearchNodeSqlPanel> getGlobSearchNodeSqlPanels()
   {
      return _globSearchNodeSQLPanels;
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString("GlobSearchNodeSession.toString", _session.getSessionInternalFrame().getTitleWithoutFile());
   }
}
