package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanelPosition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SQLTab extends BaseSQLTab
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLTab.class);


   public SQLTab(ISession session)
   {
      super(session);
   }

   @Override
   protected SQLPanel createSqlPanel()
   {
      return new SQLPanel(getSession(), SQLPanelPosition.MAIN_TAB_IN_SESSION_WINDOW);
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
