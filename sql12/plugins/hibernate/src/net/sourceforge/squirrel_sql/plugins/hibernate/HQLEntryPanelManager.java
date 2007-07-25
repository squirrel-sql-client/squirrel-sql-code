package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcherFactory;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SquirrelDefaultUndoManager;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.Properties;
import java.util.HashMap;
import java.awt.event.ActionEvent;

public class HQLEntryPanelManager extends EntryPanelManagerBase implements IHqlEntryPanelManager
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HQLEntryPanelManager.class);


   public HQLEntryPanelManager(ISession session, ISyntaxHighlightTokenMatcherFactory syntaxHighlightTokenMatcherFactory)
   {
      super(session, syntaxHighlightTokenMatcherFactory);

      // i18n[HQLEntryPanelManager,quoteHQL=Quote HQL]
      AbstractAction quoteHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,quoteHQL"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onQuoteHQL();
         }
      };
      getEntryPanel().addToSQLEntryAreaMenu(quoteHql);

      // i18n[HQLEntryPanelManager,quoteHQLsb=Quote HQL sb]
      AbstractAction quoteSbHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,quoteHQLsb"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onQuoteHQLSb();
         }
      };
      getEntryPanel().addToSQLEntryAreaMenu(quoteSbHql);

      // i18n[HQLEntryPanelManager,unquoteHQL=Unquote HQL]
      AbstractAction unquoteHql = new AbstractAction(s_stringMgr.getString("HQLEntryPanelManager,unquoteHQL"))
      {
         public void actionPerformed(ActionEvent e)
         {
            onUnquoteHQL();
         }
      };
      getEntryPanel().addToSQLEntryAreaMenu(unquoteHql);



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

   public void addKeystrokeListener(KeyStroke keyStroke, AbstractAction action)
   {
      getEntryPanel().getTextComponent().getKeymap().addActionForKeyStroke(keyStroke, action);
   }
}
