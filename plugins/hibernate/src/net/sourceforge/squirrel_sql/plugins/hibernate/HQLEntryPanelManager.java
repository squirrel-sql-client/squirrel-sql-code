package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcherFactory;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
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


   public HQLEntryPanelManager(ISession session, HibernatePluginResources resources, IHibernateConnectionProvider connectionProvider)
   {
      super(session);
      _connectionProvider = connectionProvider;

      init(createSyntaxHighlightTokenMatcherFactory());

      _resources = resources;

      // i18n[HQLEntryPanelManager,quoteHQL=Quote HQL]
      AbstractAction quoteHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,quoteHQL"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onQuoteHQL();
         }
      };
      addToSQLEntryAreaMenu(quoteHql);

      // i18n[HQLEntryPanelManager,quoteHQLsb=Quote HQL sb]
      AbstractAction quoteSbHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,quoteHQLsb"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onQuoteHQLSb();
         }
      };
      addToSQLEntryAreaMenu(quoteSbHql);

      // i18n[HQLEntryPanelManager,unquoteHQL=Unquote HQL]
      AbstractAction unquoteHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,unquoteHQL"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onUnquoteHQL();
         }
      };
      addToSQLEntryAreaMenu(unquoteHql);


      initCodeCompletion();
   }


   private void initCodeCompletion()
   {
      HQLCompleteCodeAction hcca = new HQLCompleteCodeAction(getSession().getApplication(), _resources, this, _connectionProvider, _hqlSyntaxHighlightTokenMatcherProxy);
      JMenuItem item = addToSQLEntryAreaMenu(hcca);
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


   public void addToSQLEntryAreaMenu(JMenu menu)
   {
      getEntryPanel().addToSQLEntryAreaMenu(menu);
   }

   public JMenuItem addToSQLEntryAreaMenu(Action action)
   {
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
