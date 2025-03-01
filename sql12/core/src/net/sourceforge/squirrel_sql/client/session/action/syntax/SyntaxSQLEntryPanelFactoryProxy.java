package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.RSyntaxSQLEntryAreaFactory;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.client.session.defaultentry.DefaultSQLEntryPanelFactory;

import java.util.HashMap;


public class SyntaxSQLEntryPanelFactoryProxy implements ISQLEntryPanelFactory
{
   /** The original Squirrel SQL CLient factory for creating SQL entry panels. */
   private ISQLEntryPanelFactory _defaultSQLEntryPanelFactory = new DefaultSQLEntryPanelFactory();

   private RSyntaxSQLEntryAreaFactory _rsyntaxFactory;


   public SyntaxSQLEntryPanelFactoryProxy()
   {
      _rsyntaxFactory = new RSyntaxSQLEntryAreaFactory();
   }

   public void sessionEnding(ISession session)
   {
      _rsyntaxFactory.sessionEnding(session);
   }

   public ISQLEntryPanel createSQLEntryPanel(final ISession session, HashMap<String, Object> props)
   {
      if (session == null)
      {
         throw new IllegalArgumentException("Null ISession passed");
      }

      ISQLEntryPanel pnl = getPanel(session);


      ISQLEntryPanel newPnl;


      if (isUseRSyntaxTextArea())
      {
         newPnl = _rsyntaxFactory.createSQLEntryPanel(session, props);

         boolean replaceTabsBySpaces = Main.getApplication().getSyntaxManager().getSyntaxPreferences().isReplaceTabsBySpaces();
         ((SquirrelRSyntaxTextArea)newPnl.getTextComponent()).setTabsEmulated(replaceTabsBySpaces);
      }
      else
      {
         newPnl = _defaultSQLEntryPanelFactory.createSQLEntryPanel(session, props);
      }

      newPnl.getTextComponent().setTabSize(Main.getApplication().getSyntaxManager().getSyntaxPreferences().getTabLength());


      new ToolsPopupHandler().initToolsPopup(props, newPnl);

      new AutoCorrector(newPnl.getTextComponent());

      if(null == pnl || false == newPnl.getClass().equals(pnl.getClass()))
      {
         removePanel(session);
         saveSqlEntryPanel(session, newPnl);
      }

      return newPnl;
   }

   public boolean isUseRSyntaxTextArea()
   {
      return getPreferences().getUseRSyntaxTextArea();
   }


   private SyntaxPreferences getPreferences()
   {
      return Main.getApplication().getSyntaxManager().getSyntaxPreferences();
   }

   private void removePanel(ISession session)
   {
      session.setSqlEntryPanel(null);
   }

   private ISQLEntryPanel getPanel(ISession session)
   {
      return session.getSqlEntryPanel();
   }

   private void saveSqlEntryPanel(ISession session, ISQLEntryPanel sqlEntryPanel)
   {
      session.setSqlEntryPanel(sqlEntryPanel);
   }
}

