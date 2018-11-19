package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SQLTab extends BaseSQLTab
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLTab.class);


   public SQLTab(ISession session)
   {
      super(session);
   }

   /**
    * @see IMainPanelTab#getTitle()
    */
   public String getTitle()
   {
      return s_stringMgr.getString("SQLTab.title");
   }

   /**
    * @see IMainPanelTab#getHint()
    */
   public String getHint()
   {
      return s_stringMgr.getString("SQLTab.hint");
   }

}
