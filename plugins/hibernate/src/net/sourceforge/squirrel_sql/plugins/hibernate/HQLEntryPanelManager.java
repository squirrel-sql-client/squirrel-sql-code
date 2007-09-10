package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcherFactory;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.completion.HQLCompleteCodeAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HQLEntryPanelManager extends EntryPanelManagerBase
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HQLEntryPanelManager.class);

   private HqlSyntaxHighlightTokenMatcherProxy _hqlSyntaxHighlightTokenMatcherProxy = new HqlSyntaxHighlightTokenMatcherProxy();
   private HibernatePluginResources _resources;
   private IHibernateConnectionProvider _connectionProvider;
   private ToolsPopupController _toolsPopupController;


   public HQLEntryPanelManager(ISession session, HibernatePluginResources resources, IHibernateConnectionProvider connectionProvider)
   {
      super(session);
      _connectionProvider = connectionProvider;

      init(createSyntaxHighlightTokenMatcherFactory());

      _resources = resources;

      initToolsPopUp();
      initCodeCompletion();


      // i18n[HQLEntryPanelManager,quoteHQL=Quote HQL]
      AbstractAction quoteHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,quoteHQL"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onQuoteHQL();
         }
      };
      quoteHql.putValue(Action.SHORT_DESCRIPTION, "Quote HQL");
      addToSQLEntryAreaMenu(quoteHql, "quote");

      // i18n[HQLEntryPanelManager,quoteHQLsb=Quote HQL sb]
      AbstractAction quoteSbHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,quoteHQLsb"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onQuoteHQLSb();
         }
      };
      quoteSbHql.putValue(Action.SHORT_DESCRIPTION, "Quote HQL as StringBuffer");
      addToSQLEntryAreaMenu(quoteSbHql, "quotesb");

      // i18n[HQLEntryPanelManager,unquoteHQL=Unquote HQL]
      AbstractAction unquoteHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,unquoteHQL"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onUnquoteHQL();
         }
      };
      unquoteHql.putValue(Action.SHORT_DESCRIPTION, "Unquote HQL");
      addToSQLEntryAreaMenu(unquoteHql, "unquote");
   }

   private void initToolsPopUp()
   {
      _toolsPopupController = new ToolsPopupController(getSession(), getEntryPanel());
      HQLToolsPopUpAction htp = new HQLToolsPopUpAction(_resources, _toolsPopupController, getSession().getApplication());
      JMenuItem item = addToSQLEntryAreaMenu(htp, null);
      _resources.configureMenuItem(htp, item);
      registerKeyboardAction(htp, _resources.getKeyStroke(htp));
   }


   private void initCodeCompletion()
   {
      HQLCompleteCodeAction hcca = new HQLCompleteCodeAction(getSession().getApplication(), _resources, this, _connectionProvider, _hqlSyntaxHighlightTokenMatcherProxy);
      JMenuItem item = addToSQLEntryAreaMenu(hcca, "complete");
      _resources.configureMenuItem(hcca, item);
      registerKeyboardAction(hcca, _resources.getKeyStroke(hcca));
   }


   private ISyntaxHighlightTokenMatcherFactory createSyntaxHighlightTokenMatcherFactory()
   {
      return new ISyntaxHighlightTokenMatcherFactory()
      {
         public ISyntaxHighlightTokenMatcher getSyntaxHighlightTokenMatcher(ISession sess, JEditorPane editorPane)
         {
            _hqlSyntaxHighlightTokenMatcherProxy.setEditorPane(editorPane);
            return _hqlSyntaxHighlightTokenMatcherProxy;
         }
      };

   }


   private void onUnquoteHQL()
   {
      EditExtrasAccessor.unquoteHQL(getEntryPanel(), getSession());
   }

   private void onQuoteHQLSb()
   {
      EditExtrasAccessor.quoteHQLSb(getEntryPanel(), getSession());
   }

   private void onQuoteHQL()
   {
      EditExtrasAccessor.quoteHQL(getEntryPanel(), getSession());
   }


   public JMenuItem addToSQLEntryAreaMenu(Action action, String toolsPopupSelectionString)
   {
      if(null != toolsPopupSelectionString)
      {
         _toolsPopupController.addAction(toolsPopupSelectionString, action);
      }

      return getEntryPanel().addToSQLEntryAreaMenu(action);
   }

   public void addToToolsPopUp(String selectionString, Action action)
   {
      throw new UnsupportedOperationException("NYI");
   }

   public void registerKeyboardAction(Action action, KeyStroke keyStroke)
   {
      JComponent comp = getEntryPanel().getTextComponent();
      comp.registerKeyboardAction(action, keyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
   }
}
