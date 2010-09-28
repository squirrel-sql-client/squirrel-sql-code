package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecuterPanel;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects.ObjectResultController;

import javax.swing.*;
import java.util.*;
import java.util.prefs.Preferences;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SQLPanelManager extends EntryPanelManagerBase
{
   private static final String PREF_KEY_APPEND_SQL = "SquirrelSQL.hibernate.sqlAppendSql";
   private static final String PREF_KEY_FORMAT_SQL = "SquirrelSQL.hibernate.sqlFormatSql";
   private static final String PREF_KEY_EXECUTE_SQL = "SquirrelSQL.hibernate.sqlExecuteSql";
   private static final String PREF_KEY_VIEW_OBJECTS = "SquirrelSQL.hibernate.objViewObjects";
   private static final String PREF_KEY_VIEW_LIMIT_OBJECT_COUNT = "SquirrelSQL.hibernate.limitObjectsCount";
   private static final String PREF_KEY_VIEW_LIMIT_OBJECT_COUNT_VAL = "SquirrelSQL.hibernate.limitObjectsCountVal";

   private HibernateSQLPanel _hibernateSQLPanel;
   private SQLResultExecuterPanel _resultExecuterPanel;
   private ObjectResultController _objectResultController;


   public SQLPanelManager(final ISession session, HibernatePluginResources resource)
   {
      super(session);
      init(null, null);

      _resultExecuterPanel = new SQLResultExecuterPanel(session);
      _objectResultController = new ObjectResultController(session, resource);
      _hibernateSQLPanel = new HibernateSQLPanel(super.getComponent(), _resultExecuterPanel, _objectResultController.getPanel());


      session.getApplication().getSessionManager().addSessionListener(
         new SessionAdapter()
         {

            public void sessionClosing(SessionEvent evt)
            {
               onSessionClosing();
               session.getApplication().getSessionManager().removeSessionListener(this);
            }
         }
      );

      _hibernateSQLPanel._btnFormatSql.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFormatSql();
         }
      });


      _hibernateSQLPanel._chkAppendSql.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_APPEND_SQL, false));
      _hibernateSQLPanel._chkAlwaysFormatSql.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_SQL, false));
      _hibernateSQLPanel._chkAlwaysExecuteSql.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_EXECUTE_SQL, false));
      _hibernateSQLPanel._chkAlwaysViewObjects.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_VIEW_OBJECTS, false));
      _hibernateSQLPanel._chkLimitObjectCount.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT, false));

      _hibernateSQLPanel._nbrLimitRows.setInt(Preferences.userRoot().getInt(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT_VAL, 100));
      _hibernateSQLPanel._nbrLimitRows.setEnabled(_hibernateSQLPanel._chkLimitObjectCount.isSelected());


      _hibernateSQLPanel._chkAlwaysViewObjects.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onViewObjectsChanged();
         }
      });
      onViewObjectsChanged();


      _hibernateSQLPanel._chkLimitObjectCount.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onLimitRowsChanged();
         }
      });
   }

   private void onViewObjectsChanged()
   {
      boolean b = _hibernateSQLPanel._chkAlwaysViewObjects.isSelected();
      _hibernateSQLPanel._chkLimitObjectCount.setEnabled(b);
      _hibernateSQLPanel._nbrLimitRows.setEnabled(b && _hibernateSQLPanel._chkLimitObjectCount.isSelected());
   }

   private void onLimitRowsChanged()
   {
      if(_hibernateSQLPanel._chkLimitObjectCount.isSelected())
      {
         LimitObjectCountDialog locc = new LimitObjectCountDialog(getSession().getApplication().getMainFrame());
         _hibernateSQLPanel._chkLimitObjectCount.setSelected(locc.check());

         if(locc.checkAndRemember())
         {
            Preferences.userRoot().putBoolean(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT, true);
         }
      }
      else
      {
         Preferences.userRoot().putBoolean(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT, false);
      }

     _hibernateSQLPanel._nbrLimitRows.setEnabled(_hibernateSQLPanel._chkLimitObjectCount.isSelected());
   }


   private void onFormatSql()
   {
      if (null != getEntryPanel().getText())
      {
         getEntryPanel().setText(format(getEntryPanel().getText()));
      }
   }

   private void onSessionClosing()
   {
      Preferences.userRoot().putBoolean(PREF_KEY_APPEND_SQL, _hibernateSQLPanel._chkAppendSql.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_SQL, _hibernateSQLPanel._chkAlwaysFormatSql.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_EXECUTE_SQL, _hibernateSQLPanel._chkAlwaysExecuteSql.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_VIEW_OBJECTS, _hibernateSQLPanel._chkAlwaysViewObjects.isSelected());

      // Omitted intentionally
      //Preferences.userRoot().putBoolean(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT, _hibernateSQLPanel._chkLimitObjectCount.isSelected());

      Preferences.userRoot().putInt(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT_VAL, _hibernateSQLPanel._nbrLimitRows.getInt());
   }


   public JComponent getComponent()
   {
      return _hibernateSQLPanel;
   }

   public void displaySqls(ArrayList<String> sqls)
   {
      String allSqls = createAllSqlsString(sqls);


      if (_hibernateSQLPanel._chkAlwaysExecuteSql.isSelected())
      {
         _hibernateSQLPanel._tabResult_code.setSelectedComponent(_resultExecuterPanel);
         displaySqlResult(allSqls);
      }
      else
      {
         _hibernateSQLPanel._tabResult_code.setSelectedComponent(super.getComponent());
      }
      displaySqlCode(allSqls);
   }

   public void displayObjects(HibernateConnection con, String hqlQuery)
   {
      if (_hibernateSQLPanel._chkAlwaysViewObjects.isSelected())
      {
         boolean limitObjectCount = _hibernateSQLPanel._chkLimitObjectCount.isSelected();
         int limitObjectCountVal = _hibernateSQLPanel._nbrLimitRows.getInt();

         _objectResultController.displayObjects(con, hqlQuery, limitObjectCount, limitObjectCountVal);
         _hibernateSQLPanel._tabResult_code.setSelectedComponent(_objectResultController.getPanel());
      }
   }


   private void displaySqlResult(String allSqls)
   {
      _resultExecuterPanel.executeSQL(allSqls);
   }

   private void displaySqlCode(String allSqls)
   {

      if (_hibernateSQLPanel._chkAlwaysFormatSql.isSelected())
      {
         allSqls = format(allSqls);
      }


      if (_hibernateSQLPanel._chkAppendSql.isSelected())
      {
         getEntryPanel().appendText(allSqls);

         scrollEntryPlanel(allSqls);

      }
      else
      {
         getEntryPanel().setText(allSqls, false);
      }
   }

   private String createAllSqlsString(ArrayList<String> sqls)
   {
      String allSqls = "";

      String sep = getSession().getQueryTokenizer().getSQLStatementSeparator();

      for (String sql : sqls)
      {
         allSqls += sql;

         if (1 < sep.length())
         {
            allSqls += "\n";
         }
         allSqls += (sep + "\n\n");
      }
      return allSqls;
   }

   private String format(String sqls)
   {
      CommentSpec[] commentSpecs =
         new CommentSpec[]
            {
               new CommentSpec("/*", "*/"),
               new CommentSpec("--", "\n")
            };

      String statementSep = getSession().getQueryTokenizer().getSQLStatementSeparator();

      CodeReformator cr = new CodeReformator(statementSep, commentSpecs);

      sqls = cr.reformat(sqls) + "\n";
      return sqls;
   }


   private void scrollEntryPlanel(String allSqls)
   {
      getEntryPanel().getTextComponent().setCaretPosition(getEntryPanel().getText().length() - allSqls.length());
      Point p = getEntryPanel().getTextComponent().getCaret().getMagicCaretPosition();
      if (p != null)
      {
         getEntryPanel().getTextComponent().scrollRectToVisible(new Rectangle(p.x, p.y, 1, 100));
      }
   }


   public boolean isDisplayObjects()
   {
      return _hibernateSQLPanel._chkAlwaysViewObjects.isSelected();  //To change body of created methods use File | Settings | File Templates.
   }
}
