package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.NetbeansSQLEntryAreaFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.oster.OsterSQLEntryAreaFactory;

import javax.swing.*;


public class SQLEntryPanelFactoryProxy implements ISQLEntryPanelFactory
{
   private NetbeansSQLEntryAreaFactory _netbeansFactory;
   private OsterSQLEntryAreaFactory _osterFactory;
   private SyntaxPugin _syntaxPugin;

   /** The original Squirrel SQL CLient factory for creating SQL entry panels. */
   private ISQLEntryPanelFactory _originalFactory;



   SQLEntryPanelFactoryProxy(SyntaxPugin syntaxPugin, ISQLEntryPanelFactory originalFactory)
   {
      _originalFactory = originalFactory;
      _netbeansFactory = new NetbeansSQLEntryAreaFactory(syntaxPugin);
      _osterFactory = new OsterSQLEntryAreaFactory(syntaxPugin);
      _syntaxPugin = syntaxPugin;
   }

   public void sessionEnding(ISession session)
   {
      _netbeansFactory.sessionEnding(session);
   }

   public ISQLEntryPanel createSQLEntryPanel(final ISession session)
   {
      if (session == null)
      {
         throw new IllegalArgumentException("Null ISession passed");
      }

      SyntaxPreferences prefs = getPreferences(session);
      ISQLEntryPanel pnl = getPanel(session);


      ISQLEntryPanel newPnl;


      if (prefs.getUseNetbeansTextControl())
      {
         newPnl = _netbeansFactory.createSQLEntryPanel(session);
      }
      else if (prefs.getUseOsterTextControl())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               session.getMessageHandler().showMessage(
                  "You are using the Oster editor. Please consider using the Netbeans editor. " +
                  "See menu File --> New Session Properties --> Syntax");
            }
         });

         newPnl = _osterFactory.createSQLEntryPanel(session);
      }
      else
      {
         newPnl = _originalFactory.createSQLEntryPanel(session);
      }

      if(null == pnl || false == newPnl.getClass().equals(pnl.getClass()))
      {
         removePanel(session);
         savePanel(session, newPnl);
      }

      return newPnl;
   }

   private SyntaxPreferences getPreferences(ISession session)
   {
      return (SyntaxPreferences)session.getPluginObject(_syntaxPugin,
         IConstants.ISessionKeys.PREFS);
   }

   private void removePanel(ISession session)
   {
      session.removePluginObject(_syntaxPugin, IConstants.ISessionKeys.SQL_ENTRY_CONTROL);
   }

   private ISQLEntryPanel getPanel(ISession session)
   {
      return (ISQLEntryPanel)session.getPluginObject(_syntaxPugin, IConstants.ISessionKeys.SQL_ENTRY_CONTROL);
   }

   private void savePanel(ISession session, ISQLEntryPanel pnl)
   {
      session.putPluginObject(_syntaxPugin, IConstants.ISessionKeys.SQL_ENTRY_CONTROL, pnl);
   }



}

