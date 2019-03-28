package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class HQLPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(HQLPanelController.class);

   private HibernateChannel _hibernateChannel;
   private ISession _sess;
   private HibernatePluginResources _resource;
   private HibernateConnection _con;
   private AbstractAction _runHQL;
   private HQLEntryPanelManager _hqlEntryPanelManager;

   public HQLPanelController(HibernateChannel hibernateChannel, ISession sess, HibernatePluginResources resource, TitleFilePathHandler titleFileHandler)
   {
      _hibernateChannel = hibernateChannel;
      _sess = sess;
      _resource = resource;

      _hqlEntryPanelManager = new HQLEntryPanelManager(_sess, resource, hibernateChannel, titleFileHandler);

   }

   void initActions()
   {
      _runHQL = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRunHQL();
         }
      };


      _runHQL.putValue(AbstractAction.SMALL_ICON,  _resource.getIcon(HibernatePluginResources.IKeys.RUN_IMAGE));

      // i18n[hibernate.hqlToSqlLong=Run HQL]
      _runHQL.putValue(AbstractAction.NAME,  s_stringMgr.getString("hibernate.hqlToSqlLong"));

      // i18n[hibernate.hqlToSqlShort=Run HQL (ctrl + enter)]
      _runHQL.putValue(AbstractAction.SHORT_DESCRIPTION,  s_stringMgr.getString("hibernate.hqlToSqlShort"));


      _runHQL.setEnabled(false);

      _hibernateChannel.addToToolbar(_runHQL);

      KeyStroke ctrlEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK);
      _hqlEntryPanelManager.registerKeyboardAction(_runHQL, ctrlEnter);
   }


   private void onRunHQL()
   {
      if (false == _runHQL.isEnabled())
      {
         return;
      }

      String hql = _hqlEntryPanelManager.getEntryPanel().getSQLToBeExecuted();

      if (null == hql || 0 == hql.trim().length())
      {
         return;
      }

      final IQueryTokenizer queryTokenizer = _sess.getQueryTokenizer();
      final String statementSeparator = queryTokenizer.getSQLStatementSeparator();
      final String startOfLineComment = queryTokenizer.getLineCommentBegin();
      QueryTokenizer qt = new QueryTokenizer(statementSeparator, startOfLineComment, true);
      qt.setScriptToTokenize(hql);


      while (qt.hasQuery())
      {
         String hqlQuery = qt.nextQuery().getQuery();
         _hibernateChannel.displayObjects(_con, hqlQuery);
      }
   }


   public void setConnection(HibernateConnection con)
   {
      _con = con;

      if(null == _con)
      {
         _runHQL.setEnabled(false);
      }
      else
      {
         _runHQL.setEnabled(true);
      }
   }

   public JComponent getComponent()
   {
      return _hqlEntryPanelManager.getComponent();
   }

   public void requestFocus()
   {
      _hqlEntryPanelManager.requestFocus();
   }

   public IFileEditorAPI getFileEditorAPIOrNull()
   {
      return _hqlEntryPanelManager;
   }
}
