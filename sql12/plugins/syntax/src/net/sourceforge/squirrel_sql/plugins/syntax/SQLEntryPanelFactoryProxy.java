package net.sourceforge.squirrel_sql.plugins.syntax;

import java.util.HashMap;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupAccessor;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.NetbeansSQLEntryAreaFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.FindAction;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.ReplaceAction;
import net.sourceforge.squirrel_sql.plugins.syntax.oster.OsterSQLEntryAreaFactory;


public class SQLEntryPanelFactoryProxy implements ISQLEntryPanelFactory
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SQLEntryPanelFactoryProxy.class);


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

   public ISQLEntryPanel createSQLEntryPanel(final ISession session, HashMap<String, Object> props)
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
         newPnl = _netbeansFactory.createSQLEntryPanel(session, props);
      }
      else if (prefs.getUseOsterTextControl())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               session.showMessage(
						// i18n[syntax.osterWarning=You are using the Oster editor. Please consider using the Netbeans editor. See menu File --> New Session Properties --> Syntax]
						s_stringMgr.getString("syntax.osterWarning"));
            }
         });

         newPnl = _osterFactory.createSQLEntryPanel(session);
      }
      else
      {
         newPnl = _originalFactory.createSQLEntryPanel(session, props);
      }

      new ToolsPopupHandler(_syntaxPugin).initToolsPopup(props, newPnl);

      new AutoCorrector(newPnl.getTextComponent(), _syntaxPugin);

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

